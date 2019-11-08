package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.version.VersionService;
import kz.nicnbk.service.dto.version.VersionResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private VersionService versionService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity versionGetLatest() {
        VersionResultDto resultDto = this.versionService.get();

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
