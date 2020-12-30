package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.monitoring.LiquidPortfolioService;
import kz.nicnbk.service.api.monitoring.MonitoringRiskService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        MonitoringRiskHedgeFundReportDto riskReport = this.riskService.getMonthlyHedgeFundReport(searchParamsDto);
        return buildNonNullResponse(riskReport);
    }

    //@PreAuthorize("hasRole('ROLE_RISKS_EDITOR') OR hasRole('ROLE_RISKS_VIEWER') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/dateList", method = RequestMethod.GET)
    public ResponseEntity getAvailableDateList() {
        List<Date> dateList = this.riskService.getDateList();
        return buildNonNullResponse(dateList);
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

}
