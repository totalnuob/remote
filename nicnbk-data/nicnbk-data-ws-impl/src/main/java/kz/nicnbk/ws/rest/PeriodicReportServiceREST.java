package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@RestController
@RequestMapping("/periodicReport")
public class PeriodicReportServiceREST extends CommonServiceREST{

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable Long id) {
        PeriodicReportDto periodicReportDto = this.periodicReportService.get(id);
        return buildResponse(periodicReportDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity getAll() {
        List<PeriodicReportDto> periodicReportsList = this.periodicReportService.getAll();
        return buildResponse(periodicReportsList);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody PeriodicReportDto dto) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        // check access by owner
        if(dto.getId() == null){
            // set creator
            dto.setCreator(username);
        }
        Long id = periodicReportService.save(dto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, dto.getCreationDate());
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/inputFilesList/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getInputFilesList(@PathVariable Long reportId) {
        List<FilesDto> inputFiles = this.periodicReportService.getPeriodicReportFiles(reportId);
        return buildResponse(inputFiles);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload/{reportId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("reportId") Long reportId,
                                              @RequestParam(value = "file", required = true) MultipartFile file,
                                              @RequestParam(value = "fileType", required = true) String fileType) {

        // TODO: control file upload by user role

        FilesDto filesDto = buildFilesDtoFromMultipart(file, fileType);
        if(filesDto != null){
            // save file
            FilesDto savedFile = this.periodicReportService.saveInputFile(reportId, filesDto);

            if(savedFile == null){
                // error occurred

                // TODO: add message to response

                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // parse file
            FileUploadResultDto result = this.periodicReportService.parseFile(fileType, filesDto, reportId);
            result.setFileId(savedFile.getId());

            if(result != null && result.getStatus() == StatusResultType.SUCCESS){

                // TODO: add message to response

                return new ResponseEntity<>(result, null, HttpStatus.OK);
            }else{

                // TODO: add message to response

                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

//            if(savedFile == null){
//                // error occurred
//                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }else{
//                return new ResponseEntity<>(savedFile, null, HttpStatus.OK);
//            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value="/inputFiles/download/{fileType}/{id}", method=RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@PathVariable(value="id") Long fileId,
                             @PathVariable(value = "fileType") String fileType,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        InputStream inputStream = fileService.getFileInputStream(fileId, fileType);
        if(inputStream == null){
            // TODO: handle error
        }
        sendFileDownloadResponse(response, fileService.getFileInfo(fileId), inputStream);
    }


    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/scheduleInvestments/{id}", method = RequestMethod.GET)
    public ResponseEntity getScheduleInvestments(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getScheduleInvestments(id);
        return buildResponse(recordsHolder);
    }


    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/balanceOperations/{id}", method = RequestMethod.GET)
    public ResponseEntity getBalanaceOperations(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getStatementBalanceOperations(id);
        return buildResponse(recordsHolder);
    }

}
