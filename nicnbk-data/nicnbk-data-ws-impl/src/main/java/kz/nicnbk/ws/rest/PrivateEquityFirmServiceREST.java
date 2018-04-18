package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.pe.PEFirmService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.api.pe.PEPdfService;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.pe.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 16-Nov-16.
 */

@RestController
@RequestMapping("/pe/firm")
public class PrivateEquityFirmServiceREST extends CommonServiceREST{

    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    @Autowired
    private PEFirmService peFirmService;

    @Autowired
    private PEFundService peFundService;

    @Autowired
    private PEIrrService irrService;

    @Autowired
    private PEPdfService pdfService;

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

//    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/getFunds/{id}", method = RequestMethod.GET)
//    public ResponseEntity getFunds(@PathVariable long id){
//        List<PEFundDto> fundDtoList = this.peFundService.loadFirmFunds(id, false);
//        if(fundDtoList != null){
//            return new ResponseEntity<>(fundDtoList, null, HttpStatus.OK);
//        }else{
//            // error occurred
//            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_VIEWER') OR hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/getFundsAndTotalIrrAndBarChartsForOnePager/{id}/{fundId}", method = RequestMethod.GET)
    public ResponseEntity getFundsAndTotalIrrAndBarChartsForOnePager(@PathVariable long id, @PathVariable long fundId){

        List<PEFundDto> fundDtoList = this.peFundService.loadFirmFunds(id, true);
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

            //create bar charts
            byte[] barChartNetIrrBytes = null;
            byte[] barChartNetMoicBytes = null;
            try {
                String tmpFolder = rootDirectory + "/tmp_one_pager";
                String barChartNetIrrDest = tmpFolder + "/BarChartNetIrr_" + new Date().getTime() + ".jpeg";
                String barChartNetMoicDest = tmpFolder + "/BarChartNetMoic_" + new Date().getTime() + ".jpeg";

                String benchmark = "???";
                if (fundId > 0) {
                    try {
                        benchmark = this.peFundService.get(fundId).getBenchmarkName();
                    } catch (Exception ex) {
                        System.out.println("Error obtaining benchmark name, fund: " + fundId);
                    }
                }

                this.pdfService.createCharts(this.peFirmService.get(id).getFirmName(), fundDtoListShort, benchmark, barChartNetIrrDest, barChartNetMoicDest, (float) 500);

                File barChartNetIrrFile = new File(barChartNetIrrDest);
                File barChartNetMoicFile = new File(barChartNetMoicDest);

                FileInputStream barChartNetIrrFileInputStream = new FileInputStream(barChartNetIrrFile);
                FileInputStream barChartNetMoicFileInputStream = new FileInputStream(barChartNetMoicFile);

                barChartNetIrrBytes = IOUtils.toByteArray(barChartNetIrrFileInputStream);
                barChartNetMoicBytes = IOUtils.toByteArray(barChartNetMoicFileInputStream);

                barChartNetIrrFileInputStream.close();
                barChartNetMoicFileInputStream.close();

                Files.deleteIfExists(barChartNetIrrFile.toPath());
                Files.deleteIfExists(barChartNetMoicFile.toPath());

//                this.pdfService.createCharts(firmDto.getFirmName(), fundDtoListShort, (fundDto.getBenchmarkName() != null && fundDto.getBenchmarkName() != "") ? fundDto.getBenchmarkName() : "????", barChartNetIrrDest, barChartNetMoicDest, columnOneWidth);
            } catch (Exception ex) {
                System.out.println("Error creating Bar charts, firm: " + id);
            }

            PEFirmFundsAndTotalIrrAndBarChartsResultDto resultDto;

            if (areAllKeyFundStatisticsCalculatedByGrossCF) {
                double totalIrr  = irrService.getIrrByFundList(fundDtoListShort);
                resultDto = new PEFirmFundsAndTotalIrrAndBarChartsResultDto(fundDtoListShort, totalIrr, barChartNetIrrBytes, barChartNetMoicBytes, StatusResultType.SUCCESS, "", "SUCCESS", "");
            } else {
                resultDto = new PEFirmFundsAndTotalIrrAndBarChartsResultDto(fundDtoListShort, null, barChartNetIrrBytes, barChartNetMoicBytes, StatusResultType.SUCCESS, "", "SUCCESS", "");
            }

            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
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

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        if (filesDtoSet != null) {
            FilesDto savedLogo = this.peFirmService.saveLogo(firmId, filesDtoSet, username);
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