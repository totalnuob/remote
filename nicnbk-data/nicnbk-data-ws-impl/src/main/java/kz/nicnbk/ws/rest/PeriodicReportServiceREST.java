package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.*;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.PeriodicReportHFService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.api.reporting.realestate.PeriodicReportREService;
import kz.nicnbk.service.api.reporting.realestate.REGeneralLedgerBalanceService;
import kz.nicnbk.service.dto.common.*;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.hedgefunds.ExcludeSingularityRecordDto;
import kz.nicnbk.service.dto.reporting.nickmf.NICKMFReportingDataCalculatedValueRequestDto;
import kz.nicnbk.service.dto.reporting.privateequity.ExcludeTarragonRecordDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonGeneratedGeneralLedgerFormDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonStatementBalanceOperationsHolderDto;
import kz.nicnbk.service.dto.reporting.realestate.*;
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
import java.util.Set;

/**
 * REST Service class for periodic reports.
 *
 * Created by magzumov.
 */

@RestController
@RequestMapping("/periodicReport")
public class PeriodicReportServiceREST extends CommonServiceREST{

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportServiceREST.class);

    private static final String VIEWER_ROLE = "hasRole('ROLE_REPORTING_VIEWER') OR hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')";
    private static final String EDITOR_ROLE = "hasRole('ROLE_REPORTING_EDITOR') OR hasRole('ROLE_ADMIN')";

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
    private REGeneralLedgerBalanceService realEstateGeneralLedgerBalanceService;

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

    @Autowired
    private PeriodicReportREService realEstateService;

    @Autowired
    private PeriodicDataService periodicDataService;

    /* PERIODIC REPORT ************************************************************************************************/
    /**
     * Returns periodic report dto by id.
     * Returns status 500 error if not found.
     * @param id - report id
     * @return - periodic report dto
     */
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getPeriodicReport(@PathVariable Long id) {
        PeriodicReportDto periodicReportDto = this.periodicReportService.getPeriodicReport(id);
        return buildNonNullResponse(periodicReportDto);
    }

    /**
     * Returns the list of all periodic reports.
     * @return - list of reports
     */
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity getAllPeriodicReports() {
        List<PeriodicReportDto> periodicReportsList = this.periodicReportService.getAllPeriodicReports();
        return buildNonNullResponse(periodicReportsList);
    }

    @PreAuthorize(EDITOR_ROLE)
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

    @PreAuthorize(EDITOR_ROLE)
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
            return buildOKResponse();
        }else{
            return buildOKWithErrorResponse(null);
        }
    }

    /**
     * Returns periodic data mathcing specified search params.
     * @param searchParams - search parameters
     * @return - periodic data
     */
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/searchPeriodicData", method = RequestMethod.POST)
    public ResponseEntity searchPeriodicData(@RequestBody PeriodicDataSearchParamsDto searchParams) {
        PeriodicDataPagedSearchResultDto searchResults = this.periodicDataService.searchPeriodicData(searchParams);
        return buildNonNullResponse(searchResults);
    }

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(value = "/savePeriodicData", method = RequestMethod.POST)
    public ResponseEntity<?> savePeriodicData(@RequestBody PeriodicDataDto dto) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.periodicDataService.save(dto, username);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(value = "/deletePeriodicData/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deletePeriodicDataRecord(@PathVariable Long id){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.periodicDataService.delete(id, username);
        return buildDeleteResponseEntity(deleted);
    }
    /* ****************************************************************************************************************/


    /* INPUT FILE UPLOAD **********************************************************************************************/
    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(method = RequestMethod.POST, value = "/upload/{reportId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("reportId") Long reportId,
                                              @RequestParam(value = "file", required = true) MultipartFile file,
                                              @RequestParam(value = "fileType", required = true) String fileType) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        FilesDto filesDto = buildFilesDtoFromMultipart(file, fileType);
        filesDto.setType(fileType);
        FileUploadResultDto fileUploadResultDto = this.periodicReportFileParseService.handleFileUpload(filesDto, reportId, username);

        return buildFileUploadResultResponseEntity(fileUploadResultDto);
//        if(filesDto != null){
//            // save file
//            FilesDto savedFile = this.periodicReportService.saveInputFile(reportId, filesDto);
//            if(savedFile == null){
//                // error occurred
//                logger.error("File upload failed: error saving file (no parsing yet) [user" + username + "]");
//                FileUploadResultDto result = new FileUploadResultDto(ResponseStatusType.FAIL, null, "File upload failed: error saving file (no parsing yet) [user" + username + "]", null);
//                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//
//            // parse file
//            FileUploadResultDto result = this.periodicReportFileParseService.parseFile(fileType, filesDto, reportId, username);
//            if(result != null && result.getStatus() == ResponseStatusType.SUCCESS){
//                result.setFileName(filesDto.getFileName());
//                result.setFileId(savedFile.getId());
//                logger.info("File successfully parsed: file type '" + fileType + "', file name '" + filesDto.getFileName() + "' [user " + username + "]");
//                return new ResponseEntity<>(result, null, HttpStatus.OK);
//            }else{
//                // error
//                // remove file
//                boolean deleted = periodicReportService.deletePeriodicReportFileAssociationById(savedFile.getId());
//                if(deleted){
//                    deleted = fileService.delete(savedFile.getId());
//                }
//
//                if(!deleted){
//                    logger.error("File upload and/or parse failed, error when deleting file from file system to undo actions: " +
//                            "file type '" + fileType + "', file name '" + filesDto.getFileName() + "' [user " + username + "]");
//                }
//                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }else {
//            logger.error("File upload failed: no file could be found, please check your files [user " + username + "]");
//            return new ResponseEntity<>(new FileUploadResultDto(ResponseStatusType.FAIL, null,
//                    "File upload failed: no file could be found, please check your files", null), null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/inputFilesList/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getInputFilesList(@PathVariable Long reportId) {
        PeriodicReportInputFilesHolder holder = this.periodicReportService.getPeriodicReportInputFiles(reportId);
        return buildNonNullResponse(holder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/fundRenameInfo/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getFundRenameInfo(@PathVariable Long reportId){
        ReportingFundRenameInfoDto fundRenameInfo = this.periodicReportService.getFundRenameInfo(reportId);
        return buildNonNullResponse(fundRenameInfo);
    }

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(method = RequestMethod.POST, value = "/saveFundRenameInfo")
    public ResponseEntity<?> saveFundRenameInfo(@RequestBody ReportingFundRenameInfoDto fundRenameInfoDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.periodicReportService.saveFundRenameInfo(fundRenameInfoDto);
        return buildEntitySaveResponseEntity(saved);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/getFundNameList/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getFundNameListHolder(@PathVariable Long reportId){
        ReportingFundNameListHolderDto fundNameList= this.periodicReportService.getFundNameList(reportId);
        return buildNonNullResponse(fundNameList);
    }
    /* ****************************************************************************************************************/


    /* NICK MF data ***************************************************************************************************/
    @PreAuthorize(EDITOR_ROLE)
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

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/NICKMFReportingInfo/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getNICKMFReportingData(@PathVariable Long reportId) {
        NICKMFReportingDataHolderDto holderDto = this.periodicReportNICKMFService.getNICKMFReportingData(reportId);
        return buildNonNullResponse(holderDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/NICKMFReportingInfoCalculatedValue/", method = RequestMethod.POST)
    public ResponseEntity getNICKMFReportingDataCalculatedValue(@RequestBody NICKMFReportingDataCalculatedValueRequestDto requestDto) {
        Double value = this.periodicReportNICKMFService.getNICKMFReportingDataCalculatedValue(requestDto);
        return buildNonNullResponse(value);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/NICKMFReportingInfoPreviousMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getNICKMFReportingDataFromPreviousMonth(@PathVariable Long reportId) {
        NICKMFReportingDataHolderDto holderDto = this.periodicReportNICKMFService.getNICKMFReportingDataFromPreviousMonth(reportId);
        return buildNonNullResponse(holderDto);
    }

    /* ****************************************************************************************************************/


    /* TARRAGON GL Form ***********************************************************************************************/
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/tarragonGeneratedForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTarragonGeneratedForm(@PathVariable Long reportId) {
        ListResponseDto responseDto  = this.periodicReportPEService.getTarragonGeneratedFormWithExcluded(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/tarragonGeneratedFormDataFromPreviousMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTarragonGLAddedRecordsPreviousMonth(@PathVariable Long reportId) {
        List<TarragonGeneratedGeneralLedgerFormDto> records = this.periodicReportPEService.getTarragonGLAddedRecordsPreviousMonth(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize(EDITOR_ROLE)
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

    @PreAuthorize(EDITOR_ROLE)
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

    @PreAuthorize(EDITOR_ROLE)
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

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(method = RequestMethod.POST, value = "/excludeTarragonRecord")
    public ResponseEntity<?> excludeIncludeTarragonRecord(@RequestBody ExcludeTarragonRecordDto excludeTarragonRecordDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean changed = this.periodicReportPEService.excludeIncludeTarragonRecord(excludeTarragonRecordDto, username);
        return buildDeleteResponseEntity(changed);
    }

    /* ****************************************************************************************************************/


    /* TERRA GL Form ***********************************************************************************************/
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/terraGeneralLedgerForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTerraGeneraLLedgerForm(@PathVariable Long reportId) {
        ListResponseDto responseDto  = this.realEstateService.getTerraGeneralLedgerFormData(reportId);
        return buildNonNullResponse(responseDto);
    }

//    @PreAuthorize(VIEWER_ROLE)
//    @RequestMapping(value = "/terraGeneratedForm/{reportId}", method = RequestMethod.GET)
//    public ResponseEntity getTerraGeneratedForm(@PathVariable Long reportId) {
//        ListResponseDto responseDto  = this.realEstateService.getTerraGeneratedForm(reportId);
//        return buildNonNullResponse(responseDto);
//    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/terraGeneratedFormDataFromPreviousMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getTerraGLAddedRecordsPreviousMonth(@PathVariable Long reportId) {
        List<TerraGeneratedGeneralLedgerFormDto> records = this.realEstateService.getTerraGLAddedRecordsPreviousMonth(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(value="/RealEstateGeneralLedgerFormData/delete/{recordId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteRealEstateGeneralLedgerFormDataRecord(@PathVariable(value="recordId") Long recordId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        boolean deleted = this.realEstateService.deleteRealEstateGeneralLedgerFormDataRecordById(recordId);
        if(deleted){
            logger.info("Successfully deleted Terra GL Form record: record id " + recordId + " [user " + username + "]");
        }else{
            logger.error("Failed to delete Terra GL Form record: record id " + recordId + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(method = RequestMethod.POST, value = "/RealEstateGeneralLedgerFormData/save")
    public ResponseEntity<?> saveRealEstateGeneralLedgerFormData(@RequestBody RealEstateGeneralLedgerFormDataHolderDto dataHolderDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntityListSaveResponseDto entityListSaveResponseDto = this.realEstateService.saveRealEstateGeneralLedgerFormData(dataHolderDto);

        if(entityListSaveResponseDto.getStatus() == ResponseStatusType.FAIL){
            logger.error("Error saving Terra GL Form data [user " + username + "]");
        }else{
            logger.info("Successfully saved Terra GL Form data [user " + username + "]");
        }

        return buildEntityListSaveResponse(entityListSaveResponseDto);
    }


    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(method = RequestMethod.POST, value = "/excludeTerraRecord")
    public ResponseEntity<?> excludeIncludeTerraRecord(@RequestBody ExcludeTerraRecordDto excludeTerraRecordDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean changed = this.realEstateService.excludeIncludeTerraRecord(excludeTerraRecordDto, username);
        return buildDeleteResponseEntity(changed);
    }

    /* ****************************************************************************************************************/


    /* SINGULARITY ****************************************************************************************************/
    @PreAuthorize(EDITOR_ROLE)
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

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/singularGeneratedForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getSingularGeneratedForm(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportHFService.getSingularGeneratedForm(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(method = RequestMethod.POST, value = "/excludeSingularityRecord")
    public ResponseEntity<?> excludeIncludeSingularityRecord(@RequestBody ExcludeSingularityRecordDto excludeRecordDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean changed = this.periodicReportHFService.excludeIncludeSingularityRecord(excludeRecordDto, username);
        return buildDeleteResponseEntity(changed);
    }

//    @Deprecated
//    @PreAuthorize(EDITOR_ROLE)
//    @RequestMapping(value = "/saveKZTReportForm13InterestRate/{reportId}", method = RequestMethod.POST)
//    public ResponseEntity saveKZTReportForm13InterestRate(@PathVariable Long reportId, @RequestBody String interestRate) {
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//        EntitySaveResponseDto entitySaveResponseDto = this.periodicReportService.saveInterestRate(reportId, interestRate, username);
//        return buildNonNullResponse(entitySaveResponseDto);
//    }

    /* ****************************************************************************************************************/


    /* PREVIOUS YEAR INPUT ********************************************************************************************/
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/previousYearInput/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getPreviousYearInputData(@PathVariable Long reportId) {
        List<PreviousYearInputDataDto> records = this.prevYearInputService.getPreviousYearInputData(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/previousYearInputPrevMonth/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getPreviousYearInputDataFromPreviousMonth(@PathVariable Long reportId) {
        List<PreviousYearInputDataDto> records = this.prevYearInputService.getPreviousYearInputDataFromPreviousMonth(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize(EDITOR_ROLE)
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

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/scheduleInvestments/{id}", method = RequestMethod.GET)
    public ResponseEntity getScheduleInvestments(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.peScheduleInvestmentService.get(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/soiReport/{id}", method = RequestMethod.GET)
    public ResponseEntity getSOIReport(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.peScheduleInvestmentService.getSOIReport(id);
        return buildNonNullResponse(recordsHolder);
    }


    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/balanceOperations/{id}", method = RequestMethod.GET)
    public ResponseEntity getBalanceAndOperations(@PathVariable Long id) {
        TarragonStatementBalanceOperationsHolderDto recordsHolder = this.periodicReportService.getStatementBalanceOperations(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/cashflows/{id}", method = RequestMethod.GET)
    public ResponseEntity getStatementCashflows(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.peStatementCashflowsService.get(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/changes/{id}", method = RequestMethod.GET)
    public ResponseEntity getStatementChanges(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.peStatementChangesService.get(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/generalLedgerBalance/{id}", method = RequestMethod.GET)
    public ResponseEntity getSingularityGeneralLedgerBalance(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.hfGeneralLedgerBalanceService.getWithExcludedRecords(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/noalA/{id}", method = RequestMethod.GET)
    public ResponseEntity getNOALTrancheA(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getNOAL(id, 1);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/noalB/{id}", method = RequestMethod.GET)
    public ResponseEntity getNOALTrancheB(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getNOAL(id, 2);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/ITD/{id}", method = RequestMethod.GET)
    public ResponseEntity getSingularityITD(@PathVariable Long id) {
        ConsolidatedReportRecordHolderDto recordsHolder = this.periodicReportService.getSingularityITD(id);
        return buildNonNullResponse(recordsHolder);
    }

//    @PreAuthorize(VIEWER_ROLE)
//    @RequestMapping(value = "/get/re/generalLedgerBalance/{id}", method = RequestMethod.GET)
//    public ResponseEntity getRealEstateGeneralLedgerBalance(@PathVariable Long id) {
//        ConsolidatedReportRecordHolderDto recordsHolder = this.realEstateGeneralLedgerBalanceService.get(id);
//        return buildNonNullResponse(recordsHolder);
//    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/terraCombined/{id}", method = RequestMethod.GET)
    public ResponseEntity getTerraCombined(@PathVariable Long id) {
        TerraCombinedDataHolderDto recordsHolder = this.realEstateService.getTerraCombinedParsedData(id);
        return buildNonNullResponse(recordsHolder);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/get/terraGeneralLedger/{id}", method = RequestMethod.GET)
    public ResponseEntity getTerraGeneralLedger(@PathVariable Long id) {
        TerraGeneralLedgerDataHolderDto recordsHolder = this.realEstateService.getTerraGeneralLedgerDataWithoutExcluded(id);
        return buildNonNullResponse(recordsHolder);
    }
    /* ****************************************************************************************************************/


    /* REPORT FORMS GENERATION ****************************************************************************************/
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedBalanceUSDForm(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceUSDForm(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedIncomeExpenseUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedIncomeExpenseUSDForm(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.generateConsolidatedIncomeExpenseUSDForm(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedTotalIncomeUSDForm/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedTotalIncomeUSDForm(@PathVariable Long reportId) {
        List<ConsolidatedBalanceFormRecordDto> records = this.periodicReportService.generateConsolidatedTotalIncomeUSDForm(reportId);
        return buildNonNullResponse(records);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm1/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm1(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm1(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm2/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm2(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedIncomeExpenseKZTForm2(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm3/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm3(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedTotalIncomeKZTForm3(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm6/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm6(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm6(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm7/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm7(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm7(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm8/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm8(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm8(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm10/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm10(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm10(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm13/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm13(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm13(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm14/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm14(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm14(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/consolidatedBalanceKZTForm19/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getConsolidatedKZTForm19(@PathVariable Long reportId) {
        ListResponseDto responseDto = this.periodicReportService.generateConsolidatedBalanceKZTForm19(reportId);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
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

    @RequestMapping(value="/exportAll/{reportId}", method= RequestMethod.GET)
    @ResponseBody
    public void exportAllReports(@PathVariable(value="reportId") Long reportId, HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        FilesDto filesDto = null;
        try{
            filesDto = this.periodicReportService.getExportAllKZTReportsFileStream(reportId);
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

        response.setContentType("application/zip");
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
    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/reserveCalculation/", method = RequestMethod.GET)
    public ResponseEntity getReserveCalculationRecords() {
        List<ReserveCalculationDto> responseDto  = this.reserveCalculationService.getAllReserveCalculations();
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(VIEWER_ROLE)
    @RequestMapping(value = "/searchReserveCalculations/", method = RequestMethod.POST)
    public ResponseEntity searchReserveCalculationRecords(@RequestBody ReserveCalculationSearchParams searchParams) {
        ReserveCalculationPagedSearchResult responseDto  = this.reserveCalculationService.search(searchParams);
        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(value = "/reserveCalculationSave/", method = RequestMethod.POST)
    public ResponseEntity saveReserveCalculationList( @RequestBody List<ReserveCalculationDto> records) {
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

    @PreAuthorize(EDITOR_ROLE)
    @RequestMapping(value = "/reserveCalculationRecordSave/", method = RequestMethod.POST)
    public ResponseEntity saveReserveCalculation( @RequestBody ReserveCalculationDto record) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto entitySaveResponseDto = this.reserveCalculationService.save(record, username);
        return buildEntitySaveResponse(entitySaveResponseDto);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reserveCalculation/uploadAttachment/{recordId}")
    public ResponseEntity<?> reserveCalculationFileUpload(@PathVariable("recordId") Long recordId,
                                              @RequestParam(value = "file", required = true) MultipartFile[] files,
                                              @RequestParam(value = "fileType", required = true) String fileType) {

        // TODO: control file upload by user role

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Set<FilesDto> filesDto = buildFilesDtoFromMultipart(files, fileType);
        if(filesDto != null){
            Set<FilesDto> savedAttachments = this.reserveCalculationService.saveAttachments(recordId, filesDto);
            if(savedAttachments == null){
                // error occurred
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }else{
                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);

    }

    @RequestMapping(value="/reserveCalculationAttachmentDelete/{recordId}/{fileId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteReserveCalculationRecordFile(@PathVariable(value="recordId") Long recordId, @PathVariable(value="fileId") Long fileId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // TODO: check if can edit

        boolean deleted = this.reserveCalculationService.deleteReserveCalculationAttachment(recordId, fileId, username);
        return buildDeleteResponseEntity(deleted);
    }


    @PreAuthorize(EDITOR_ROLE)
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
