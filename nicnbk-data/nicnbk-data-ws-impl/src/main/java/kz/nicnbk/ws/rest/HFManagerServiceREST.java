package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.api.m2s2.*;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.m2s2.*;
import kz.nicnbk.ws.model.EntitySaveResponse;
import kz.nicnbk.ws.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
        HFManagerDto managerDto = this.service.get(id);
        if(managerDto == null){

        }
        return managerDto;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HFManagerDto managerDto) {
        Long id = this.service.save(managerDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(managerDto.getId() == null){
            response.setCreationDate(new Date());
        }
        //managerDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

}
