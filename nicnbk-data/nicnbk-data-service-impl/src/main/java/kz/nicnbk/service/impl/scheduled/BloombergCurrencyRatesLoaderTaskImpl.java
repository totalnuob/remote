package kz.nicnbk.service.impl.scheduled;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.bloomberg.BloombergJSONResponseDto;
import kz.nicnbk.service.dto.bloomberg.BloombergJSONSecurityDataDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.impl.bloomberg.service.BloombergServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BloombergCurrencyRatesLoaderTaskImpl {

    private static final Logger logger = LoggerFactory.getLogger(BloombergCurrencyRatesLoaderTaskImpl.class);
    public static final String JOBNAME = "SYSTEM_USER";

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Scheduled(cron = "0 0 7 ? * MON-FRI") //Every weekday, at 7:00 am
    public void loadCurrencyRates() {
        String url = "http://10.10.165.123:8080/bloomberg/currencyRates3";
        String authStr = "unic:qwerty123";
        BloombergJSONResponseDto result = new BloombergJSONResponseDto();
        ResponseEntity<String> responseEntity = (new RestTemplate()).getForEntity(url, String.class);
        String response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, new TypeReference<BloombergJSONResponseDto>() {});

            CurrencyRatesDto currencyRatesDto = new CurrencyRatesDto();
            BloombergJSONSecurityDataDto securityData = new BloombergJSONSecurityDataDto();
            for (int i = 0; i < result.getBloombergJSONSecurityDataDtoList().size(); i++) {
                if (result.getBloombergJSONSecurityDataDtoList().get(i).getSecurity().equals("USDKZT BGN Curncy")) {
                    securityData = result.getBloombergJSONSecurityDataDtoList().get(i);
                }
            }

            String sDate = securityData.getCurrencyFieldJSONDataDtoList().get(0).getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = formatter.parse(sDate);
            currencyRatesDto.setDate(date);

            Double value = Double.valueOf(securityData.getCurrencyFieldJSONDataDtoList().get(0).getValue());
            currencyRatesDto.setValue(value);
            currencyRatesDto.setCurrency(new BaseDictionaryDto(CurrencyLookup.USD.getCode(), null, null, null));

            EntitySaveResponseDto saveResponseDto = this.currencyRatesService.save(currencyRatesDto, JOBNAME);
            if(saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
                logger.info("Successfully saved Currency rate USD (from Bloomberg): " + currencyRatesDto.getValue());
            }else{
                logger.error("Error saving parsed Currency rate USD (from Bloomberg)");
            }
        } catch (Exception e) {
            logger.error("Error parsing Currency Rate from Bloomberg (with exception" + e);
        }
    }

    @Scheduled(cron = "0 0 7 ? * MON-FRI") //Every weekday, at 7:00 am
    public void loadBenchmarks() {
        String url = "http://10.10.165.123:8080/bloomberg/benchmark";
        String authStr = "unic:qwerty123";
        BloombergJSONResponseDto result = new BloombergJSONResponseDto();
        ResponseEntity<String> responseEntity = (new RestTemplate()).getForEntity(url, String.class);
        String response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, new TypeReference<BloombergJSONResponseDto>() {});

            CurrencyRatesDto currencyRatesDto = new CurrencyRatesDto();
            BenchmarkValueDto benchmarkValueDto = new BenchmarkValueDto();
            BloombergJSONSecurityDataDto securityData = new BloombergJSONSecurityDataDto();
            for (int i = 0; i < result.getBloombergJSONSecurityDataDtoList().size(); i++) {
                if (result.getBloombergJSONSecurityDataDtoList().get(i).getSecurity().equals("USDKZT BGN Curncy")) {
                    securityData = result.getBloombergJSONSecurityDataDtoList().get(i);
                }
            }

            BloombergJSONSecurityDataDto spxSecurityData = new BloombergJSONSecurityDataDto();
            BloombergJSONSecurityDataDto hfriSecurityData = new BloombergJSONSecurityDataDto();
            BloombergJSONSecurityDataDto tbillsSecurityData = new BloombergJSONSecurityDataDto();
            for(int i = 0; i < result.getBloombergJSONSecurityDataDtoList().size(); i++) {
                if (result.getBloombergJSONSecurityDataDtoList().get(i).getSecurity().equals("SPX Index")) {
                    spxSecurityData = result.getBloombergJSONSecurityDataDtoList().get(i);
                } else if (result.getBloombergJSONSecurityDataDtoList().get(i).getSecurity().equals("HFRIFOF Index")) {
                    hfriSecurityData = result.getBloombergJSONSecurityDataDtoList().get(i);
                } else {
                    tbillsSecurityData = result.getBloombergJSONSecurityDataDtoList().get(i);
                }
            }

            String sDate = securityData.getCurrencyFieldJSONDataDtoList().get(0).getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = formatter.parse(sDate);
            currencyRatesDto.setDate(date);

            Double value = Double.valueOf(securityData.getCurrencyFieldJSONDataDtoList().get(0).getValue());
            currencyRatesDto.setValue(value);
            currencyRatesDto.setCurrency(new BaseDictionaryDto(CurrencyLookup.USD.getCode(), null, null, null));

            EntitySaveResponseDto saveResponseDto = this.currencyRatesService.save(currencyRatesDto, JOBNAME);
            if(saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
                logger.info("Successfully saved Currency rate USD (from Bloomberg): " + currencyRatesDto.getValue());
            }else{
                logger.error("Error saving parsed Currency rate USD (from Bloomberg)");
            }
        } catch (Exception e) {
            logger.error("Error parsing Currency Rate from Bloomberg (with exception" + e);
        }
    }
}
