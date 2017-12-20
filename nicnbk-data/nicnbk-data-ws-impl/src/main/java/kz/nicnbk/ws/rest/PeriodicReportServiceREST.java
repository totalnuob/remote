package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.ws.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@RestController
@RequestMapping("/periodicReport")
public class PeriodicReportServiceREST extends CommonServiceREST{

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportServiceREST.class);

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
        PeriodicReportInputFilesHolder holder = this.periodicReportService.getPeriodicReportFiles(reportId);
        return buildResponse(holder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
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

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/NICKMFReportingInfo/save")
    public ResponseEntity<?> saveNICKMFReportingData(@RequestBody NICKMFReportingDataHolderDto dataHolderDto){
        boolean saved = this.periodicReportService.saveNICKMFReportingData(dataHolderDto);
        if(saved){

            // TODO: response from DB, not UI
            Date createDate = new Date();

            return buildEntitySaveResponse(null, createDate);
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
    @RequestMapping(value = "/tarragonGeneratedFormDataFromPreviousMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTarragonGLAddedRecordsPreviousMonth(@PathVariable Long reportId) {
        List<GeneratedGeneralLedgerFormDto> records = this.periodicReportService.getTarragonGLAddedRecordsPreviousMonth(reportId);
        return new ResponseEntity<>(records, null, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/NICKMFReportingInfo/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getNICKMFReportingData(@PathVariable Long reportId) {
        NICKMFReportingDataHolderDto holderDto = this.periodicReportService.getNICKMFReportingData(reportId);
        return new ResponseEntity<>(holderDto, null, HttpStatus.OK);
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


    @RequestMapping(value="/deleteFile/{fileId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> safeDeleteFile(@PathVariable(value="fileId") Long fileId){

        //TODO: ? check rights ?

        boolean deleted = this.periodicReportService.safeDeleteFile(fileId);
        return buildDeleteResponseEntity(deleted);
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

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/singularGeneratedForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getSingularGeneratedForm(@PathVariable Long reportId) {
        List<GeneratedGeneralLedgerFormDto> records = this.periodicReportService.getSingularGeneratedForm(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/tarragonGeneratedForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTarragonGeneratedForm(@PathVariable Long reportId) {
        ListResponseDto responseDto  = this.periodicReportService.getTarragonGeneratedForm(reportId);
        return buildResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/reserveCalculation/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculation() {
        List<ReserveCalculationDto> responseDto  = this.periodicReportService.getReserveCalculation();
        return buildResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/reserveCalculationSave/", method = RequestMethod.POST)
    public ResponseEntity saveReserveCalculation( @RequestBody List<ReserveCalculationDto> records) {
        boolean saved = this.periodicReportService.saveReserveCalculation(records);
        if(saved){
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(null, new Date());
        }else {
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceUSDForm(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.getConsolidatedBalanceUSDForm(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedIncomeExpenseUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedIncomeExpenseUSDForm(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.getConsolidatedIncomeExpenseUSDForm(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedTotalIncomeUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedTotalIncomeUSDForm(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.getConsolidatedTotalIncomeUSDForm(reportId);
        return buildResponse(records);
    }


    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm8/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceKZTForm8(@PathVariable Long reportId) {
        List<ConsolidatedKZTForm8RecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm8(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm10/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceKZTForm10(@PathVariable Long reportId) {
        List<ConsolidatedKZTForm10RecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm10(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm14/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceKZTForm14(@PathVariable Long reportId) {
        List<ConsolidatedKZTForm14RecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm14(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm13/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceKZTForm13(@PathVariable Long reportId) {
        List<ConsolidatedKZTForm13RecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm13(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm7/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceKZTForm7(@PathVariable Long reportId) {
        List<ConsolidatedKZTForm7RecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm7(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm1/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceKZTForm1(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm1(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm2/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm2(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.getConsolidatedIncomeExpenseKZTForm2(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm3/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm3(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.getConsolidatedTotalIncomeKZTForm3(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm19/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm19(@PathVariable Long reportId) {
        List<ConsolidatedKZTForm19RecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm19(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm22/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm22(@PathVariable Long reportId) {
        List<ConsolidatedKZTForm22RecordDto> records = this.periodicReportService.getConsolidatedBalanceKZTForm22(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/previousYearInput/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getPreviousYearInputData(@PathVariable Long reportId) {
        List<PreviousYearInputDataDto> records = this.periodicReportService.getPreviousYearInputData(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/previousYearInputPrevMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getPreviousYearInputDataFromPreviousMonth(@PathVariable Long reportId) {
        List<PreviousYearInputDataDto> records = this.periodicReportService.getPreviousYearInputDataFromPreviousMonth(reportId);
        return buildResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/previousYearInput/{reportId}", method = RequestMethod.POST)
    public ResponseEntity savePreviousYearInputData(@PathVariable Long reportId, @RequestBody List<PreviousYearInputDataDto> records) {
        boolean saved = this.periodicReportService.savePreviousYearInputData(records, reportId);
        if(saved){

            // TODO: response from DB, not UI

            return buildEntitySaveResponse(null, new Date());
        }else {
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/PEGeneralLedgerFormData/save")
    public ResponseEntity<?> savePEGeneralLedgerFormData(@RequestBody PEGeneralLedgerFormDataHolderDto dataHolderDto){
        boolean saved = this.periodicReportService.savePEGeneralLedgerFormData(dataHolderDto);
        if(saved){

            // TODO: response from DB, not UI

            return buildEntitySaveResponse(null, new Date());
        }else {
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/updatedTarragonInvestment")
    public ResponseEntity<?> saveUpdatedTarragonInvestment(@RequestBody UpdateTarragonInvestmentDto updateDto){
        boolean saved = this.periodicReportService.saveUpdatedTarragonInvestment(updateDto);
        if(saved){

            // TODO: response from DB, not UI

            return buildEntitySaveResponse(null, new Date());
        }else {
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value="/PEGeneralLedgerFormData/delete/{recordId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deletePEGeneralLedgerFormDataRecord(@PathVariable(value="recordId") Long recordId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();


        // TODO: check rights


        boolean deleted = this.periodicReportService.deletePEGeneralLedgerFormDataRecordById(recordId);

        return buildDeleteResponseEntity(deleted);
    }

    //@PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/markReportFinal/{reportId}")
    public ResponseEntity<?> markReportAsFinal(@PathVariable Long reportId){

        boolean marked = this.periodicReportService.markReportAsFinal(reportId);
        if(marked) {
            Response response = new Response();
            response.setSuccess(marked);
            return new ResponseEntity<>(response, null, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/export/{reportId}/{type}", method= RequestMethod.GET)
    @ResponseBody
    public void exportReport(@PathVariable(value="reportId") Long reportId,
                             @PathVariable(value = "type") String type,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        InputStream inputStream = this.periodicReportService.getExportFileStream(reportId, type);
        if(inputStream == null){

            // TODO: handle error ??/
            return;
            //return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        //String fileName = "Конс_баланс_USD"; // TODO: change file name by report type
        try {
            //fileName = URLEncoder.encode(fileName, "UTF-8");
            //fileName = URLDecoder.decode(fileName, "ISO8859_1");
            //response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + "\"");
            response.setHeader("Content-disposition", "attachment;");
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


}
