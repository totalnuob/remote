package kz.nicnbk.ws.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Pak on 08.11.2019.
 */

@RestController
@RequestMapping("/version")
public class VersionServiceREST extends CommonServiceREST {

    @RequestMapping(value = "/getLatest", method = RequestMethod.GET)
    public ResponseEntity versionGetLatest() {
        return null;
    }
}
