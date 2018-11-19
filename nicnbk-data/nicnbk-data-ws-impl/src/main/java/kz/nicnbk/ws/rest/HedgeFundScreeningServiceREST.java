package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hf.HedgeFundScreeningService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            if(result != null && result.getStatus() == ResponseStatusType.SUCCESS){
                return new ResponseEntity<>(result, null, HttpStatus.OK);
            }else{
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
    public ResponseEntity<?> safeDeleteFile(@PathVariable(value="screeningId") Long screeningId, @PathVariable(value="fileId") Long fileId){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // TODO: CHECK USER ROLES

        boolean deleted = this.screeningService.removeFileAndData(fileId, screeningId, username);
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
    @RequestMapping(value = "/filteredResults/fundList/get", method = RequestMethod.POST)
    public ResponseEntity getFilteredResultQualifiedFundList(@RequestBody HedgeFundScreeningFilteredResultDto params){
        List<HedgeFundScreeningParsedDataDto> fundList = this.screeningService.getFilteredResultQualifiedFundList(params);
        return buildNonNullResponse(fundList);
    }
}
