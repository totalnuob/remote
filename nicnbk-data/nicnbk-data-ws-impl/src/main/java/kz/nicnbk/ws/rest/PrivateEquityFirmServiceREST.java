package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.pe.PEFirmService;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEPagedSearchResult;
import kz.nicnbk.service.dto.pe.PESearchParams;
import kz.nicnbk.ws.model.EntitySaveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by zhambyl on 16-Nov-16.
 */

@RestController
@RequestMapping("/pe/firm")
public class PrivateEquityFirmServiceREST {
    @Autowired
    private PEFirmService service;

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public PEFirmDto get(@PathVariable long id){
        PEFirmDto firmDto = this.service.get(id);
        if(firmDto == null){

        }
        return firmDto;
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody PEFirmDto firmDto){

        Long id = this.service.save(firmDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(firmDto.getId() == null){
            response.setCreationDate(new Date());
        } else {
            response.setCreationDate(firmDto.getCreationDate());
        }
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_PRIVATE_EQUITY_VIEWER') OR hasAuthority('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public PEPagedSearchResult search(@RequestBody PESearchParams searchParams, HttpServletRequest request){
        return this.service.findByName(searchParams);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<PEFirmDto> getFirms(){
        return this.service.findAll();
    }

}