package kz.nicnbk.ws.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.monitoring.LiquidPortfolioService;
import kz.nicnbk.service.api.monitoring.MonitoringRiskService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Created by Pak on 13.06.2019.
 */

@RestController
@RequestMapping("/monitoring/risk")
public class MonitoringRiskServiceREST extends CommonServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringRiskServiceREST.class);

    @Autowired
    private MonitoringRiskService riskService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_RISKS_VIEWER') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/hfMonthly", method = RequestMethod.POST)
    public ResponseEntity getMonthlyHedgeFundReport(@RequestBody MonitoringRiskReportSearchParamsDto searchParamsDto) {
        MonitoringRiskHedgeFundReportDto riskReport = this.riskService.getMonthlyHedgeFundRiskReport(searchParamsDto);
        return buildNonNullResponse(riskReport);
    }

    //@PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_RISKS_VIEWER') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/dateList", method = RequestMethod.GET)
    public ResponseEntity getAvailableDateList() {
        List<Date> dateList = this.riskService.getReportDateList();
        return buildNonNullResponse(dateList);
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/riskHFReport/save", method = RequestMethod.POST)
    public ResponseEntity createRiskReport(@RequestBody MonitoringRiskHFMonthlyReportDto reportDto) {
        EntitySaveResponseDto saveResponseDto = this.riskService.saveReport(reportDto);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/strategy/upload", method = RequestMethod.POST)
    public ResponseEntity uploadStrategy(@RequestParam(value = "file", required = false) MultipartFile[] files) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        MonitoringRiskHedgeFundAllocationSubStrategyResultDto resultDto = this.riskService.uploadStrategy(filesDtoSet, username);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/returns/upload", method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity uploadReturns( @RequestPart("data") String data,
                                         @RequestPart(name="file", required=true) MultipartFile[] files) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        // Deserialize string to object
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(df);
        try {
            MonitoringRiskHFReturnsHolderDto holderDto = objectMapper.readValue(data, MonitoringRiskHFReturnsHolderDto.class);
            List<FilesDto> filesDto = files != null ? buildFilesListDtoFromMultipart(files, holderDto.getFileType()) : null;
            if(holderDto.isEmpty()){
                ResponseDto responseDto = new ResponseDto();
                responseDto.setErrorMessageEn("Failed to upload returns file: file meta data is missing");
                return new ResponseEntity<>(responseDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            ResponseDto responseDto = this.riskService.uploadHFReturns(holderDto.getReport().getId(), filesDto.get(0), username);
            if (responseDto.getStatus().getCode().equals(ResponseStatusType.SUCCESS.getCode())) {
                return new ResponseEntity<>(responseDto, null, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(responseDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException ex) {
            logger.error("Monitoring Risk HF - Returns file upload failed: could not deserialize object (with exception)", ex);
        }
        return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/returns/classA/delete/{reportId}", method = RequestMethod.GET)
    public ResponseEntity deleteReturnsClassAFile(@PathVariable Long reportId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.riskService.deleteReturnsClassAFile(reportId, username);
        if(deleted){
            logger.info("Successfully deleted Returns Class A data: report id=" + reportId.longValue() + " [user " + username + "]");
        }else{
            logger.error("Failed to delete Returns Class A data: report id= " + reportId.longValue() + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/returns/classB/delete/{reportId}", method = RequestMethod.GET)
    public ResponseEntity deleteReturnsClassBFile(@PathVariable Long reportId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.riskService.deleteReturnsClassBFile(reportId, username);
        if(deleted){
            logger.info("Successfully deleted Returns Class B data: report id=" + reportId.longValue() + " [user " + username + "]");
        }else{
            logger.error("Failed to delete Returns Class B data: report id= " + reportId.longValue() + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/returns/cons/delete/{reportId}", method = RequestMethod.GET)
    public ResponseEntity deleteReturnsConsFile(@PathVariable Long reportId) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean deleted = this.riskService.deleteReturnsConsFile(reportId, username);
        if(deleted){
            logger.info("Successfully deleted Returns Cons data: report id=" + reportId.longValue() + " [user " + username + "]");
        }else{
            logger.error("Failed to delete Returns Cons data: report id= " + reportId.longValue() + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_RISKS_VIEWER') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/strategy/export", method = RequestMethod.POST)
    public void exportStrategy(@RequestBody Date selectedDate,
                                   HttpServletResponse response) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment;");
            ByteArrayInputStream stream = this.riskService.exportStrategy(selectedDate);
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("IO Exception");
        }
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/strategy/delete/{selectedDate}", method = RequestMethod.GET)
    public ResponseEntity deleteStrategy(@PathVariable @DateTimeFormat(pattern="dd-MM-yyyy") Date selectedDate) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        boolean deleted = this.riskService.deleteStrategy(selectedDate, username);
        if(deleted){
            logger.info("Successfully deleted Top Portfolio record: record date " + selectedDate + " [user " + username + "]");
        }else{
            logger.error("Failed to delete Top Portfolio record: record date " + selectedDate + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/topPortfolio/upload", method = RequestMethod.POST)
    public ResponseEntity uploadTopPortfolio(@RequestParam(value = "file", required = false) MultipartFile[] files) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        MonitoringRiskHedgeFundAllocationResultDto resultDto = this.riskService.uploadTopPortfolio(filesDtoSet, username);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_RISKS_VIEWER') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/topPortfolio/export", method = RequestMethod.POST)
    public void exportTopPortfolio(@RequestBody Date selectedDate,
                                   HttpServletResponse response) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment;");
            ByteArrayInputStream stream = this.riskService.exportTopPortfolio(selectedDate);
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("IO Exception");
        }
    }

    @PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/topPortfolio/delete/{selectedDate}", method = RequestMethod.GET)
    public ResponseEntity deleteTopPortfolio(@PathVariable @DateTimeFormat(pattern="dd-MM-yyyy") Date selectedDate) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        boolean deleted = this.riskService.deletePortfolios(selectedDate, username);
        if(deleted){
            logger.info("Successfully deleted Top Portfolio record: record date " + selectedDate + " [user " + username + "]");
        }else{
            logger.error("Failed to delete Top Portfolio record: record date " + selectedDate + " [user " + username + "]");
        }
        return buildDeleteResponseEntity(deleted);
    }

}
