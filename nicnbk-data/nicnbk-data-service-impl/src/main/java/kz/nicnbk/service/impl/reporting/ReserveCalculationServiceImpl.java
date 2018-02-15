package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.repo.api.reporting.ReserveCalculationRepository;
import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.reporting.privateequity.ReserveCalculationService;
import kz.nicnbk.service.converter.reporting.ReserveCalculationConverter;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.reporting.ReserveCalculationDto;
import kz.nicnbk.service.impl.reporting.lookup.ReserveCalculationsExpenseTypeLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public List<ReserveCalculationDto> getAllReserveCalculations(){
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


    /**
     * Returns reserve calculation records for the specified month.
     * E.g. if date is 31.08.2018, then returns records with date
     * from 01.08.2018 to 31.08.2018.
     *
     * @param date - date
     * @return - reserve calculation records
     */
    //@Override
    private List<ReserveCalculationDto> getReserveCalculationsForMonth(String code, Date date) {
        Date fromDate = DateUtils.getFirstDayOfCurrentMonth(date);
        Date toDate = DateUtils.getFirstDayOfNextMonth(date);
        List<ReserveCalculation> entities = this.reserveCalculationRepository.getEntitiesByExpenseTypeBetweenDates(
                code, fromDate, toDate);

        List<ReserveCalculationDto> records = this.reserveCalculationConverter.disassembleList(entities);

        for(ReserveCalculationDto record: records) {
            // set currency rate
            Date nextDay = DateUtils.getNextDay(record.getDate());
            CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
            if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                // TODO: error message
                logger.error("No currency rate for date '" + nextDay + "', currency='USD'");
                //return null;
            } else {
                record.setCurrencyRate(currencyRatesDto.getValue());
            }

            // set amount kzt
            if (record.getAmount() != null && currencyRatesDto != null && currencyRatesDto.getValue() != null) {
                record.setAmountKZT(MathUtils.multiply(currencyRatesDto.getValue().doubleValue(), record.getAmount()));
            }
        }

        return records;
    }

    @Override
    public Double getReserveCalculationSumKZTForMonth(String code, Date date) {
        List<ReserveCalculationDto> records = getReserveCalculationsForMonth(code, date);
        Double sum = 0.0;
        if(records != null){
            for(ReserveCalculationDto record: records){
                if(record.getCurrencyRate() != null) {
                    sum = MathUtils.add(sum, record.getAmountKZT());
                }else{
                    String errorMessage = "Reserve Calculations sum: one of records has no currency rate. Date '" + DateUtils.getDateFormatted(record.getDate()) + "'";
                    logger.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            }
        }
        return sum;
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
                    logger.error("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'" );
                    throw new IllegalStateException("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'" );
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
}
