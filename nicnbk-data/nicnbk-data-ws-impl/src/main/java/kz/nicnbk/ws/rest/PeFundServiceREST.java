package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.pe.PeFundService;
import kz.nicnbk.service.dto.pe.PeFirmDto;
import kz.nicnbk.service.dto.pe.PeFundDto;
import kz.nicnbk.service.dto.pe.PeSearchParams;
import kz.nicnbk.ws.model.EntitySaveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 16-Nov-16.
 */
@RestController
@RequestMapping("/pe/fund")
public class PeFundServiceREST {

    @Autowired
    private PeFundService service;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public PeFundDto get(@PathVariable long id){
        PeFundDto fundDto = this.service.get(id);
        if(fundDto == null){

        }
        return fundDto;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody PeFundDto fundDto){
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

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Set<PeFundDto> search(@RequestBody PeSearchParams searchParams){
        return this.service.loadFirmFunds(searchParams.getId());
    }

}
