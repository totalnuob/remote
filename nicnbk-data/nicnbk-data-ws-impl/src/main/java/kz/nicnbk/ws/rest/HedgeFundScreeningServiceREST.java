package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningFundCounts;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedData;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedDataAUM;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/screening")
public class HedgeFundScreeningServiceREST extends CommonServiceREST{

    @Autowired
    private HedgeFundScreeningService screeningService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        HedgeFundScreeningDto dto = this.screeningService.get(id);
        return buildNonNullResponse(dto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundScreeningDto screeningDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = this.screeningService.save(screeningDto, username);

        return buildEntitySaveResponse(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/file/upload/{screeningId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("screeningId") Long screeningId,
                                              @RequestParam(value = "file", required = false)MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        HedgeFundScreeningDto dto = screeningService.get(screeningId);
        // TODO: Check roles and permissions!

        if(files == null || files.length != 1){
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilesDto filesDto = buildFilesDtoFromMultipart(files[0], FileTypeLookup.HF_SCREENING_DATA_FILE.getCode());
        if (filesDto != null) {
            FileUploadResultDto result = this.screeningService.saveAttachmentDataFile(screeningId, filesDto, username);
            if (result != null && result.getStatus() == ResponseStatusType.SUCCESS) {
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

        HedgeFundScreeningDto dto = screeningService.get(screeningId);
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


    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity search(@RequestBody HedgeFundScreeningSearchParams searchParams){
        HedgeFundScreeningPagedSearchResult searchResult = this.screeningService.search(searchParams);
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
        HedgeFundScreeningFilteredResultDto dto = this.screeningService.getFilteredResult(id);

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
    public ResponseEntity<?>  saveFilteredResult(@RequestBody HedgeFundScreeningFilteredResultDto filteredResultDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = this.screeningService.saveFilteredResult(filteredResultDto, username);

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

        List<HedgeFundScreeningParsedDataDto> fundList = this.screeningService.getFilteredResultQualifiedFundList(params);

        long end = new Date().getTime();
        System.out.println("Qualified fund list total time = " + (end-start) / 1000.);

        return buildNonNullResponse(fundList);
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

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/filteredResults/updateManagerAUM/", method = RequestMethod.POST)
    public ResponseEntity updateManagerAUM(@RequestBody List<HedgeFundScreeningParsedDataDto> fundList){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved = this.screeningService.updateManagerAUM(fundList, username);
        return buildNonNullResponse(saved);
    }
}
