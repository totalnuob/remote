package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.*;
import kz.nicnbk.service.dto.files.FilesDto;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/m2s2")
public class MemoServiceREST {

    @Autowired
    private MeetingMemoService memoService;

    @Autowired
    private GeneralMeetingMemoService generalMemoService;
    @Autowired
    private PEMeetingMemoService PEmemoService;
    @Autowired
    private HFMeetingMemoService HFmemoService;
    @Autowired
    private REMeetingMemoService REmemoService;

    @Autowired
    private FileService fileService;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public MemoPagedSearchResult search(@RequestBody MemoSearchParams searchParams){
        MemoPagedSearchResult searchResult = memoService.search(searchParams);
        return searchResult;
    }

    @RequestMapping(value = "/get/{type}/{memoId}", method = RequestMethod.GET)
    public MeetingMemoDto get(@PathVariable int type, @PathVariable long memoId){
        switch (type){
            case MeetingMemo.GENERAL_DISCRIMINATOR:
                return generalMemoService.get(memoId);
            case MeetingMemo.PE_DISCRIMINATOR:
                return PEmemoService.get(memoId);
            case MeetingMemo.HF_DISCRIMINATOR:
                return HFmemoService.get(memoId);
            case MeetingMemo.RE_DISCRIMINATOR:
                return REmemoService.get(memoId);
        }

        // TODO: custom response if not matched
        return null;
    }

    @RequestMapping(value = "/PE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody PrivateEquityMeetingMemoDto memoDto){
        Long id = PEmemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/HF/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundsMeetingMemoDto memoDto){
        Long id = HFmemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/RE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody RealEstateMeetingMemoDto memoDto){
        Long id = REmemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/GN/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody GeneralMeetingMemoDto memoDto){
        Long id = generalMemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/attachment/upload/{memoId}")
    public Set<FilesDto> handleFileUpload(@PathVariable("memoId") long memoId,
                                              @RequestParam(value = "file", required = false) MultipartFile[] files) {

        Set<FilesDto> filesDtoSet = new HashSet<>();
        if(files != null && files.length > 0){
            for(MultipartFile file: files){
                FilesDto filesDto = new FilesDto();
                filesDto.setType(FileTypeLookup.MEMO_ATTACHMENT.getCode());
                filesDto.setFileName(file.getOriginalFilename());
                filesDto.setMimeType(file.getContentType());
                filesDto.setSize(file.getSize());
                try {
                    filesDto.setBytes(file.getBytes());
                } catch (IOException e) {

                    // TODO: handle error
                    e.printStackTrace();
                }
                filesDtoSet.add(filesDto);
            }
            try {
                return this.memoService.saveAttachments(memoId, filesDtoSet);
            }catch (Exception ex){

                // TODO: handle error
                ex.printStackTrace();
            }
        }

        // TODO: handle error, send response
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
    public ResponseEntity<?> deleteFile(@PathVariable(value="memoId") Long memoId, @PathVariable(value="fileId") Long fileId){

        boolean deleted = this.memoService.deleteAttachment(memoId, fileId);

        HttpHeaders httpHeaders = new HttpHeaders();
        Response response = new Response();
        response.setSuccess(deleted);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/attachment/list/{memoId}", method = RequestMethod.GET)
    private Set<FilesDto> get(@PathVariable("memoId") long memoId){

       return this.memoService.getAttachments(memoId);
    }
}
