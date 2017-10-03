package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.reporting.NICKMFReportingData;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Date;
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

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/otherInfo/save")
    public ResponseEntity<?> saveOtherInfo(@RequestBody ReportOtherInfoDto dto){
        boolean saved = this.periodicReportService.saveOtherInfo(dto);
        if(saved){
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(null, dto.getCreationDate());
        }else {
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/otherInfo/{id}", method = RequestMethod.GET)
    public ResponseEntity getOtherInfo(@PathVariable Long id) {
        ReportOtherInfoDto otherInfo = this.periodicReportService.getOtherInfo(id);
        return new ResponseEntity<>(otherInfo, null, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/NICKMFReportingInfo/save")
    public ResponseEntity<?> saveNICKMFReportingData(@RequestBody NICKMFReportingDataHolderDto dataHolderDto){
        boolean saved = this.periodicReportService.saveNICKMFReportingData(dataHolderDto);
        if(saved){

            // TODO: response from DB, not UI

            return buildEntitySaveResponse(null, new Date());
        }else {
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/NICKMFReportingInfoPreviousMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getNICKMFReportingDataFromPreviousMonth(@PathVariable Long reportId) {
        NICKMFReportingDataHolderDto holderDto = this.periodicReportService.getNICKMFReportingDataFromPreviousMonth(reportId);
        return new ResponseEntity<>(holderDto, null, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/NICKMFReportingInfo/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getNICKMFReportingData(@PathVariable Long reportId) {
        NICKMFReportingDataHolderDto holderDto = this.periodicReportService.getNICKMFReportingData(reportId);
        return new ResponseEntity<>(holderDto, null, HttpStatus.OK);
    }

    @RequestMapping(value="/monthlyCashStatementFile/delete/{reportId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteMonthlyCashStatementFile(@PathVariable(value="reportId") Long reportId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();


        // TODO: check rights


        boolean deleted = this.periodicReportService.safeDelete(reportId, FileTypeLookup.NB_REP_MONTHLY_CASH_STATEMENT, username);
        return buildDeleteResponseEntity(deleted);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/nonParsed/upload/{reportId}")
    public ResponseEntity<?> handleNonParsedFileUpload(@PathVariable("reportId") Long reportId,
                                              @RequestParam(value = "file", required = true) MultipartFile file,
                                              @RequestParam(value = "fileType", required = true) String fileType) {

        // TODO: control file upload by user role

        FilesDto filesDto = buildFilesDtoFromMultipart(file, fileType);
        if(filesDto != null){
            // save file
            FilesDto savedFile = this.periodicReportService.saveInputFile(reportId, filesDto);

            if(savedFile == null){
                // error occurred

                // TODO: check error
                FileUploadResultDto result = new FileUploadResultDto(StatusResultType.FAIL, null, "File upload failed: error saving file", null);

                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            FileUploadResultDto result = new FileUploadResultDto(StatusResultType.SUCCESS, null, "Successfully uploaded file", null);
            result.setFileId(savedFile.getId());
            result.setFileName(savedFile.getFileName());
            return new ResponseEntity<>(result, null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
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

                // TODO: check error
                FileUploadResultDto result = new FileUploadResultDto(StatusResultType.FAIL, null, "File upload failed: error saving file", null);

                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // parse file
            FileUploadResultDto result = this.periodicReportService.parseFile(fileType, filesDto, reportId);
            result.setFileName(filesDto.getFileName());
            result.setFileId(savedFile.getId());

            if(result != null && result.getStatus() == StatusResultType.SUCCESS){
                return new ResponseEntity<>(result, null, HttpStatus.OK);
            }else{

                // TODO: check error
                // remove file
                boolean deleted = periodicReportService.deleteFile(savedFile.getId());
                if(deleted){
                    deleted = fileService.delete(savedFile.getId());
                }

                if(deleted){
                    return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
                }else{
                    return new ResponseEntity<>(new FileUploadResultDto(StatusResultType.FAIL, null,
                            "File upload failed: error when deleting file to undo actions", null), null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/scheduleInvestments/{id}", method = RequestMethod.GET)
    public ResponseEntity getScheduleInvestments(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getScheduleInvestments(id);
        return buildResponse(recordsHolder);
    }


    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/balanceOperations/{id}", method = RequestMethod.GET)
    public ResponseEntity getBalanceOperations(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getStatementBalanceOperations(id);
        return buildResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/cashflows/{id}", method = RequestMethod.GET)
    public ResponseEntity getStatementCashflows(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getStatementCashflows(id);
        return buildResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/changes/{id}", method = RequestMethod.GET)
    public ResponseEntity getStatementChanges(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getStatementChanges(id);
        return buildResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/generalLedgerBalance/{id}", method = RequestMethod.GET)
    public ResponseEntity getGeneralLedgerBalance(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getGeneralLedgerBalance(id);
        return buildResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/noalA/{id}", method = RequestMethod.GET)
    public ResponseEntity getNOALTrancheA(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getNOAL(id, 1);
        return buildResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/noalB/{id}", method = RequestMethod.GET)
    public ResponseEntity getNOALTrancheB(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getNOAL(id, 2);
        return buildResponse(recordsHolder);
    }
}
