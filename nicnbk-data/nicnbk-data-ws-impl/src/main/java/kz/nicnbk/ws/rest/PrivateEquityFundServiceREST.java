package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.dto.pe.PEFundDto;
import kz.nicnbk.service.dto.pe.PESearchParams;
import kz.nicnbk.ws.model.EntitySaveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by zhambyl on 16-Nov-16.
 */
@RestController
@RequestMapping("/pe/fund")
public class PrivateEquityFundServiceREST {

    @Autowired
    private PEFundService service;

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public PEFundDto get(@PathVariable long id){
        PEFundDto fundDto = this.service.get(id);
        if(fundDto == null){

        }
        return fundDto;
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody PEFundDto fundDto){
        Long id = this.service.save(fundDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(fundDto.getId() == null){
            response.setCreationDate(new Date());
        }else{
            response.setCreationDate(fundDto.getCreationDate());
        }

        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public List<PEFundDto> search(@RequestBody PESearchParams searchParams){
        return this.service.loadFirmFunds(searchParams.getId());
    }

}