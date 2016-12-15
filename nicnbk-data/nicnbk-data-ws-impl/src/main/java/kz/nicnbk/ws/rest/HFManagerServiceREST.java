package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.dto.hf.HFFirmDto;
import kz.nicnbk.ws.model.EntitySaveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/manager")
public class HFManagerServiceREST {

    @Autowired
    private HFManagerService service;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public HFFirmDto get(@PathVariable long id){
        HFFirmDto firmDto = this.service.get(id);
        if(firmDto == null){

        }
        return firmDto;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HFFirmDto firmDto) {
        Long id = this.service.save(firmDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(firmDto.getId() == null){
            response.setCreationDate(new Date());
        }
        //managerDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

}
