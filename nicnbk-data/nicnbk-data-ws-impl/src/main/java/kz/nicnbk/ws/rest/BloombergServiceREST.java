package kz.nicnbk.ws.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import kz.nicnbk.service.dto.bloomberg.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping({"/bloomberg"})
public class BloombergServiceREST {
    private static final Logger logger = LoggerFactory.getLogger(BloombergServiceREST.class);
//    @Autowired
//    private BloombergService bloombergService;

    @RequestMapping(
            path = {"/currencyRates"},
            method = {RequestMethod.GET}
    )
    public ResponseDto getHistoricalData3() {
        String url = "http://10.10.165.123:8080/bloomberg/currencyRates";
        String authStr = "unic:qwerty123";
        ResponseDto result = new ResponseDto();
        ResponseEntity<String> responseEntity = (new RestTemplate()).getForEntity(url, String.class);
        String response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, new TypeReference<ResponseDto>() {});
        } catch (Exception e) {
            logger.error("Error working with JSON");
        }

        return result;
    }

    @RequestMapping(
            path = {"/benchmark"},
            method = {RequestMethod.GET}
    )
    public ResponseDto getHistoricalData4() {
        String url = "http://10.10.165.123:8080/bloomberg/benchmark";
        String authStr = "unic:qwerty123";
        ResponseDto result = new ResponseDto();
        ResponseEntity<String> responseEntity = (new RestTemplate()).getForEntity(url, String.class);
        String response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, new TypeReference<ResponseDto>() {});
        } catch (Exception e) {
            logger.error("Error working with JSON");
        }

        return result;
    }

    public BloombergServiceREST() {
    }

    @RequestMapping(
            path = {"/currencyRates2"},
            method = {RequestMethod.GET}
    )
    public List<kz.nicnbk.service.dto.common.ResponseDto> getHistoricalData2() {
        String url = "http://10.10.165.123:8080/bloomberg/currencyRates3";
        String authStr = "unic:qwerty123";
        List<kz.nicnbk.service.dto.common.ResponseDto> result = new ArrayList<>();
        ResponseEntity<String> responseEntity = (new RestTemplate()).getForEntity(url, String.class);
        String response = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, new TypeReference<List<kz.nicnbk.service.dto.common.ResponseDto>>() {});
        } catch (Exception e) {
            logger.error("Error working with JSON");
        }

        return result;
    }

}
