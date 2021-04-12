package kz.nicnbk.service.impl.scheduled;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.bloomberg.ResponseDto;
import kz.nicnbk.service.dto.bloomberg.SecurityDataDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class BloombergCurrencyRatesLoaderTaskImpl {

    private static final Logger logger = LoggerFactory.getLogger(BloombergCurrencyRatesLoaderTaskImpl.class);
    public static final String JOBNAME = "SYSTEM_USER";

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    private BenchmarkService benchmarkService;

    //@Scheduled(cron = "0 0 7 ? * MON-FRI") //Every weekday, at 7:00 am
    public void loadCurrencyRates() {
        String url = "http://10.10.165.123:8080/bloomberg/currencyRates";
        String authStr = "unic:qwerty123";
        ResponseDto result = new ResponseDto();
        ResponseEntity<String> responseEntity = (new RestTemplate()).getForEntity(url, String.class);
        String response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, new TypeReference<ResponseDto>() {});

            CurrencyRatesDto currencyRatesDto = new CurrencyRatesDto();
            SecurityDataDto securityData = new SecurityDataDto();
            for (int i = 0; i < result.getSecurityDataDtoList().size(); i++) {
                if (result.getSecurityDataDtoList().get(i).getSecurity().equals("USDKZT BGN Curncy")) {
                    securityData = result.getSecurityDataDtoList().get(i);
                }
            }

            String sDate = securityData.getFieldDataDtoList().get(0).getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = formatter.parse(sDate);
            currencyRatesDto.setDate(date);

            Double value = Double.valueOf(securityData.getFieldDataDtoList().get(0).getValue());
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

    //@Scheduled(cron = "0 0 7 ? * MON-FRI") //Every weekday, at 7:00 am
    public void loadBenchmarks() {
        String url = "http://10.10.165.123:8080/bloomberg/benchmark";
        String authStr = "unic:qwerty123";
        ResponseDto result = new ResponseDto();
        ResponseEntity<String> responseEntity = (new RestTemplate()).getForEntity(url, String.class);
        String response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, new TypeReference<ResponseDto>() {});

            SecurityDataDto spxSecurityData = initializeDto(result);
            SecurityDataDto hfriSecurityData = initializeDto(result);
            SecurityDataDto tbillsSecurityData = initializeDto(result);
            //persist(result, spxSecurityData, hfriSecurityData, tbillsSecurityData);
        } catch (Exception e) {
            logger.error("Error parsing Currency Rate from Bloomberg (with exception" + e);
        }
    }

    private SecurityDataDto initializeDto(ResponseDto response) {
        for(int i = 0; i < response.getSecurityDataDtoList().size(); i++) {
            switch (response.getSecurityDataDtoList().get(i).getSecurity()) {
                case "SPX Index":
                    return response.getSecurityDataDtoList().get(i);
                case "HFRIFOF Index":
                    return response.getSecurityDataDtoList().get(i);
                case "T-Bills Index":
                    return response.getSecurityDataDtoList().get(i);
            }
        }
        return null;
    }

//    private void persist(ResponseDto response, SecurityDataDto spxDto,
//                         SecurityDataDto hfriDto, SecurityDataDto tbillsDto) throws Exception{
//        BenchmarkValueDto spx = new BenchmarkValueDto();
//        BenchmarkValueDto hfri = new BenchmarkValueDto();
//        BenchmarkValueDto tbills = new BenchmarkValueDto();
//
//        String sDate = response.getSecurityDataDtoList().get(0).
//                getFieldDataDtoList().get(0).getDate();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        Date date = formatter.parse(sDate);
//        spx.setDate(date);
//        hfri.setDate(date);
//        tbills.setDate(date);
//
//        for (int i = 0; i < response.getSecurityDataDtoList().size(); i++) {
//            switch (response.getSecurityDataDtoList().get(i).getSecurity()) {
//                case "SPX Index": {
//                    Double value = Double.valueOf(spxDto.getFieldDataDtoList().get(0).getValue());
//                    spx.setReturnValue(value);
//                    spx.setBenchmark(new BaseDictionaryDto(BenchmarkLookup.SNP_500_SPX.getCode(), null, null, null));
//                    EntitySaveResponseDto saveResponseDto = this.benchmarkService.save(spx, JOBNAME);
//                    if(saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
//                        logger.info("Successfully saved SPX values (from Bloomberg): " + spx.getReturnValue());
//                    }else{
//                        logger.error("Error saving parsed SPX values (from Bloomberg)");
//                    }
//                    break;
//                }
//                case "HFRIFOF Index": {
//                    Double value = Double.valueOf(hfriDto.getFieldDataDtoList().get(0).getValue());
//                    hfri.setReturnValue(value);
//                    hfri.setBenchmark(new BaseDictionaryDto(BenchmarkLookup.HFRI.getCode(), null, null, null));
//                    EntitySaveResponseDto saveResponseDto = this.benchmarkService.save(hfri, JOBNAME);
//                    if(saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
//                        logger.info("Successfully saved HFRI values (from Bloomberg): " + hfri.getReturnValue());
//                    }else{
//                        logger.error("Error saving parsed HFRI values (from Bloomberg)");
//                    }
//                    break;
//                }
//                case "T-Bills Index": {
//                    Double value = Double.valueOf(tbillsDto.getFieldDataDtoList().get(0).getValue());
//                    tbills.setReturnValue(value);
//                    tbills.setBenchmark(new BaseDictionaryDto(BenchmarkLookup.T_BILLS.getCode(), null, null, null));
//                    EntitySaveResponseDto saveResponseDto = this.benchmarkService.save(tbills, JOBNAME);
//                    if(saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
//                        logger.info("Successfully saved T-Bills values (from Bloomberg): " + tbills.getReturnValue());
//                    }else{
//                        logger.error("Error saving parsed T-Bills values (from Bloomberg)");
//                    }
//                    break;
//                }
//            }
//        }
//    }
}
