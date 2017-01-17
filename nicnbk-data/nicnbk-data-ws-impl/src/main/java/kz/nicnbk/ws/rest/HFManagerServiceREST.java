package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.hf.HedgeFundManagerPagedSearchResult;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;
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
    public HFManagerDto get(@PathVariable long id){
        HFManagerDto firmDto = this.service.get(id);
        if(firmDto == null){

        }
        return firmDto;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HFManagerDto firmDto) {
        Long id = this.service.save(firmDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(firmDto.getId() == null){
            response.setCreationDate(new Date());
        }else{
            response.setCreationDate(firmDto.getCreationDate());
        }
        //managerDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public HedgeFundManagerPagedSearchResult search(@RequestBody HedgeFundSearchParams searchParams){
        return this.service.findByName(searchParams);
    }

}
