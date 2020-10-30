package kz.nicnbk.ws.rest;

import kz.nicnbk.service.impl.bloomberg.service.BloombergService;
import kz.nicnbk.service.impl.m2s2.MeetingMemoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@RestController
@RequestMapping("/bloomberg")
public class BloombergServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(MeetingMemoServiceImpl.class);

    @Autowired
    private BloombergService bloombergService;

    @RequestMapping(method = RequestMethod.GET)
    public String queryForMessage() {
        String url = "http://10.10.165.123:8080/bloomberg";
        String authStr = "unic:qwerty123";
//        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Basic " + base64Creds);
//        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<String> responseEntity = new RestTemplate().
                getForEntity(url, String.class);
        return responseEntity.getBody();
    }
}
