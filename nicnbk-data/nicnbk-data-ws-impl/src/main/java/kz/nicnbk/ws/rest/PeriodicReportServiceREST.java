package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicReportFileParseService;
import kz.nicnbk.service.api.reporting.PeriodicReportNICKMFService;
import kz.nicnbk.service.api.reporting.PeriodicReportPrevYearInputService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.PeriodicReportHFService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.dto.common.*;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
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
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * REST Service class for periodic reports.
 *
 * Created by magzumov.
 */

@RestController
@RequestMapping("/periodicReport")
public class PeriodicReportServiceREST extends CommonServiceREST{

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportServiceREST.class);

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private PeriodicReportPEService periodicReportPEService;

    @Autowired
    private PeriodicReportHFService periodicReportHFService;

    @Autowired
    private PeriodicReportNICKMFService periodicReportNICKMFService;

    @Autowired
    private PeriodicReportFileParseService periodicReportFileParseService;

    @Autowired
    private PEScheduleInvestmentService peScheduleInvestmentService;

    @Autowired
    private PEStatementCashflowsService peStatementCashflowsService;

    @Autowired
    private HFGeneralLedgerBalanceService hfGeneralLedgerBalanceService;

    @Autowired
    private PEStatementChangesService peStatementChangesService;

    @Autowired
    private PeriodicReportPrevYearInputService prevYearInputService;

    @Autowired
    private ReserveCalculationService reserveCalculationService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TokenService tokenService;



    /* PERIODIC REPORT ************************************************************************************************/
    /**
     * Returns periodic report dto with status ok 200, status 500 error if not found.
     * @param id - report id
     * @return - periodic report dto
     */
    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getPeriodicReport(@PathVariable Long id) {
        PeriodicReportDto periodicReportDto = this.periodicReportService.getPeriodicReport(id);
        return buildNonNullResponse(periodicReportDto);
    }

    /**
     * Returns
     * @return
     */
    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity getAllPeriodicReports() {
        List<PeriodicReportDto> periodicReportsList = this.periodicReportService.getAllPeriodicReports();
        return buildNonNullResponse(periodicReportsList);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> savePeriodicReport(@RequestBody PeriodicReportDto dto) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(dto.getId() == null){
            // set creator
            dto.setCreator(username);
        }

        EntitySaveResponseDto saveResponseDto = periodicReportService.savePeriodicReport(dto, username);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/markReportFinal/{reportId}")
    public ResponseEntity<?> markReportAsFinal(@PathVariable Long reportId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean marked = false;
        try {
            marked = this.periodicReportService.markReportAsFinal(reportId);
        }catch (IllegalStateException ex){
            return buildOKWithErrorResponse(ex.getMessage());
        }
        if(marked) {
            logger.info("Successfully marked report as final : report id " + reportId + " [user " + username + "]");
            return buildOKResponse();
        }else{
            logger.error("Failed to mark report as final : report id " + reportId + " [user " + username + "]");
            return buildOKWithErrorResponse(null);
        }
    }
    /* ****************************************************************************************************************/


    /* INPUT FILE UPLOAD **********************************************************************************************/
    @RequestMapping(method = RequestMethod.POST, value = "/upload/{reportId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("reportId") Long reportId,
                                              @RequestParam(value = "file", required = true) MultipartFile file,
                                              @RequestParam(value = "fileType", required = true) String fileType) {

        // TODO: control file upload by user role

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = buildFilesDtoFromMultipart(file, fileType);
        if(filesDto != null){
            // save file
            FilesDto savedFile = this.periodicReportService.saveInputFile(reportId, filesDto);
            if(savedFile == null){
                // error occurred
                logger.error("File upload failed: error saving file (no parsing yet) [user" + username + "]");
                FileUploadResultDto result = new FileUploadResultDto(ResponseStatusType.FAIL, null, "File upload failed: error saving file (no parsing yet) [user" + username + "]", null);
                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // parse file
            FileUploadResultDto result = this.periodicReportFileParseService.parseFile(fileType, filesDto, reportId);
            if(result != null && result.getStatus() == ResponseStatusType.SUCCESS){
                result.setFileName(filesDto.getFileName());
                result.setFileId(savedFile.getId());
                logger.info("File successfully parsed: file type '" + fileType + "', file name '" + filesDto.getFileName() + "' [user " + username + "]");
                return new ResponseEntity<>(result, null, HttpStatus.OK);
            }else{
                // error
                // remove file
                boolean deleted = periodicReportService.deletePeriodicReportFileAssociationById(savedFile.getId());
                if(deleted){
                    deleted = fileService.delete(savedFile.getId());
                }

                if(!deleted){
                    logger.error("File upload and/or parse failed, error when deleting file from file system to undo actions: " +
                            "file type '" + fileType + "', file name '" + filesDto.getFileName() + "' [user " + username + "]");
                }
                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else {
            logger.error("File upload failed: no file could be found, please check your files [user " + username + "]");
            return new ResponseEntity<>(new FileUploadResultDto(ResponseStatusType.FAIL, null,
                    "File upload failed: no file could be found, please check your files", null), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/inputFilesList/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getInputFilesList(@PathVariable Long reportId) {
        PeriodicReportInputFilesHolder holder = this.periodicReportService.getPeriodicReportInputFiles(reportId);
        return buildNonNullResponse(holder);
    }
    /* ****************************************************************************************************************/


    /* NICK MF data ***************************************************************************************************/
    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/NICKMFReportingInfo/save")
    public ResponseEntity<?> saveNICKMFReportingData(@RequestBody NICKMFReportingDataHolderDto dataHolderDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntityListSaveResponseDto entityListSaveResponseDto  = this.periodicReportNICKMFService.saveNICKMFReportingData(dataHolderDto);
        if(entityListSaveResponseDto.getStatus() == ResponseStatusType.FAIL){
            logger.error("Error saving NICK MF data [user " + username + "]");
        }else{
            logger.info("Successfully saved NICK MF data [user " + username + "]");
        }
        return buildEntityListSaveResponse(entityListSaveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/NICKMFReportingInfo/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getNICKMFReportingData(@PathVariable Long reportId) {
        NICKMFReportingDataHolderDto holderDto = this.periodicReportNICKMFService.getNICKMFReportingData(reportId);
        return buildNonNullResponse(holderDto);
    }

//    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/NICKMFReportingInfoCalculatedValue/{reportId}", method = RequestMethod.POST)
//    public ResponseEntity getNICKMFReportingDataCalculatedValue(@RequestBody NICKMFReportingDataCalculatedValueRequestDto requestDto) {
//        Double value = this.periodicReportNICKMFService.getNICKMFReportingDataCalculatedValue(requestDto);
//        return buildNonNullResponse(value);
//    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/NICKMFReportingInfoPreviousMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getNICKMFReportingDataFromPreviousMonth(@PathVariable Long reportId) {
        NICKMFReportingDataHolderDto holderDto = this.periodicReportNICKMFService.getNICKMFReportingDataFromPreviousMonth(reportId);
        return buildNonNullResponse(holderDto);
    }

    /* ****************************************************************************************************************/


    /* TARRAGON GL Form ***********************************************************************************************/
    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/tarragonGeneratedForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTarragonGeneratedForm(@PathVariable Long reportId) {
        ListResponseDto responseDto  = this.periodicReportPEService.getTarragonGeneratedForm(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/tarragonGeneratedFormDataFromPreviousMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTarragonGLAddedRecordsPreviousMonth(@PathVariable Long reportId) {
        List<GeneratedGeneralLedgerFormDto> records = this.periodicReportPEService.getTarragonGLAddedRecordsPreviousMonth(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/PEGeneralLedgerFormData/save")
    public ResponseEntity<?> savePEGeneralLedgerFormData(@RequestBody PEGeneralLedgerFormDataHolderDto dataHolderDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntityListSaveResponseDto entityListSaveResponseDto = this.periodicReportPEService.savePEGeneralLedgerFormData(dataHolderDto);

        if(entityListSaveResponseDto.getStatus() == ResponseStatusType.FAIL){
            logger.error("Error saving Tarragon GL Form data [user " + username + "]");
        }else{
            logger.info("Successfully saved Tarragon GL Form data [user " + username + "]");
        }

        return buildEntityListSaveResponse(entityListSaveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "/updatedTarragonInvestment")
    public ResponseEntity<?> saveUpdatedTarragonInvestment(@RequestBody UpdateTarragonInvestmentDto updateDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto entitySaveResponseDto  = this.peScheduleInvestmentService.updateScheduleInvestments(updateDto);

        if(entitySaveResponseDto.getStatus() == ResponseStatusType.FAIL){
            logger.error("Error updating Tarragon Investment: '" + updateDto.getFundName() + "' [user " + username + "]");
        }else{
            logger.info("Successfully updated Tarragon Investment: '" + updateDto.getFundName() + "' [user " + username + "]");
        }

        return buildEntitySaveResponse(entitySaveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/PEGeneralLedgerFormData/delete/{recordId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deletePEGeneralLedgerFormDataRecord(@PathVariable(value="recordId") Long recordId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        boolean deleted = this.periodicReportPEService.deletePEGeneralLedgerFormDataRecordById(recordId);
        if(deleted){
            logger.info("Successfully deleted Tarragon GL Form record: record id " + recordId + " [user " + username + "]");
        }else{
            logger.error("Failed to delete Tarragon GL Form record: record id " + recordId + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    /* ****************************************************************************************************************/



    /* SINGULARITY ****************************************************************************************************/
    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/saveSingularityAdjustments/{reportId}", method = RequestMethod.POST)
    public ResponseEntity saveSingularityAdjustments(@PathVariable Long reportId, @RequestBody SingularityAdjustmentsDto adjustmentsDto) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.hfGeneralLedgerBalanceService.saveAdjustments(adjustmentsDto, username);

        if(!saved){
            logger.error("Error saving Singularity adjustments: report id " + reportId + " [user " + username + "]");
        }else{
            logger.info("Successfully saved Singularity adjustments: report id " + reportId + " [user " + username + "]");
        }
        return buildNonNullResponse(saved);
    }

//    @Deprecated
//    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/saveKZTReportForm13InterestRate/{reportId}", method = RequestMethod.POST)
//    public ResponseEntity saveKZTReportForm13InterestRate(@PathVariable Long reportId, @RequestBody String interestRate) {
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//        EntitySaveResponseDto entitySaveResponseDto = this.periodicReportService.saveInterestRate(reportId, interestRate, username);
//        return buildNonNullResponse(entitySaveResponseDto);
//    }

    /* ****************************************************************************************************************/


    /* PREVIOUS YEAR INPUT ********************************************************************************************/
    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/previousYearInput/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getPreviousYearInputData(@PathVariable Long reportId) {
        List<PreviousYearInputDataDto> records = this.prevYearInputService.getPreviousYearInputData(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/previousYearInputPrevMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getPreviousYearInputDataFromPreviousMonth(@PathVariable Long reportId) {
        List<PreviousYearInputDataDto> records = this.prevYearInputService.getPreviousYearInputDataFromPreviousMonth(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/previousYearInput/{reportId}", method = RequestMethod.POST)
    public ResponseEntity savePreviousYearInputData(@PathVariable Long reportId, @RequestBody List<PreviousYearInputDataDto> records) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntityListSaveResponseDto entityListSaveResponseDto = this.prevYearInputService.savePreviousYearInputData(records, reportId);
        if(entityListSaveResponseDto.getStatus() == ResponseStatusType.FAIL){
            logger.error("Error saving Previous Year input data: report id " + reportId + " [user " + username + "]");
        }else{
            logger.info("Successfully saved Previous Year input data: report id " + reportId + " [user " + username + "]");
        }
        return buildEntityListSaveResponse(entityListSaveResponseDto);
    }
    /* ****************************************************************************************************************/


    /* GET PARSED DATA ****************************************************************************************************/
    @RequestMapping(value="/deleteFile/{fileId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> safeDeleteFile(@PathVariable(value="fileId") Long fileId){
        //TODO: ? check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.periodicReportService.safeDeleteFile(fileId, username);
        if(deleted){
            logger.info("Successfully deleted (safe) file : file id " + fileId + " [user " + username + "]");
        }else{
            logger.info("Error deleting(safe) file : file id " + fileId + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/scheduleInvestments/{id}", method = RequestMethod.GET)
    public ResponseEntity getScheduleInvestments(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.peScheduleInvestmentService.get(id);
        return buildNonNullResponse(recordsHolder);
    }


    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/balanceOperations/{id}", method = RequestMethod.GET)
    public ResponseEntity getBalanceAndOperations(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getStatementBalanceOperations(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/cashflows/{id}", method = RequestMethod.GET)
    public ResponseEntity getStatementCashflows(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.peStatementCashflowsService.get(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/changes/{id}", method = RequestMethod.GET)
    public ResponseEntity getStatementChanges(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.peStatementChangesService.get(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/generalLedgerBalance/{id}", method = RequestMethod.GET)
    public ResponseEntity getGeneralLedgerBalance(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.hfGeneralLedgerBalanceService.get(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/singularGeneratedForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getSingularGeneratedForm(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportHFService.getSingularGeneratedForm(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/noalA/{id}", method = RequestMethod.GET)
    public ResponseEntity getNOALTrancheA(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getNOAL(id, 1);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/noalB/{id}", method = RequestMethod.GET)
    public ResponseEntity getNOALTrancheB(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getNOAL(id, 2);
        return buildNonNullResponse(recordsHolder);
    }
    /* ****************************************************************************************************************/


    /* REPORT FORMS GENERATION ****************************************************************************************/
    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceUSDForm(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceUSDForm(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedIncomeExpenseUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedIncomeExpenseUSDForm(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.generateConsolidatedIncomeExpenseUSDForm(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedTotalIncomeUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedTotalIncomeUSDForm(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.generateConsolidatedTotalIncomeUSDForm(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm1/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm1(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm1(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm2/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm2(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedIncomeExpenseKZTForm2(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm3/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm3(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedTotalIncomeKZTForm3(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm6/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm6(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm6(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm7/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm7(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm7(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm8/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm8(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm8(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm10/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm10(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm10(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm13/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm13(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm13(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm14/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm14(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm14(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm19/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm19(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm19(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/consolidatedBalanceKZTForm22/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm22(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm22(reportId);
        return buildNonNullResponse(responseDto);
    }
    /* ****************************************************************************************************************/


    /* EXPORT GENERATED REPORT ****************************************************************************************/
    @RequestMapping(value="/export/{reportId}/{type}", method= RequestMethod.GET)
    @ResponseBody
    public void exportReport(@PathVariable(value="reportId") Long reportId,
                             @PathVariable(value = "type") String type,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        FilesDto filesDto = null;
        try{
            filesDto = this.periodicReportService.getExportFileStream(reportId, type);
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
            logger.error("(PeriodicReport) File export request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(PeriodicReport) File export request failed: io exception", e);
        } catch (Exception e){
            logger.error("(PeriodicReport) File export request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e) {
            logger.error("(PeriodicReport) File export: failed to close input stream", e);
        }
    }
    /* ****************************************************************************************************************/


    /* RESERVE CALCULATIONS *******************************************************************************************/
    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/reserveCalculation/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculationRecords() {
        List<ReserveCalculationDto> responseDto  = this.reserveCalculationService.getAllReserveCalculations();
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/searchReserveCalculations/", method = RequestMethod.POST)
    public ResponseEntity searchReserveCalculationRecords(@RequestBody ReserveCalculationSearchParams searchParams) {
        ReserveCalculationPagedSearchResult responseDto  = this.reserveCalculationService.search(searchParams);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/reserveCalculationSave/", method = RequestMethod.POST)
    public ResponseEntity saveReserveCalculation( @RequestBody List<ReserveCalculationDto> records) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.reserveCalculationService.save(records);
        if(saved){
            logger.info("Successfully saved reserve calculation records [user " + username + "]");

            // TODO: response from DB, not UI
            return buildEntitySaveResponse(null, new Date());
        }else {
            // error occurred
            logger.error("Failed to save reserve calculation records [user " + username + "]");
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/reserveCalculation/delete/{recordId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteReserveCalculationRecord(@PathVariable(value="recordId") Long recordId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();


        boolean deleted = this.reserveCalculationService.deleteReserveCalculationRecord(recordId);
        if(deleted){
            logger.info("Successfully deleted Reserve Calculation record: record id " + recordId + " [user " + username + "]");
        }else{
            logger.error("Failed to delete  Reserve Calculation record: record id " + recordId + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    @RequestMapping(value="reserveCalculation/export/{recordId}/{type}", method= RequestMethod.POST)
    //@ResponseBody
    public void exportCapitalCall(@PathVariable(value="recordId") Long recordId,
                                  @PathVariable(value = "type") String type,
                                  @RequestBody ReserveCalculationExportParamsDto exportParamsDto,
                                  HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        FilesDto filesDto = null;
        try{
            filesDto = this.reserveCalculationService.getExportFileStream(recordId, type, exportParamsDto);
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

        if(type.equalsIgnoreCase("ORDER")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        }else{
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
        try {
            //fileName = URLEncoder.encode(fileName, "UTF-8");
            //fileName = URLDecoder.decode(fileName, "ISO8859_1");
            //response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + "\"");
            response.setHeader("Content-disposition", "attachment;");
            org.apache.commons.io.IOUtils.copy(filesDto.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        } catch (UnsupportedEncodingException e) {
            logger.error("(PeriodicReport) File export request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(PeriodicReport) File export request failed: io exception", e);
        } catch (Exception e){
            logger.error("(PeriodicReport) File export request failed", e);
        }
        try {
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e) {
            logger.error("(PeriodicReport) File export: failed to close input stream", e);
        }
    }
    /* ****************************************************************************************************************/
}
