package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.NicPortfolioResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Created by Pak on 13.06.2019.
 */

@RestController
@RequestMapping("/monitoring/portfolio")
public class MonitoringNicPortfolioServiceREST extends CommonServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringNicPortfolioServiceREST.class);

    @Autowired
    private NicPortfolioService service;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity get() {

        NicPortfolioResultDto resultDto = this.service.get();

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity upload(@RequestParam(value = "file", required = false) MultipartFile[] files) {

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        NicPortfolioResultDto resultDto = this.service.upload(filesDtoSet);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
