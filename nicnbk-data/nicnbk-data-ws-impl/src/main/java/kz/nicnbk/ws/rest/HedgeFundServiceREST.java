package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/fund")
public class HedgeFundServiceREST extends CommonServiceREST{

    @Autowired
    private HedgeFundService service;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        HedgeFundDto hedgeFundDto = this.service.get(id);
        return buildResponse(hedgeFundDto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundDto hedgeFundDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(hedgeFundDto.getId() == null){
            hedgeFundDto.setOwner(username);
        }
        Long id = this.service.save(hedgeFundDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, hedgeFundDto.getCreationDate());
        }
    }


    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity search(@RequestBody HedgeFundSearchParams searchParams){
        HedgeFundPagedSearchResult searchResult = this.service.findByName(searchParams);
        return buildResponse(searchResult);
    }
}
