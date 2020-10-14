package kz.nicnbk.ws.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@RestController
@RequestMapping("/corpMeetings")
public class CorpMeetingsServiceREST extends CommonServiceREST{

    @Autowired
    private CorpMeetingService corpMeetingService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    private static final String corpMeetingsRole = "hasRole('ROLE_IC_MEMBER') OR hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')";

//    @Deprecated
//    @PreAuthorize("hasRole('ROLE_CORPMEETINGS_VIEWER') OR hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/search", method = RequestMethod.POST)
//    public ResponseEntity<?> search(@RequestBody CorpMeetingsSearchParamsDto searchParams) {
//        CorpMeetingsPagedSearchResult searchResult = corpMeetingService.search(searchParams);
//        return buildNonNullResponse(searchResult);
//    }


//    @Deprecated
//    @PreAuthorize("hasRole('ROLE_CORPMEETINGS_VIEWER') OR hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
//    public ResponseEntity get(@PathVariable long id) {
//        CorpMeetingDto corpMeetingDto = corpMeetingService.get(id);
//        return buildNonNullResponse(corpMeetingDto);
//    }

//    @Deprecated
//    @PreAuthorize("hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public ResponseEntity<?> save(@RequestBody CorpMeetingDto corpMeetingDto) {
//
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//
//        EntitySaveResponseDto saveResponseDto = this.corpMeetingService.save(corpMeetingDto, username);
//        return buildEntitySaveResponse(saveResponseDto);
//    }


//    @Deprecated
//    @PreAuthorize("hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
//    public ResponseEntity<?> delete(@PathVariable long id) {
//
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//
//        boolean deleted = this.corpMeetingService.safeDelete(id, username);
//        return buildDeleteResponseEntity(deleted);
//    }


    /* IC MEETING TOPIC ***********************************************************************************************/
    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeetingTopic/search", method = RequestMethod.POST)
    public ResponseEntity<?> searchICMeetingTopics(@RequestBody ICMeetingTopicsSearchParamsDto searchParams) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ICMeetingTopicsPagedSearchResult searchResult = corpMeetingService.searchICMeetingTopics(searchParams, username);
        return buildNonNullResponse(searchResult);
    }

    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeetingTopic/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getICMeetingTopic(@PathVariable long id) {
        ICMeetingTopicDto dto = corpMeetingService.getICMeetingTopic(id);

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, false)){
            return buildUnauthorizedResponse();
        }

        return buildNonNullResponse(dto);
    }

    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeetingTopic/save", method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> saveICMeetingTopic(
            @RequestPart("data") String data,
            @ModelAttribute("file") MultipartFile[] files
    ) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // TODO: deserialize string
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                ;
        try {
            ICMeetingTopicDto icMeetingTopicDto = objectMapper.readValue(data, ICMeetingTopicDto.class);
            EntitySaveResponseDto saveResponseDto = this.corpMeetingService.saveICMeetingTopic(icMeetingTopicDto, username);
            return buildEntitySaveResponse(saveResponseDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildUnauthorizedResponse();
//        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, true)){
//            return buildUnauthorizedResponse();
//        }
    }

    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeetingTopic/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> safeDeleteICMeetingTopic(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ICMeetingTopicDto dto = corpMeetingService.getICMeetingTopic(id);
        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, true)){
            return buildUnauthorizedResponse();
        }

        boolean deleted = this.corpMeetingService.safeDeleteICMeetingTopic(id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/materials/upload/{meetingId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("meetingId") long meetingId,
                                          @RequestParam(value = "file", required = false)MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ICMeetingTopicDto dto = corpMeetingService.getICMeetingTopic(meetingId);
        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, true)){
            return buildUnauthorizedResponse();
        }

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, FileTypeLookup.IC_MATERIALS.getCode());
        if (filesDtoSet != null) {
            Set<FilesDto> savedAttachments = this.corpMeetingService.saveICMeetingTopicAttachments(meetingId, filesDtoSet, username);
            if (savedAttachments == null) {
                // error occurred
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }


    @PreAuthorize(corpMeetingsRole)
    @ResponseBody
    @RequestMapping(value="/materials/delete/{meetingId}/{fileId}", method=RequestMethod.GET)
    public ResponseEntity<?> safeDeleteFile(@PathVariable(value="meetingId") Long meetingId, @PathVariable(value="fileId") Long fileId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ICMeetingTopicDto dto = corpMeetingService.getICMeetingTopic(meetingId);
        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, true)){
            return buildUnauthorizedResponse();
        }

        //boolean deleted = this.tripMemoService.deleteAttachment(tripMemoId, fileId, username);
        boolean deleted = this.corpMeetingService.safeDeleteICMeetingTopicAttachment(meetingId, fileId, username);
        return buildDeleteResponseEntity(deleted);
    }


    /* IC MEETING *****************************************************************************************************/
    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeeting/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveICMeeting(@RequestBody ICMeetingDto icMeetingDto) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.corpMeetingService.saveICMeeting(icMeetingDto, username);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeeting/getAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllICMeetings() {
        List<ICMeetingDto> searchResult = corpMeetingService.getAllICMeetings();
        return buildNonNullResponse(searchResult);
    }

    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeeting/search", method = RequestMethod.POST)
    public ResponseEntity<?> searchICMeetings(@RequestBody ICMeetingsSearchParamsDto searchParams) {
        ICMeetingsPagedSearchResult searchResult = corpMeetingService.searchICMeetings(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ICMeeting/protocol/upload/{meetingId}")
    public ResponseEntity<?> handleICMeetingProtocolFileUpload(@PathVariable("meetingId") long meetingId,
                                              @RequestParam(value = "file", required = true)MultipartFile file) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = buildFilesDtoFromMultipart(file, FileTypeLookup.IC_PROTOCOL.getCode());
        if (filesDto != null) {
            Set<FilesDto> filesSet = new HashSet<FilesDto>();
            filesSet.add(filesDto);
            Set<FilesDto> savedAttachments = this.corpMeetingService.saveICMeetingProtocol(meetingId,filesSet, username);
            if (savedAttachments == null) {
                // error occurred
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize(corpMeetingsRole)
    @RequestMapping(value = "/ICMeeting/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> safeDeleteICMeeting(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.safeDeleteICMeeting(id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(corpMeetingsRole)
    @ResponseBody
    @RequestMapping(value="/ICMeeting/protocol/delete/{meetingId}/{fileId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> safeDeleteICMeetingProtocolFile(@PathVariable(value="meetingId") Long meetingId, @PathVariable(value="fileId") Long fileId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.safeDeleteICMeetingProtocolAttachment(meetingId, fileId, username);
        return buildDeleteResponseEntity(deleted);
    }
}
