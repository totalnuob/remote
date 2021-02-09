package kz.nicnbk.ws.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@RestController
@RequestMapping("/corpMeetings")
public class CorpMeetingsServiceREST extends CommonServiceREST{

    private static final Logger logger = LoggerFactory.getLogger(CorpMeetingsServiceREST.class);

    @Autowired
    private CorpMeetingService corpMeetingService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    //private static final String corpMeetingsRole = "hasRole('ROLE_IC_MEMBER') OR hasRole('ROLE_CORPMEETINGS_EDITOR') OR hasRole('ROLE_ADMIN')";

    private static final String IC_MEETING_EDITOR = "hasRole('ROLE_IC_EDITOR') OR hasRole('ROLE_ADMIN') OR hasRole('ROLE_IC_ADMIN')";
    private static final String IC_MEETING_VIEWER = "hasRole('ROLE_IC_EDITOR') OR hasRole('ROLE_IC_VIEWER') " +
            "OR hasRole('ROLE_ADMIN') OR hasRole('ROLE_IC_ADMIN')";

    private static final String IC_MEETING_ADMIN = "hasRole('ROLE_ADMIN') OR hasRole('ROLE_IC_ADMIN')";

    private static final String IC_MEETING_TOPIC_EDITOR = "hasRole('ROLE_IC_TOPIC_EDITOR') OR hasRole('ROLE_ADMIN') OR hasRole('ROLE_IC_ADMIN')";

    private static final String IC_MEETING_TOPIC_VIEWER = "hasRole('ROLE_IC_TOPIC_EDITOR') OR hasRole('ROLE_IC_TOPIC_VIEWER_ALL') " +
            " OR hasRole('ROLE_IC_TOPIC_VIEWER') OR hasRole('ROLE_IC_TOPIC_RESTR') " +
            " OR hasRole('ROLE_ADMIN') OR hasRole('ROLE_IC_ADMIN')";
    private static final String IC_MEETING_TOPIC_APPROVAL = "hasRole('ROLE_IC_TOPIC_RESTR') OR hasRole('ROLE_IC_TOPIC_EDITOR') " +
            "OR hasRole('ROLE_ADMIN') OR hasRole('ROLE_IC_ADMIN')";

    private static final String IC_MEETING_OR_TOPIC_VIEWER = "hasRole('ROLE_IC_EDITOR') OR hasRole('ROLE_IC_VIEWER') " +
            " OR hasRole('ROLE_IC_TOPIC_EDITOR') OR hasRole('ROLE_IC_TOPIC_VIEWER') OR hasRole('ROLE_IC_TOPIC_VIEWER_ALL') " +
            " OR hasRole('ROLE_IC_TOPIC_RESTR') OR hasRole('ROLE_ADMIN') OR hasRole('ROLE_IC_ADMIN')";

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
    @PreAuthorize(IC_MEETING_TOPIC_VIEWER)
    @RequestMapping(value = "/ICMeetingTopic/search", method = RequestMethod.POST)
    public ResponseEntity<?> searchICMeetingTopics(@RequestBody ICMeetingTopicsSearchParamsDto searchParams) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        searchParams.setUsername(username);
        ICMeetingTopicsPagedSearchResult searchResult = corpMeetingService.searchICMeetingTopics(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @PreAuthorize(IC_MEETING_TOPIC_VIEWER)
    @RequestMapping(value = "/ICMeetingTopic/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getICMeetingTopic(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ICMeetingTopicDto dto = corpMeetingService.getICMeetingTopic(id, username);

        return buildNonNullResponse(dto);
    }

    @PreAuthorize(IC_MEETING_TOPIC_EDITOR)
    @RequestMapping(value = "/ICMeetingTopic/save", method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> saveICMeetingTopic(
            @RequestPart("data") String data,
            @RequestPart(name="file", required=false) MultipartFile[] files,
            @RequestPart(name="exp_note", required=false) MultipartFile explanatoryNotes
    ) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // Deserialize string to object
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(df);
        try {
            ICMeetingTopicDto icMeetingTopicDto = objectMapper.readValue(data, ICMeetingTopicDto.class);
            List<FilesDto> filesDtoSet = files != null ? buildFilesListDtoFromMultipart(files, FileTypeLookup.IC_MATERIALS.getCode()) : null;
            FilesDto explanatoryNote = explanatoryNotes != null ? buildFilesDtoFromMultipart(explanatoryNotes, FileTypeLookup.IC_EXPLANATORY_NOTE.getCode()) : null;
            EntitySaveResponseDto saveResponseDto = this.corpMeetingService.saveICMeetingTopic(icMeetingTopicDto, explanatoryNote, filesDtoSet, username);
            return buildEntitySaveResponse(saveResponseDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildEntitySaveResponseEntity(false);
//        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, true)){
//            return buildUnauthorizedResponse();
//        }
    }

    @PreAuthorize(IC_MEETING_TOPIC_EDITOR)
    @RequestMapping(value = "/ICMeetingTopic/saveUpdate", method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> saveICMeetingTopicUpdate(
            @RequestPart("data") String data,
            @RequestPart(name="file", required=false) MultipartFile[] files, @RequestPart(name="exp_note", required=false) MultipartFile explanatoryNotes
    ) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // Deserialize string to object
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(df);
        try {
            ICMeetingTopicUpdateDto updateDto = objectMapper.readValue(data, ICMeetingTopicUpdateDto.class);
            List<FilesDto> filesDtoSet = files != null ? buildFilesListDtoFromMultipart(files, FileTypeLookup.IC_MATERIALS.getCode()) : null;
            FilesDto explanatoryNote = explanatoryNotes != null ? buildFilesDtoFromMultipart(explanatoryNotes, FileTypeLookup.IC_EXPLANATORY_NOTE.getCode()) : null;
            EntitySaveResponseDto saveResponseDto = this.corpMeetingService.saveICMeetingTopicUpdate(updateDto, explanatoryNote, filesDtoSet, username);
            return buildEntitySaveResponse(saveResponseDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildEntitySaveResponseEntity(false);
//        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, true)){
//            return buildUnauthorizedResponse();
//        }
    }

    @PreAuthorize(IC_MEETING_TOPIC_APPROVAL)
    @RequestMapping(value = "/ICMeetingTopic/approve/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> approveICMeetingTopicById(@PathVariable long id){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto responseDto = corpMeetingService.approveICMeetingTopic(id, username);
        return buildEntitySaveResponse(responseDto);
    }

    @PreAuthorize(IC_MEETING_TOPIC_APPROVAL)
    @RequestMapping(value = "/ICMeetingTopic/cancelApprove/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> cancelApproveICMeetingTopicById(@PathVariable long id){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto responseDto = corpMeetingService.cancelApproveICMeetingTopic(id, username);
        return buildEntitySaveResponse(responseDto);
    }

    @PreAuthorize(IC_MEETING_TOPIC_VIEWER)
    @RequestMapping(value = "/availableApproveList", method = RequestMethod.GET)
    public ResponseEntity getAvailableApproveList(){
        List<EmployeeDto> employees = this.corpMeetingService.getAvailableApproveList();
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @PreAuthorize(IC_MEETING_TOPIC_EDITOR)
    @RequestMapping(value = "/ICMeetingTopic/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> safeDeleteICMeetingTopic(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.safeDeleteICMeetingTopic(id, username);
        return buildDeleteResponseEntity(deleted);
    }

//    @Deprecated
//    @RequestMapping(method = RequestMethod.POST, value = "/materials/upload/{meetingId}")
//    public ResponseEntity<?> handleFileUpload(@PathVariable("meetingId") long meetingId,
//                                          @RequestParam(value = "file", required = false)MultipartFile[] files) {
//
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//
//        ICMeetingTopicDto dto = corpMeetingService.getICMeetingTopic(meetingId);
//        if(!this.corpMeetingService.checkUserRolesForICMeetingTopicByTypeAndUsername(dto.getType(), username, true)){
//            return buildUnauthorizedResponse();
//        }
//
//        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, FileTypeLookup.IC_MATERIALS.getCode());
//        if (filesDtoSet != null) {
//            Set<FilesDto> savedAttachments = this.corpMeetingService.saveICMeetingTopicAttachments(meetingId, filesDtoSet, username);
//            if (savedAttachments == null) {
//                // error occurred
//                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
//            } else {
//                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(null, null, HttpStatus.OK);
//    }


    @PreAuthorize(IC_MEETING_TOPIC_EDITOR)
    @ResponseBody
    @RequestMapping(value="/materials/delete/{topicId}/{fileId}", method=RequestMethod.GET)
    public ResponseEntity<?> safeDeleteFile(@PathVariable(value="topicId") Long topicId, @PathVariable(value="fileId") Long fileId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.safeDeleteICMeetingTopicAttachment(topicId, fileId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_TOPIC_EDITOR)
    @ResponseBody
    @RequestMapping(value="/explanatoryNote/delete/{topicId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> safeDeleteExplanatoryNoteFile(@PathVariable(value="topicId") Long topicId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.deleteICMeetingTopicExplanatoryNote(topicId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_TOPIC_EDITOR)
    @ResponseBody
    @RequestMapping(value="/explanatoryNoteUpd/delete/{topicId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> safeDeleteExplanatoryNoteUpdFile(@PathVariable(value="topicId") Long topicId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.deleteICMeetingTopicExplanatoryNoteUpd(topicId, username);
        return buildDeleteResponseEntity(deleted);
    }


    /* IC MEETING *****************************************************************************************************/
    @PreAuthorize(IC_MEETING_EDITOR)
    @RequestMapping(value = "/ICMeeting/save", method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> saveICMeeting(@RequestPart(name="data") String data,
                                           @RequestPart(name="agenda", required=false) MultipartFile agendaFile,
                                           @RequestPart(name="protocol", required=false) MultipartFile protocolFile,
                                           @RequestPart(name="bulletin", required=false) MultipartFile bulletinFile) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // Deserialize string to object
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(df);
        try {
            ICMeetingDto icMeetingDto = objectMapper.readValue(data, ICMeetingDto.class);
            FilesDto agendaFileDto = agendaFile != null ? buildFilesDtoFromMultipart(agendaFile, FileTypeLookup.IC_AGENDA.getCode()) : null;
            FilesDto protocolFileDto = protocolFile != null ? buildFilesDtoFromMultipart(protocolFile, FileTypeLookup.IC_PROTOCOL.getCode()) : null;
            FilesDto bulletinFileDto = bulletinFile != null ? buildFilesDtoFromMultipart(bulletinFile, FileTypeLookup.IC_BULLETIN.getCode()) : null;
            EntitySaveResponseDto saveResponseDto = this.corpMeetingService.saveICMeeting(icMeetingDto, agendaFileDto,
                    protocolFileDto, bulletinFileDto, username);
            return buildEntitySaveResponse(saveResponseDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildEntitySaveResponseEntity(false);
    }

    @PreAuthorize(IC_MEETING_ADMIN)
    @ResponseBody
    @RequestMapping(value="/ICMeeting/agenda/delete/{icMeetingId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> safeDeleteICMeetingAgendaFile(@PathVariable(value="icMeetingId") Long icMeetingId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.deleteICMeetingAgenda(icMeetingId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_ADMIN)
    @ResponseBody
    @RequestMapping(value="/ICMeeting/protocol/delete/{icMeetingId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> safeDeleteICMeetingProtocolFile(@PathVariable(value="icMeetingId") Long icMeetingId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.deleteICMeetingProtocol(icMeetingId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_ADMIN)
    @ResponseBody
    @RequestMapping(value="/ICMeeting/bulletin/delete/{icMeetingId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> safeDeleteICMeetingBulletinFile(@PathVariable(value="icMeetingId") Long icMeetingId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.deleteICMeetingBulletin(icMeetingId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_OR_TOPIC_VIEWER)
    @RequestMapping(value = "/ICMeeting/getAllShort", method = RequestMethod.GET)
    public ResponseEntity<?> getAllICMeetingsShort() {
        List<ICMeetingDto> searchResult = corpMeetingService.getAllICMeetingsShort();
        return buildNonNullResponse(searchResult);
    }

    @PreAuthorize(IC_MEETING_VIEWER)
    @RequestMapping(value = "/ICMeeting/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getICMeeting(@PathVariable long id) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ICMeetingDto icMeetingDto = corpMeetingService.getICMeeting(id, username);
        return buildNonNullResponse(icMeetingDto);
    }

    @PreAuthorize(IC_MEETING_VIEWER)
    @RequestMapping(value = "/ICMeeting/search", method = RequestMethod.POST)
    public ResponseEntity<?> searchICMeetings(@RequestBody ICMeetingsSearchParamsDto searchParams) {
        ICMeetingsPagedSearchResult searchResult = corpMeetingService.searchICMeetings(searchParams);
        return buildNonNullResponse(searchResult);
    }

//    @RequestMapping(method = RequestMethod.POST, value = "/ICMeeting/protocol/upload/{meetingId}")
//    public ResponseEntity<?> handleICMeetingProtocolFileUpload(@PathVariable("meetingId") long meetingId,
//                                              @RequestParam(value = "file", required = true)MultipartFile file) {
//
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//
//        FilesDto filesDto = buildFilesDtoFromMultipart(file, FileTypeLookup.IC_PROTOCOL.getCode());
//        if (filesDto != null) {
//            Set<FilesDto> filesSet = new HashSet<FilesDto>();
//            filesSet.add(filesDto);
//            Set<FilesDto> savedAttachments = this.corpMeetingService.saveICMeetingProtocol(meetingId,filesSet, username);
//            if (savedAttachments == null) {
//                // error occurred
//                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
//            } else {
//                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @PreAuthorize(IC_MEETING_EDITOR)
    @RequestMapping(value = "/ICMeeting/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> safeDeleteICMeeting(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.safeDeleteICMeeting(id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_VIEWER)
    @RequestMapping(value="/ICMeeting/exportProtocolRegistry", method= RequestMethod.GET)
    @ResponseBody
    public void exportICMeetingAgenda(HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = null;
        try{
            filesDto = this.corpMeetingService.getICMeetingProtocolRegistryFileStream(username);
        }catch (IllegalStateException ex){
            filesDto = null;
        }

        if(filesDto == null || filesDto.getInputStream() == null){
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } catch (IOException e) {
                return;
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        try {
            //fileName = URLEncoder.encode(fileName, "UTF-8");
            //fileName = URLDecoder.decode(fileName, "ISO8859_1");
            //response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + "\"");
            response.setHeader("Content-disposition", "attachment;");
            org.apache.commons.io.IOUtils.copy(filesDto.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("(IC Meeting) File export (protocol registry) request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (protocol registry) request failed: io exception", e);
        } catch (Exception e){
            logger.error("(IC Meeting) File export (protocol registry) request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (protocol registry): failed to close input stream", e);
        }
    }

    @PreAuthorize(IC_MEETING_VIEWER)
    @RequestMapping(value="/ICMeeting/exportAgenda/{id}", method= RequestMethod.GET)
    @ResponseBody
    public void exportICMeetingAgenda(@PathVariable(value="id") Long icMeetingId,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = null;
        try{
            filesDto = this.corpMeetingService.getICMeetingAgendaFileStream(icMeetingId, username);
        }catch (IllegalStateException ex){
            filesDto = null;
        }

        if(filesDto == null || filesDto.getInputStream() == null){
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } catch (IOException e) {
                return;
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        try {
            //fileName = URLEncoder.encode(fileName, "UTF-8");
            //fileName = URLDecoder.decode(fileName, "ISO8859_1");
            //response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + "\"");
            response.setHeader("Content-disposition", "attachment;");
            org.apache.commons.io.IOUtils.copy(filesDto.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("(IC Meeting) File export (agenda) request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (agenda) request failed: io exception", e);
        } catch (Exception e){
            logger.error("(IC Meeting) File export (agenda) request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (agenda): failed to close input stream", e);
        }
    }

    @PreAuthorize(IC_MEETING_VIEWER)
    @RequestMapping(value="/ICMeeting/exportTopicApproveList/{id}", method= RequestMethod.GET)
    @ResponseBody
    public void exportICMeetingTopicApproveList(@PathVariable(value="id") Long icTopicId,
                                      HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = null;
        try{
            filesDto = this.corpMeetingService.getICMeetingTopicApproveListFileStream(icTopicId, username);
        }catch (IllegalStateException ex){
            filesDto = null;
        }

        if(filesDto == null || filesDto.getInputStream() == null){
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } catch (IOException e) {
                return;
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        try {
            response.setHeader("Content-disposition", "attachment;");
            org.apache.commons.io.IOUtils.copy(filesDto.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("(IC Meeting) File export (topic approve list) request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (topic approve list) request failed: io exception", e);
        } catch (Exception e){
            logger.error("(IC Meeting) File export (topic approve list) request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (topic approve list): failed to close input stream", e);
        }
    }

    @PreAuthorize(IC_MEETING_VIEWER)
    @RequestMapping(value="/ICMeeting/exportProtocol/{id}", method= RequestMethod.GET)
    @ResponseBody
    public void exportICMeetingProtocol(@PathVariable(value="id") Long icMeetingId,
                                      HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = null;
        try{
            filesDto = this.corpMeetingService.getICMeetingProtocolFileStream(icMeetingId, username);
        }catch (IllegalStateException ex){
            filesDto = null;
        }

        if(filesDto == null || filesDto.getInputStream() == null){
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } catch (IOException e) {
                return;
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        try {
            response.setHeader("Content-disposition", "attachment;");
            org.apache.commons.io.IOUtils.copy(filesDto.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("(IC Meeting) File export (protocol) request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (protocol) request failed: io exception", e);
        } catch (Exception e){
            logger.error("(IC Meeting) File export (protocol) request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (protocol): failed to close input stream", e);
        }
    }

    @PreAuthorize(IC_MEETING_VIEWER)
    @RequestMapping(value="/ICMeeting/exportBulletin/{id}", method= RequestMethod.GET)
    @ResponseBody
    public void exportICMeetingBulletin(@PathVariable(value="id") Long icMeetingId,
                                        HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = null;
        try{
            filesDto = this.corpMeetingService.getICMeetingBulletinFileStream(icMeetingId, username);
        }catch (IllegalStateException ex){
            filesDto = null;
        }

        if(filesDto == null || filesDto.getInputStream() == null){
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } catch (IOException e) {
                return;
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        try {
            response.setHeader("Content-disposition", "attachment;");
            org.apache.commons.io.IOUtils.copy(filesDto.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("(IC Meeting) File export (bulletin) request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (bulletin) request failed: io exception", e);
        } catch (Exception e){
            logger.error("(IC Meeting) File export (bulletin) request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e) {
            logger.error("(IC Meeting) File export (bulletin): failed to close input stream", e);
        }
    }


    @PreAuthorize(IC_MEETING_EDITOR)
    @RequestMapping(value = "/ICMeeting/unlockForFinalize/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> unlockICMeetingForFinalize(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.unlockICMeetingForFinalize(id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_TOPIC_EDITOR)
    @RequestMapping(value = "/ICMeeting/vote/", method = RequestMethod.POST)
    public ResponseEntity<?> saveICMeetingVotes(@RequestBody ICMeetingVoteDto votes) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.saveICMeetingVotes(votes, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_ADMIN)
    @RequestMapping(value = "/ICMeeting/close/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> closeICMeeting(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.closeICMeeting(id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_ADMIN)
    @RequestMapping(value = "/ICMeeting/reopen/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> reopenICMeeting(@PathVariable long id) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.corpMeetingService.reopenICMeeting(id, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(IC_MEETING_OR_TOPIC_VIEWER)
    @RequestMapping(value = "/CorpMeetings/upcoming", method = RequestMethod.GET)
    public ResponseEntity<?> getUpcomingEvents() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        List<CorpMeetingUpcomingEventDto> events = corpMeetingService.getUpcomingEvents(username);
        return buildNonNullResponse(events);
    }

//    @PreAuthorize(IC_MEETING_EDITOR)
//    @ResponseBody
//    @RequestMapping(value="/ICMeeting/protocol/delete/{meetingId}/{fileId}", method=RequestMethod.DELETE)
//    public ResponseEntity<?> safeDeleteICMeetingProtocolFile(@PathVariable(value="meetingId") Long meetingId, @PathVariable(value="fileId") Long fileId){
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//
//        boolean deleted = this.corpMeetingService.safeDeleteICMeetingProtocolAttachment(meetingId, fileId, username);
//        return buildDeleteResponseEntity(deleted);
//    }
}
