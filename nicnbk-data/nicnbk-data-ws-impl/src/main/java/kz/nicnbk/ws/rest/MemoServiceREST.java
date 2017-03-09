package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.*;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.*;
import kz.nicnbk.ws.model.EntitySaveResponse;
import kz.nicnbk.ws.model.Response;
import kz.nicnbk.ws.model.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private TokenService tokenService;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public MemoPagedSearchResult search(@RequestBody MemoSearchParams searchParams){
        MemoPagedSearchResult searchResult = memoService.search(searchParams);
        return searchResult;
    }

    @RequestMapping(value = "PE/search", method = RequestMethod.POST)
    public MemoPagedSearchResult searchPE(@RequestBody MemoSearchParams searchParams){
        return PEmemoService.search(searchParams);
    }

    @RequestMapping(value = "HF/search", method = RequestMethod.POST)
    public MemoPagedSearchResult searchHF(@RequestBody MemoSearchParams searchParams){
        return HFmemoService.search(searchParams);
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

//    @RequestMapping(value = "/get/{type}/{memoId}", method = RequestMethod.GET)
//    public HedgeFundsMeetingMemoDto get(@PathVariable int type, @PathVariable long memoId){
//        return HFmemoService.get(memoId);
//    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/PE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody PrivateEquityMeetingMemoDto memoDto){
        if(memoDto.getFund() != null && memoDto.getFund().getId() == null) {
            memoDto.setFund(null);
        }
        // set creator
        if(memoDto.getId() == null){
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
            String username = this.tokenService.decode(token).getUsername();
            memoDto.setOwner(username);
        }

        Long id = PEmemoService.save(memoDto);

        // TODO: response

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(memoDto.getId() == null){
            response.setCreationDate(new Date());
        } else {
            response.setCreationDate(memoDto.getCreationDate());
        }
        memoDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/HF/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundsMeetingMemoDto memoDto){

        if(memoDto.getFund() != null && memoDto.getFund().getId() == null) {
            memoDto.setFund(null);
        }

        // set creator
        if(memoDto.getId() == null){
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
            String username = this.tokenService.decode(token).getUsername();
            memoDto.setOwner(username);
        }

        Long id = HFmemoService.save(memoDto);

        // TODO: response

        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(memoDto.getId() == null){
            response.setCreationDate(new Date());
        } else {
            response.setCreationDate(memoDto.getCreationDate());
        }
        memoDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_REAL_ESTATE_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/RE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody RealEstateMeetingMemoDto memoDto){

        // set creator
        if(memoDto.getId() == null){
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
            String username = this.tokenService.decode(token).getUsername();
            memoDto.setOwner(username);
        }
        Long id = REmemoService.save(memoDto);

        // TODO: response
        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(memoDto.getId() == null){
            response.setCreationDate(new Date());
        } else {
            response.setCreationDate(memoDto.getCreationDate());
        }
        memoDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/GN/save", method = RequestMethod.POST)
    public ResponseEntity<Response>  save(@RequestBody GeneralMeetingMemoDto memoDto){

        // check access by owner
        if(memoDto.getId() != null){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean access = this.generalMemoService.checkAccess((String)auth.getDetails(), memoDto.getId());
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
            memoDto.setOwner(username);
        }

        Long id = generalMemoService.save(memoDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        EntitySaveResponse response = new EntitySaveResponse();
        response.setEntityId(id);
        if(memoDto.getId() == null){
            response.setCreationDate(new Date());
        } else {
            response.setCreationDate(memoDto.getCreationDate());
        }
        memoDto.setId(id);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }


    // TODO: control file upload by user role

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


    // TODO: control file download by user role
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


    // TODO: control file delete by user role
    @RequestMapping(value="/attachment/delete/{memoId}/{fileId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable(value="memoId") Long memoId, @PathVariable(value="fileId") Long fileId){

        boolean deleted = this.memoService.deleteAttachment(memoId, fileId);

        HttpHeaders httpHeaders = new HttpHeaders();
        Response response = new Response();
        response.setSuccess(deleted);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    // TODO: control file info by user role

    @RequestMapping(value = "/attachment/list/{memoId}", method = RequestMethod.GET)
    private Set<FilesDto> get(@PathVariable("memoId") long memoId){

       return this.memoService.getAttachments(memoId);
    }
}
