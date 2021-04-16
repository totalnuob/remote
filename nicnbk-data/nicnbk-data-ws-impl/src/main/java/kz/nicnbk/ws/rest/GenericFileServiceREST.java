package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.common.EmployeeApproveDto;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingTopicDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by magzumov on 15.09.2017.
 */
@RestController
@RequestMapping("/files")
public class GenericFileServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(GenericFileServiceREST.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CorpMeetingService corpMeetingService;

    @RequestMapping(value="/download/{fileType}/{id}", method= RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@PathVariable(value="id") Long fileId,
                             @PathVariable(value = "fileType") String fileType,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        if(!checkFileDownloadPermission(fileId, username)){
            response.setStatus(401);
            try {
                response.sendError(401, "Permission denied");
                return;
            } catch (IOException e) {
                // TODO: handle error
            }
        }

        InputStream inputStream = fileService.getFileInputStream(fileId, fileType);
        if(inputStream == null){
            // TODO: handle error
        }
        sendFileDownloadResponse(response, fileService.getFileInfo(fileId), inputStream, false);
    }

    @RequestMapping(value="/open/{fileType}/{id}", method= RequestMethod.GET)
    @ResponseBody
    public void openFile(@PathVariable(value="id") Long fileId,
                             @PathVariable(value = "fileType") String fileType,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        if(!checkFileDownloadPermission(fileId, username)){
            response.setStatus(401);
            try {
                response.sendError(401, "Permission denied");
                return;
            } catch (IOException e) {
                // TODO: handle error
            }
        }

        InputStream inputStream = fileService.getFileInputStream(fileId, fileType);
        if(inputStream == null){
            // TODO: handle error
        }
        sendFileDownloadResponse(response, fileService.getFileInfo(fileId), inputStream, true);
    }

    public void sendFileDownloadResponse(HttpServletResponse response, FilesDto fileDto, InputStream inputStream, boolean inline){
        response.setContentType(fileDto.getMimeType());
        String fileName = null;
        try {
            fileName = URLEncoder.encode(fileDto.getFileName(), "UTF-8");
            fileName = URLDecoder.decode(fileName, "ISO8859_1");
            response.setHeader("Content-disposition",
                    (inline ? "inline": "attachment") +  ";filename=\""+ fileName + "\"");
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("File download request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("File download request failed: io exception", e);
        } catch (Exception e){
            logger.error("File download request failed", e);
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            logger.error("File download: failed to close input stream");
        }
    }

    // TODO: refactor
    private boolean checkFileDownloadPermission(Long fileId, String username){
        FilesDto filesDto = this.fileService.getFileInfo(fileId);
        if(filesDto != null && filesDto.getType() != null){
            if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.IC_PROTOCOL.getCode())){
                // IC PROTOCOL
                return checkICProtocol(username);
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.IC_AGENDA.getCode())){
                // IC AGENDA
                return checkICAgenda(username);
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.IC_BULLETIN.getCode())){
                // IC AGENDA
                return checkICBulletin(username);
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.IC_EXPLANATORY_NOTE.getCode())){
                return checkICMeetingTopicExplanatoryNoteReadAccess(fileId, username);
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.IC_MATERIALS.getCode())){
                return checkICMeetingTopicMaterialsReadAccess(fileId, username);
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.HR_DOCS.getCode())){
                // HR DOCS
                return true;
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.CC_ATTACHMENT.getCode())){
                // Capital Call Attachment
                EmployeeDto employeeDto = this.employeeService.findByUsername(username);
                if(employeeDto.getRoles() != null && !employeeDto.getRoles().isEmpty()){
                    // Check rights
                    for(BaseDictionaryDto role: employeeDto.getRoles()){
                        if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.REPORTING_VIEW.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.REPORTING_EDIT.getCode())){
                            return true;
                        }
                    }
                }
            }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_A.getCode()) ||
                    filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_B.getCode()) ||
                    filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CONS.getCode()) ||
                    filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_ALLOCATIONS_CONS.getCode())){
                //
                return true;
            }else {
                // Other files types?
                // TODO
                return true;
            }
        }
        return false;
    }

    private boolean checkICProtocol(String username){
        EmployeeDto employeeDto = this.employeeService.findByUsername(username);
        if(employeeDto.getRoles() != null && !employeeDto.getRoles().isEmpty()){
            // Check rights
            for(BaseDictionaryDto role: employeeDto.getRoles()){
                if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_EDIT.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_VIEW.getCode())){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkICAgenda(String username){
        EmployeeDto employeeDto = this.employeeService.findByUsername(username);
        if(employeeDto.getRoles() != null && !employeeDto.getRoles().isEmpty()){
            // Check rights
            for(BaseDictionaryDto role: employeeDto.getRoles()){
                if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_EDIT.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_VIEW.getCode())){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkICBulletin(String username){
        EmployeeDto employeeDto = this.employeeService.findByUsername(username);
        if(employeeDto.getRoles() != null && !employeeDto.getRoles().isEmpty()){
            // Check rights
            for(BaseDictionaryDto role: employeeDto.getRoles()){
                if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_EDIT.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_VIEW.getCode())){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkICMeetingTopicFilesReadAccess(ICMeetingTopicDto icMeetingTopic, EmployeeDto employeeDto){
        if(employeeDto != null && employeeDto.getRoles() != null && !employeeDto.getRoles().isEmpty()){
            for(BaseDictionaryDto role: employeeDto.getRoles()){
                if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_ADMIN.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_TOPIC_VIEW_ALL.getCode())){
                    return true;
                }else if(role.getCode().equalsIgnoreCase(UserRoles.IC_TOPIC_EDIT.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_TOPIC_VIEW.getCode()) ||
                        role.getCode().equalsIgnoreCase(UserRoles.IC_TOPIC_RESTR.getCode())){
                    // check department
                    if(icMeetingTopic != null){
                        // check same dept
                        if(icMeetingTopic.getDepartment() != null) {
                            if (icMeetingTopic.getDepartment() != null) {
                                int topicDeptId = icMeetingTopic.getDepartment().getId();
                                int editorDeptId = employeeDto.getPosition() != null && employeeDto.getPosition().getDepartment() != null ?
                                        employeeDto.getPosition().getDepartment().getId() : 0;
                                if (topicDeptId != 0 && topicDeptId == editorDeptId) {
                                    return true;
                                }
                            }
                        }
                        //check approve list
                        if(icMeetingTopic.getApproveList() != null){
                            for(EmployeeApproveDto approveDto: icMeetingTopic.getApproveList()){
                                if(approveDto.getEmployee().getId().longValue() == approveDto.getEmployee().getId().longValue()){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkICMeetingTopicMaterialsReadAccess(Long fileId, String username){
        EmployeeDto employeeDto = this.employeeService.findByUsername(username);
        ICMeetingTopicDto icMeetingTopic = this.corpMeetingService.getICMeetingTopicByMaterialFileId(fileId);
        return checkICMeetingTopicFilesReadAccess(icMeetingTopic, employeeDto);
    }

    private boolean checkICMeetingTopicExplanatoryNoteReadAccess(Long fileId, String username){
        EmployeeDto employeeDto = this.employeeService.findByUsername(username);
        ICMeetingTopicDto icMeetingTopic = this.corpMeetingService.getICMeetingTopicByExplanatoryFileId(fileId);
        return checkICMeetingTopicFilesReadAccess(icMeetingTopic, employeeDto);
    }
}
