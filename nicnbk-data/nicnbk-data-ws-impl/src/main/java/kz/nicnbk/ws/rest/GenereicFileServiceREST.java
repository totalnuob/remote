package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
public class GenereicFileServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(GenereicFileServiceREST.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

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
        sendFileDownloadResponse(response, fileService.getFileInfo(fileId), inputStream);
    }

    public void sendFileDownloadResponse(HttpServletResponse response, FilesDto fileDto, InputStream inputStream){
        response.setContentType(fileDto.getMimeType());
        String fileName = null;
        try {
            fileName = URLEncoder.encode(fileDto.getFileName(), "UTF-8");
            fileName = URLDecoder.decode(fileName, "ISO8859_1");
            response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + "\"");
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
                EmployeeDto employeeDto = this.employeeService.findByUsername(username);
                if(employeeDto.getRoles() != null && !employeeDto.getRoles().isEmpty()){
                    // Check rights
                    for(BaseDictionaryDto role: employeeDto.getRoles()){
                        if(role.getCode().equalsIgnoreCase(UserRoles.ADMIN.getCode()) ||
                                role.getCode().equalsIgnoreCase(UserRoles.IC_MEMBER.getCode())){
                            return true;
                        }
                    }
                }
                return false;
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
            }else {
                // Other files types?
                // TODO
                return true;
            }
        }
        return false;
    }
}
