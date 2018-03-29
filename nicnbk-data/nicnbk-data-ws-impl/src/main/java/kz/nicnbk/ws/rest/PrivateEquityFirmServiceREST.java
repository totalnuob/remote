package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.pe.PEFirmService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEFundDto;
import kz.nicnbk.service.dto.pe.PEPagedSearchResult;
import kz.nicnbk.service.dto.pe.PESearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 16-Nov-16.
 */

@RestController
@RequestMapping("/pe/firm")
public class PrivateEquityFirmServiceREST extends CommonServiceREST{
    @Autowired
    private PEFirmService peFirmService;

    @Autowired
    private PEFundService peFundService;

    @Autowired
    private PEIrrService irrService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        PEFirmDto firmDto = this.peFirmService.get(id);
        if(firmDto != null){
            return new ResponseEntity<>(firmDto, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/getFunds/{id}", method = RequestMethod.GET)
    public ResponseEntity getFunds(@PathVariable long id){
        List<PEFundDto> fundDtoList = this.peFundService.loadFirmFunds(id, false);
        if(fundDtoList != null){
            return new ResponseEntity<>(fundDtoList, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/getTotalIrrForOnePager/{id}", method = RequestMethod.GET)
    public ResponseEntity getTotalIrrForOnePager(@PathVariable long id){

        List<PEFundDto> fundDtoList = this.peFundService.loadFirmFunds(id, false);
        List<PEFundDto> fundDtoListShort = new ArrayList<>();
        boolean areAllKeyFundStatisticsCalculatedByGrossCF = true;

        if (fundDtoList != null) {
            for (PEFundDto fundDto : fundDtoList) {
                if (fundDto.getDoNotDisplayInOnePager() != null && fundDto.getDoNotDisplayInOnePager()) {continue;}

                if (fundDto.getCalculationType() == null || fundDto.getCalculationType() !=2) {
                    areAllKeyFundStatisticsCalculatedByGrossCF = false;
                }

                fundDtoListShort.add(fundDto);
            }

            if (areAllKeyFundStatisticsCalculatedByGrossCF) {
                double totalIrr  = irrService.getIrrByFundList(fundDtoListShort);
                return new ResponseEntity<>(totalIrr, null, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody PEFirmDto firmDto){

        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(firmDto.getId() == null){
            firmDto.setOwner(username);
        }
        Long id = this.peFirmService.save(firmDto, username);

        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, firmDto.getCreationDate());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_PRIVATE_EQUITY_VIEWER') OR hasAuthority('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public  ResponseEntity search(@RequestBody PESearchParams searchParams, HttpServletRequest request){
        PEPagedSearchResult searchResult =  this.peFirmService.findByName(searchParams);
        if(searchResult != null){
            return new ResponseEntity<>(searchResult, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity getFirms(){
        List<PEFirmDto> firms = this.peFirmService.findAll();
        if(firms != null){
            return new ResponseEntity<>(firms, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/logo/upload/{firmId}", method = RequestMethod.POST)
    public ResponseEntity<?> uploadLogo(@RequestParam(value = "file", required = false) MultipartFile[] files, @PathVariable Long firmId) {

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        if (filesDtoSet != null) {
            FilesDto savedLogo = this.peFirmService.saveLogo(firmId, filesDtoSet);
            if (savedLogo == null) {
                // error occurred
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(savedLogo, null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }
}