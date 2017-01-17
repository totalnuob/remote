package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.pe.PEFirmService;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PESearchParams;
import kz.nicnbk.ws.model.EntitySaveResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/pe/firm")
public class PEFirmServiceREST {

    @Autowired
    private PEFirmService service;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public PEFirmDto get(@PathVariable long id){
        PEFirmDto firmDto = this.service.get(id);
        if(firmDto == null){

        }
        return firmDto;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody PEFirmDto firmDto){
//        System.out.println(firmDto.getFirmName());
//        System.out.println(firmDto.getStrategy().size());
        Long id = this.service.save(firmDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(firmDto.getId() == null){
            response.setCreationDate(new Date());
        }
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Set<PEFirmDto> search(@RequestBody PESearchParams searchParams){
        return this.service.findByName(searchParams);
    }

}
