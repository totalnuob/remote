package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;
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
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/screening")
public class HedgeFundScreeningServiceREST extends CommonServiceREST{

    private static final Logger logger = LoggerFactory.getLogger(HedgeFundScreeningServiceREST.class);

    @Autowired
    private HedgeFundScreeningService screeningService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        HedgeFundScreeningDto dto = this.screeningService.getScreening(id);
        return buildNonNullResponse(dto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity getAll(){
        List<HedgeFundScreeningDto> dto = this.screeningService.getAllScreenings();
        return buildNonNullResponse(dto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundScreeningDto screeningDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = this.screeningService.saveScreening(screeningDto, username);

        return buildEntitySaveResponse(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/file/upload/{screeningId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("screeningId") Long screeningId,
                                              @RequestParam(value = "file", required = false)MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        HedgeFundScreeningDto dto = screeningService.getScreening(screeningId);
        // TODO: Check roles and permissions!

        if(files == null || files.length != 1){
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilesDto filesDto = buildFilesDtoFromMultipart(files[0], FileTypeLookup.HF_SCREENING_DATA_FILE.getCode());
        if (filesDto != null) {
            FileUploadResultDto result = this.screeningService.saveAndParseAttachmentDataFile(screeningId, filesDto, username);
            if (result != null && result.getStatus() == ResponseStatusType.SUCCESS) {
                this.screeningService.sendEmailNotificationForAudit(screeningId, filesDto, username);
                return new ResponseEntity<>(result, null, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/file/ucits/upload/{screeningId}")
    public ResponseEntity<?> handleUcitsFileUpload(@PathVariable("screeningId") Long screeningId,
                                              @RequestParam(value = "file", required = false)MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        HedgeFundScreeningDto dto = screeningService.getScreening(screeningId);
        // TODO: Check roles and permissions!

        if(files == null || files.length != 1){
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilesDto filesDto = buildFilesDtoFromMultipart(files[0], FileTypeLookup.HF_SCREENING_UCITS_FILE.getCode());
        if (filesDto != null) {
            FileUploadResultDto result = this.screeningService.saveAttachmentUcitsFile(screeningId, filesDto, username);
            if (result != null && result.getStatus() == ResponseStatusType.SUCCESS) {
                return new ResponseEntity<>(result, null, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/file/fundParams/upload/{screeningId}")
    public ResponseEntity<?> handleFundParamsFileUpload(@PathVariable("screeningId") Long screeningId,
                                              @RequestParam(value = "file", required = false)MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        HedgeFundScreeningDto dto = screeningService.getScreening(screeningId);
        // TODO: Check roles and permissions!

        if(files == null || files.length != 1){
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilesDto filesDto = buildFilesDtoFromMultipart(files[0], FileTypeLookup.HF_SCREENING_PARAMS_FILE.getCode());
        if (filesDto != null) {
            FileUploadResultDto result = this.screeningService.saveFundParamsFile(screeningId, filesDto, username);
            if (result != null && result.getStatus() == ResponseStatusType.SUCCESS) {
                return new ResponseEntity<>(result, null, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(result, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity search(@RequestBody HedgeFundScreeningSearchParams searchParams){
        HedgeFundScreeningPagedSearchResult searchResult = this.screeningService.searchScreenings(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @ResponseBody
    @RequestMapping(value="/file/delete/{screeningId}/{fileId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteFile(@PathVariable(value="screeningId") Long screeningId, @PathVariable(value="fileId") Long fileId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // TODO: CHECK USER ROLES

        boolean deleted = this.screeningService.removeFileAndData(fileId, screeningId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @ResponseBody
    @RequestMapping(value="/file/ucits/delete/{screeningId}/{fileId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteUcitsFile(@PathVariable(value="screeningId") Long screeningId, @PathVariable(value="fileId") Long fileId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // TODO: CHECK USER ROLES

        boolean deleted = this.screeningService.removeUcitsFileAndData(fileId, screeningId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @ResponseBody
    @RequestMapping(value="/file/fundParams/delete/{screeningId}/{fileId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteFundParamsFile(@PathVariable(value="screeningId") Long screeningId, @PathVariable(value="fileId") Long fileId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // TODO: CHECK USER ROLES

        boolean deleted = this.screeningService.removeFundParamsFileAndData(fileId, screeningId, username);
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/return/search", method = RequestMethod.POST)
    public ResponseEntity searchReturns(@RequestBody HedgeFundScreeningParsedDataDateValueSearchParamsDto parans){
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> returns = this.screeningService.searchParsedReturns(parans);
        return buildNonNullResponse(returns);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/aum/search", method = RequestMethod.POST)
    public ResponseEntity searchAUMs(@RequestBody HedgeFundScreeningParsedDataDateValueSearchParamsDto parans){
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> returns = this.screeningService.searchParsedAUMS(parans);
        return buildNonNullResponse(returns);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/aum/ucits/search", method = RequestMethod.POST)
    public ResponseEntity searchUcitsAUMs(@RequestBody HedgeFundScreeningParsedDataDateValueSearchParamsDto parans){
        List<HedgeFundScreeningParsedDataDateValueCombinedDto> returns = this.screeningService.searchParsedUcitsAUMS(parans);
        return buildNonNullResponse(returns);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getFilteredResult(@PathVariable long id){
        HedgeFundScreeningFilteredResultDto dto = this.screeningService.getFilteredResultWithFundsInfo(id);
        return buildNonNullResponse(dto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/statistics/get", method = RequestMethod.POST)
    public ResponseEntity getFilteredResultStatistics(@RequestBody HedgeFundScreeningFilteredResultDto params){
        HedgeFundScreeningFilteredResultStatisticsDto dto = this.screeningService.getFilteredResultStatistics(params);
        return buildNonNullResponse(dto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveFilteredResult(@RequestBody HedgeFundScreeningFilteredResultDto filteredResultDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = this.screeningService.saveFilters(filteredResultDto, username);

        return buildEntitySaveResponse(id);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/findAll/{screeningId}", method = RequestMethod.GET)
    public ResponseEntity findAllFilteredResult(@PathVariable long screeningId){
        List<HedgeFundScreeningFilteredResultDto>  resultList = this.screeningService.getFilteredResultsByScreeningId(screeningId);
        return buildNonNullResponse(resultList);
    }


    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/qualifiedFundList/get", method = RequestMethod.POST)
    public ResponseEntity getFilteredResultQualifiedFundList(@RequestBody HedgeFundScreeningFilteredResultDto params){
        long start = new Date().getTime();

        ListResponseDto responseDto = this.screeningService.getFilteredResultQualifiedFundList(params, true);

        long end = new Date().getTime();
        System.out.println("Qualified fund list total time = " + (end-start) / 1000.);

        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/qualifiedFundList/getAlternative", method = RequestMethod.POST)
    public ResponseEntity getFilteredResultQualifiedFundListAlternative(@RequestBody HedgeFundScreeningFilteredResultDto params){
        long start = new Date().getTime();

        ListResponseDto responseDto = this.screeningService.getFilteredResultQualifiedFundListAlternative(params, true);

        long end = new Date().getTime();
        System.out.println("Qualified fund list total time = " + (end-start) / 1000.);

        return buildNonNullResponse(responseDto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/unqualifiedFundList/get", method = RequestMethod.POST)
    public ResponseEntity getFilteredResultUnqualifiedFundList(@RequestBody HedgeFundScreeningFilteredResultDto params){

        long start = new Date().getTime();
        List<HedgeFundScreeningParsedDataDto> fundList = this.screeningService.getFilteredResultUnqualifiedFundList(params);

        long end = new Date().getTime();
        System.out.println("Un-qualified fund list total time = " + (end-start) / 1000.);
        return buildNonNullResponse(fundList);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/undecidedFundList/get", method = RequestMethod.POST)
    public ResponseEntity getFilteredResultUndecidedFundList(@RequestBody HedgeFundScreeningFilteredResultDto params){

        long start = new Date().getTime();

        List<HedgeFundScreeningParsedDataDto> fundList = this.screeningService.getFilteredResultUndecidedFundList(params);

        long end = new Date().getTime();
        System.out.println("Undecided fund list total time = " + (end-start) / 1000.);
        return buildNonNullResponse(fundList);
    }

    @Deprecated
    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/updateManagerAUM/", method = RequestMethod.POST)
    public ResponseEntity updateManagerAUM(@RequestBody List<HedgeFundScreeningParsedDataDto> fundList){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.screeningService.updateManagerAUM(fundList, username);
        return buildNonNullResponse(saved);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/updateFund/", method = RequestMethod.POST)
    public ResponseEntity updateFund(@RequestBody HedgeFundScreeningParsedDataDto fund){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.screeningService.updateFundInfo(fund, username);
        return buildEntitySaveResponseEntity(saved);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/deleteAddedFund/", method = RequestMethod.POST)
    public ResponseEntity deleteAddedFund(@RequestBody HedgeFundScreeningParsedDataDto fund){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.screeningService.deleteAddedFund(fund.getFundName(), fund.getFilteredResultId(), username);
        return buildEntitySaveResponseEntity(saved);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/excludeFund/", method = RequestMethod.POST)
    public ResponseEntity excludeParsedFund(@RequestBody HedgeFundScreeningParsedDataDto fund){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.screeningService.excludeParsedFund(fund.getFilteredResultId(), fund.getFundId(), fund.getExcludeComment(), fund.isExcludeFromStrategyAUM(), username);
        return buildEntitySaveResponseEntity(saved);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/includeFund/", method = RequestMethod.POST)
    public ResponseEntity includeParsedFund(@RequestBody HedgeFundScreeningParsedDataDto fund){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.screeningService.includeParsedFund(fund.getFilteredResultId(), fund.getFundId(), username);
        return buildEntitySaveResponseEntity(saved);
    }

    @RequestMapping(value="/scoring/export/{type}/{filteredResultId}/{lookbackAUM}//{lookbackReturn}/{isAlternative}", method= RequestMethod.GET)
    @ResponseBody
    public void exportFundList(@PathVariable(value = "type") int type,
                               @PathVariable(value="filteredResultId") Long filteredResultId,
                             @PathVariable(value = "lookbackAUM") int lookbackAUM,
                             @PathVariable(value = "lookbackReturn") int lookbackReturn,
                             @PathVariable(value = "isAlternative") boolean isAlternative,
                             HttpServletResponse response) {

        // TODO: control file download by user role
        // TODO: Check rights

        FilesDto filesDto = null;
        try{
            if(type == 1) {
                filesDto = this.screeningService.getQualifiedFundListAsStream(filteredResultId, lookbackAUM, lookbackReturn, isAlternative);
            }else if(type == 2){
                filesDto = this.screeningService.getUnqualifiedFundListAsStream(filteredResultId, lookbackAUM, lookbackReturn);
            }
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
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment; filename=\"" + filesDto.getOutputFileName() + "\"");
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

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/saveResults", method = RequestMethod.POST)
    public ResponseEntity<?> saveResults(@RequestBody HedgeFundScreeningSaveParamsDto saveParamsDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ResponseDto response = this.screeningService.saveResults(saveParamsDto, username);

        return buildNonNullResponseWithStatus(response);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/deleteResults/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSavedResults(@PathVariable(value = "id") Long id) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        //ResponseDto response = this.screeningService.deleteSavedResultsById(id, username);
        ResponseDto response = this.screeningService.archiveSavedResultsById(id, username);

        return buildNonNullResponseWithStatus(response);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/deleteFilteredResult/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFilteredResult(@PathVariable(value = "id") Long id) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ResponseDto response = this.screeningService.deleteFilteredResultById(id, username);

        return buildNonNullResponseWithStatus(response);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteScreening(@PathVariable(value = "id") Long id) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ResponseDto response = this.screeningService.deleteScreeningById(id, username);

        return buildNonNullResponseWithStatus(response);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/markNonArchived/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> markAsSavedResultNonArchived(@PathVariable(value = "id") Long id) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        ResponseDto response = this.screeningService.markAsSavedResultNonArchived(id, username);

        return buildNonNullResponseWithStatus(response);
    }

}
