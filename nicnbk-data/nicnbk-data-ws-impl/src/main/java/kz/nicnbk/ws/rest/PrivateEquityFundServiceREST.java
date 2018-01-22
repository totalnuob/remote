package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.pe.PECompanyPerformanceIddService;
import kz.nicnbk.service.api.pe.PECompanyPerformanceService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEGrossCashflowService;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.pe.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 16-Nov-16.
 */
@RestController
@RequestMapping("/pe/fund")
public class PrivateEquityFundServiceREST extends  CommonServiceREST{

    @Autowired
    private PEFundService service;

    @Autowired
    private PEGrossCashflowService cashflowService;

    @Autowired
    private PECompanyPerformanceIddService performanceIddService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        PEFundDto fundDto = this.service.get(id);
        if(fundDto != null){
            return new ResponseEntity<>(fundDto, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody PEFundDto fundDto){
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = this.service.save(fundDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, fundDto.getCreationDate());
        }
    }

//    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/savePerformance/{fundId}", method = RequestMethod.POST)
//    public ResponseEntity<?> savePerformance(@RequestBody List<PECompanyPerformanceDto> performanceDtoList, @PathVariable Long fundId) {
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//
//        PECompanyPerformanceResultDto resultDto = this.service.savePerformance(performanceDtoList, fundId, username);
//
//        if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
//            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/recalculate/{fundId}/{calculationType}", method = RequestMethod.GET)
    public ResponseEntity<?> recalculateStatistics(@PathVariable Long fundId, @PathVariable int calculationType) {
        PEFundTrackRecordResultDto resultDto = this.service.calculateTrackRecord(fundId, calculationType);

        if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/savePerformance/{fundId}/recalculate", method = RequestMethod.POST)
    public ResponseEntity<?> savePerformanceAndUpdateStatistics(@RequestBody List<PECompanyPerformanceDto> performanceDtoList, @PathVariable Long fundId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        PECompanyPerformanceAndFundTrackRecordResultDto resultDto = this.service.savePerformanceAndUpdateStatistics(performanceDtoList, fundId, username);

        if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/savePortfolioInfo/{fundId}", method = RequestMethod.POST)
    public ResponseEntity<?> savePortfolioInfo(@RequestBody List<PECompanyPerformanceIddDto> performanceIddDtoList, @PathVariable Long fundId) {

        PECompanyPerformanceIddResultDto resultDto = this.performanceIddService.saveList(performanceIddDtoList, fundId);

        if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/calculateIRR/{fundId}", method = RequestMethod.POST)
    public ResponseEntity<?> calculateIRR(@RequestBody PEPortfolioInfoDto portfolioInfoDto, @PathVariable Long fundId) {

        PEIrrResultDto resultDto = this.cashflowService.calculateIRR(portfolioInfoDto, fundId);

        if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/saveGrossCF/{fundId}", method = RequestMethod.POST)
//    public ResponseEntity<?> saveGrossCF(@RequestBody List<PEGrossCashflowDto> grossCashflowDtoList, @PathVariable Long fundId) {
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String username = this.tokenService.decode(token).getUsername();
//
//        PEGrossCashflowResultDto resultDto = this.service.saveGrossCF(grossCashflowDtoList, fundId, username);
//
//        if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
//            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/saveGrossCF/{fundId}/recalculate", method = RequestMethod.POST)
    public ResponseEntity<?> saveGrossCFAndRecalculatePerformanceIdd(@RequestBody List<PEGrossCashflowDto> grossCashflowDtoList, @PathVariable Long fundId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto resultDto = this.service.saveGrossCFAndRecalculatePerformanceIddAndUpdateStatistics(grossCashflowDtoList, fundId, username);

        if (resultDto.getStatus().equals(StatusResultType.SUCCESS)) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity search(@RequestBody PESearchParams searchParams){
        List<PEFundDto> funds = this.service.loadFirmFunds(searchParams.getId(), searchParams.getReport());
        return buildResponse(funds);
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/uploadGrossCF", method = RequestMethod.POST)
    public ResponseEntity<?> uploadGrossCF(@RequestParam(value = "file", required = false) MultipartFile[] files) {

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        PEGrossCashflowResultDto resultDto = this.cashflowService.uploadGrossCF(filesDtoSet);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, FileTypeLookup.MEMO_ATTACHMENT.getCode());
//        if(filesDtoSet != null){
//            Set<FilesDto> savedAttachments = this.tripMemoService.saveAttachments(tripMemoId, filesDtoSet);
//            if(savedAttachments == null){
//                // error occurred
//                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
//            }else{
//                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }
}