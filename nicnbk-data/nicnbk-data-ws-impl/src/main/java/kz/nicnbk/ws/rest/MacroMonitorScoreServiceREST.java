package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.macromonitor.MacroMonitorScoreService;
import kz.nicnbk.service.dto.macromonitor.MacroMonitorScoreDto;
import kz.nicnbk.ws.model.EntitySaveResponse;
import kz.nicnbk.ws.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

//    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_MACROMONITOR_EDITOR')")
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
        Long id = this.service.save(dtoList);
        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_MACROMONITOR_EDITOR')")
    @RequestMapping(value = "/deleteAll/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> deleteAll(@PathVariable Integer id) {
        Long ret = this.service.deleteAll(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        Response response = new Response();
        response.setSuccess(true);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

}
