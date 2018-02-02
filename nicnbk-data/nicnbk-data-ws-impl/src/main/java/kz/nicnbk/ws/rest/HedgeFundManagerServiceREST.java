package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.hf.HedgeFundManagerPagedSearchResult;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/manager")
public class HedgeFundManagerServiceREST extends CommonServiceREST{

    @Autowired
    private HFManagerService service;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        HFManagerDto firmDto = this.service.get(id);
        return buildNonNullResponse(firmDto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HFManagerDto firmDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(firmDto.getId() == null){
            firmDto.setOwner(username);
        }
        Long id = this.service.save(firmDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, firmDto.getCreationDate());
        }
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity search(@RequestBody HedgeFundSearchParams searchParams){
        HedgeFundManagerPagedSearchResult searchResult = this.service.findByName(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity getManagers(){
        List<HFManagerDto> managers = this.service.findAll();
        return buildNonNullResponse(managers);
    }
}
