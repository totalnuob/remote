package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.macromonitor.MacroMonitorScoreService;
import kz.nicnbk.service.dto.macromonitor.MacroMonitorScoreDto;
import kz.nicnbk.ws.model.EntitySaveResponse;
import kz.nicnbk.ws.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhambyl on 30-Mar-17.
 */

@RestController
@RequestMapping("/macromonitor")
public class MacroMonitorScoreServiceREST {

    @Autowired
    private MacroMonitorScoreService service;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_MACROMONITOR_EDITOR') OR hasRole('ROLE_MACROMONITOR_VIEWER') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public List<MacroMonitorScoreDto> get(@PathVariable Integer id) {
        List<MacroMonitorScoreDto> dto = this.service.getList(id);
        if(dto == null) {

        }
        return dto;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_MACROMONITOR_EDITOR')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody List<MacroMonitorScoreDto> dtoList) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = this.service.save(dtoList, username);
        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_MACROMONITOR_EDITOR')")
    @RequestMapping(value = "/deleteAll/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> deleteAll(@PathVariable Integer id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long ret = this.service.deleteAll(id, username);
        HttpHeaders httpHeaders = new HttpHeaders();
        Response response = new Response();
        response.setSuccess(true);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

}
