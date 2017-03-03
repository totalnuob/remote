package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.dto.hf.HedgeFundDto;
import kz.nicnbk.service.dto.hf.HedgeFundPagedSearchResult;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;
import kz.nicnbk.ws.model.EntitySaveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/fund")
public class HedgeFundServiceREST {

    @Autowired
    private HedgeFundService service;

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public HedgeFundDto get(@PathVariable long id){
        HedgeFundDto hedgeFundDto = this.service.get(id);
        if(hedgeFundDto == null){

        }
        return hedgeFundDto;
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundDto hedgeFundDto) {
        Long id = this.service.save(hedgeFundDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(hedgeFundDto.getId() == null){
            response.setCreationDate(new Date());
        }else{
            response.setCreationDate(hedgeFundDto.getCreationDate());
        }
        //hedgeFundDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public HedgeFundPagedSearchResult search(@RequestBody HedgeFundSearchParams searchParams){
        return this.service.findByName(searchParams);
    }
}
