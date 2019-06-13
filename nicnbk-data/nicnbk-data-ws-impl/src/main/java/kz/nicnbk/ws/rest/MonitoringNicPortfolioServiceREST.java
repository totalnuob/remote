package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.dto.monitoring.NicPortfolioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Pak on 13.06.2019.
 */

@RestController
@RequestMapping("/monitoring/portfolio")
public class MonitoringNicPortfolioServiceREST extends CommonServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringNicPortfolioServiceREST.class);

    @Autowired
    private NicPortfolioService service;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity get(){
        List<NicPortfolioDto> nicPortfolioDtoList = this.service.get();
        if (nicPortfolioDtoList != null) {
            return new ResponseEntity<>(nicPortfolioDtoList, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
