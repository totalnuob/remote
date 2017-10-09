package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.pe.PEFundCompaniesPerformanceDto;
import kz.nicnbk.service.dto.pe.PEFundDto;
import kz.nicnbk.service.dto.pe.PEFundTrackRecordResultDto;
import kz.nicnbk.service.dto.pe.PESearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        if(fundDto.getId() == null){
            fundDto.setOwner(username);
        }
        Long id = this.service.save(fundDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, fundDto.getCreationDate());
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save/{fundId}", method = RequestMethod.POST)
    public ResponseEntity<?> savePerformance(@RequestBody List<PEFundCompaniesPerformanceDto> performanceDtoList, @PathVariable Long fundId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        StatusResultDto statusResultDto = this.service.savePerformance(performanceDtoList, fundId, username);

        if (statusResultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(statusResultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(statusResultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save/{fundId}/recalculate", method = RequestMethod.POST)
    public ResponseEntity<?> savePerformanceAndRecalculateStatistics(@RequestBody List<PEFundCompaniesPerformanceDto> performanceDtoList, @PathVariable Long fundId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        PEFundTrackRecordResultDto resultDto = this.service.savePerformanceAndRecalculateStatistics(performanceDtoList, fundId, username);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
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
    @RequestMapping(value = "/uploadGrossCF/{fundId}", method = RequestMethod.POST)
    public ResponseEntity<?> uploadGrossCF(@RequestParam(value = "file", required = false) MultipartFile[] files, @PathVariable Long fundId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        return new ResponseEntity<>(new StatusResultDto(StatusResultType.SUCCESS, "", "Done!", ""), null, HttpStatus.OK);

//        StatusResultDto statusResultDto = this.service.uploadGrossCF(dsasadas, fundId, username);
//
//        if (statusResultDto.getStatus().getCode().equals("SUCCESS")) {
//            return new ResponseEntity<>(statusResultDto, null, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(statusResultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }



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