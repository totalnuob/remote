package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoPagedSearchResult;
import kz.nicnbk.service.dto.tripmemo.TripMemoSearchParamsDto;
import kz.nicnbk.ws.model.EntitySaveResponse;
import kz.nicnbk.ws.model.Response;
import kz.nicnbk.ws.model.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@RestController
@RequestMapping("/bt")
public class TripMemoServiceREST {

    @Autowired
    private TripMemoService tripMemoService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TokenService tokenService;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public TripMemoPagedSearchResult search(@RequestBody TripMemoSearchParamsDto searchParams) {
        TripMemoPagedSearchResult searchResult = tripMemoService.search(searchParams);
        return searchResult;
    }

    @RequestMapping(value = "/get/{tripMemoId}", method = RequestMethod.GET)
    public TripMemoDto get(@PathVariable long tripMemoId) {
        return tripMemoService.get(tripMemoId);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody TripMemoDto tripMemoDto) {


        // check access by owner
        if(tripMemoDto.getId() != null){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean access = this.tripMemoService.checkAccess((String)auth.getDetails(), tripMemoDto.getId());
            if(!access){
                Response response = new Response();
                response.setSuccess(false);
                ResponseMessage message = new ResponseMessage();
                message.setNameEn("Accees denied");
                response.setMessage(message);
                return new ResponseEntity<>(response, null, HttpStatus.UNAUTHORIZED);
            }

        } else{
            // set creator
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
            String username = this.tokenService.decode(token).getUsername();
            tripMemoDto.setOwner(username);
        }

        Long id = tripMemoService.save(tripMemoDto);
        // TODO: response
        tripMemoDto.setId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/attachment/upload/{tripMemoId}")
    public Set<FilesDto> handleFileUpload(@PathVariable("tripMemoId") long tripMemoId,
                                          @RequestParam(value = "file", required = false)MultipartFile[] files) {

        Set<FilesDto> filesDtoSet = new HashSet<>();
        if(files != null && files.length > 0) {
            for(MultipartFile file: files) {
                FilesDto filesDto = new FilesDto();
                filesDto.setType(FileTypeLookup.MEMO_ATTACHMENT.getCode());
                filesDto.setFileName(file.getOriginalFilename());
                filesDto.setMimeType(file.getContentType());
                filesDto.setSize(file.getSize());
                try {
                    filesDto.setBytes(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filesDtoSet.add(filesDto);
            }
            try {
                return this.tripMemoService.saveAttachments(tripMemoId, filesDtoSet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping(value="/attachment/{id}", method=RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@PathVariable(value="id") Long fileId, HttpServletResponse response) {
        // TODO: Check rights
        InputStream inputStream = fileService.getFileInputStream(fileId, FileTypeLookup.MEMO_ATTACHMENT.getCode());
        if(inputStream == null){
            // TODO: handle error
        }
        try {
            FilesDto fileDto = fileService.getFileInfo(fileId);
            response.setContentType(fileDto.getMimeType());
            String fileName = URLEncoder.encode(fileDto.getFileName(), "UTF-8");
            fileName = URLDecoder.decode(fileName, "ISO8859_1");
            response.setHeader("Content-disposition", "attachment; filename="+ fileName);
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: handle error
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="/attachment/delete/{memoId}/{fileId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable(value="memoId") Long tripMemoId, @PathVariable(value="fileId") Long fileId){

        boolean deleted = this.tripMemoService.deleteAttachment(tripMemoId, fileId);

        HttpHeaders httpHeaders = new HttpHeaders();
        Response response = new Response();
        response.setSuccess(deleted);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }


    @RequestMapping(value = "/attachment/list/{tripMemoId}", method = RequestMethod.GET)
    private Set<FilesDto> getFiles(@PathVariable("tripMemoId") long tripMemoId){
        return this.tripMemoService.getAttachments(tripMemoId);
    }
}
