package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingDto;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingsPagedSearchResult;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingsSearchParamsDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoPagedSearchResult;
import kz.nicnbk.service.dto.tripmemo.TripMemoSearchParamsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@RestController
@RequestMapping("/corpMeetings")
public class CorpMeetingsServiceREST extends CommonServiceREST{

    @Autowired
    private FileService fileService;

    @Autowired
    private CorpMeetingService corpMeetingService;

    @Autowired
    private TokenService tokenService;


    @PreAuthorize("hasRole('ROLE_CORPMEETINGS_VIEWER') OR hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity<?> search(@RequestBody CorpMeetingsSearchParamsDto searchParams) {
        CorpMeetingsPagedSearchResult searchResult = corpMeetingService.search(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @PreAuthorize("hasRole('ROLE_CORPMEETINGS_VIEWER') OR hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id) {
        CorpMeetingDto corpMeetingDto = corpMeetingService.get(id);
        return buildNonNullResponse(corpMeetingDto);
    }

    @PreAuthorize("hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody CorpMeetingDto corpMeetingDto) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.corpMeetingService.save(corpMeetingDto, username);
        return buildEntitySaveResponse(saveResponseDto);
    }


//    @RequestMapping(method = RequestMethod.POST, value = "/attachment/upload/{tripMemoId}")
//    public ResponseEntity<?> handleFileUpload(@PathVariable("tripMemoId") long tripMemoId,
//                                          @RequestParam(value = "file", required = false)MultipartFile[] files) {
//
//        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, FileTypeLookup.MEMO_ATTACHMENT.getCode());
//        if(filesDtoSet != null){
//            Set<FilesDto> savedAttachments = this.tripMemoService.saveAttachments(tripMemoId, filesDtoSet);
//            if(savedAttachments == null){
//                // error occurred
//                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }else{
//                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(null, null, HttpStatus.OK);
//    }
//
//    @ResponseBody
//    @RequestMapping(value="/attachment/delete/{memoId}/{fileId}", method=RequestMethod.GET)
//    public ResponseEntity<?> deleteFile(@PathVariable(value="memoId") Long tripMemoId, @PathVariable(value="fileId") Long fileId){
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//        boolean access = this.tripMemoService.checkOwner(token, tripMemoId);
//        if(!access){
//            return buildUnauthorizedResponse();
//        }
//        //boolean deleted = this.tripMemoService.deleteAttachment(tripMemoId, fileId, username);
//        boolean deleted = this.tripMemoService.safeDeleteAttachment(tripMemoId, fileId, username);
//        return buildDeleteResponseEntity(deleted);
//    }
//
//
//    @RequestMapping(value = "/attachment/list/{tripMemoId}", method = RequestMethod.GET)
//    private ResponseEntity getFiles(@PathVariable("tripMemoId") long tripMemoId){
//        Set<FilesDto> files = this.tripMemoService.getAttachments(tripMemoId);
//        return buildNonNullResponse(files);
//    }
}
