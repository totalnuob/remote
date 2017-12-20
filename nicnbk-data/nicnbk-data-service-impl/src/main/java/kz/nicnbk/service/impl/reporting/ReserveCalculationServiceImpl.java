package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.repo.api.reporting.ReserveCalculationRepository;
import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.reporting.privateequity.ReserveCalculationService;
import kz.nicnbk.service.converter.reporting.ReserveCalculationConverter;
import kz.nicnbk.service.dto.common.CurrencyRatesDto;
import kz.nicnbk.service.dto.reporting.ReserveCalculationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by magzumov on 30.11.2017.
 */

@Service
public class ReserveCalculationServiceImpl implements ReserveCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(ReserveCalculationServiceImpl.class);

    @Autowired
    ReserveCalculationRepository reserveCalculationRepository;

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    ReserveCalculationConverter reserveCalculationConverter;

    @Override
    public List<ReserveCalculationDto> getReserveCalculations(){
        List<ReserveCalculationDto> records = new ArrayList<>();
        try {
            Iterator<ReserveCalculation> entitiesIterator = this.reserveCalculationRepository.findAll(new Sort(Sort.Direction.ASC, "date", "id")).iterator();
            if (entitiesIterator != null) {
                while (entitiesIterator.hasNext()) {
                    ReserveCalculation entity = entitiesIterator.next();
                    ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);

                    // set currency rate
                    Date nextDay = DateUtils.getNextDay(dto.getDate());
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
                    if(currencyRatesDto == null || currencyRatesDto.getValue() == null){
                        // TODO: error message
                        logger.error("No currency rate for date '" + nextDay + "', currency='USD'" );
                        //return null;
                    }else {
                        dto.setCurrencyRate(currencyRatesDto.getValue());
                    }

                    // set amount kzt
                    if(dto.getAmount() != null && currencyRatesDto != null && currencyRatesDto.getValue() != null) {
                        dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    }

                    records.add(dto);
                }
            }
        }catch (Exception ex){
            logger.error("Error loading reserve calculations", ex);
            return null;
        }
        return records;
    }

    @Override
    public boolean save(List<ReserveCalculationDto> records) {
        try {
            if (records != null) {
                for (ReserveCalculationDto dto : records) {
                    List<ReserveCalculation> entities = this.reserveCalculationConverter.assembleList(records);
                    this.reserveCalculationRepository.save(entities);
                }
            }
        }catch (Exception ex){
            logger.error("Error saving reserve calculation records", ex);
            return false;
        }

        return true;
    }

    @Override
    public List<ReserveCalculationDto> getReserveCalculationsByExpenseType(String code) {
        List<ReserveCalculationDto> records = new ArrayList<>();
        List<ReserveCalculation> entities = this.reserveCalculationRepository.getEntitiesByExpenseType(code);
        if(entities != null){
            for(ReserveCalculation entity: entities){
                ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);

                // set currency rate
                Date nextDay = DateUtils.getNextDay(dto.getDate());
                CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
                if(currencyRatesDto == null || currencyRatesDto.getValue() == null){
                    // TODO: error message
                    logger.error("No currency rate for date '" + nextDay + "', currency='USD'" );
                    return null;
                }
                dto.setCurrencyRate(currencyRatesDto.getValue());

                // set amount kzt
                if(dto.getAmount() != null) {
                    dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }

                records.add(dto);
            }
        }
        return records;
    }

    private void load(){
        String csvFile = "C:/Users/magzumov/Desktop/123.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] rates = line.split(cvsSplitBy);
                Date date = null;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    date = simpleDateFormat.parse(rates[0]);
                } catch (ParseException e) {
                    //e.printStackTrace();
                }

                Double value = Double.parseDouble(rates[1].replace(",", "."));

                CurrencyRatesDto ratesDto = new CurrencyRatesDto();
                ratesDto.setCurrency(new BaseDictionaryDto("USD", null, null, null));
                ratesDto.setValue(value);
                ratesDto.setDate(date);

                this.currencyRatesService.save(ratesDto);
                count++;

            }
            System.out.println(count);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
