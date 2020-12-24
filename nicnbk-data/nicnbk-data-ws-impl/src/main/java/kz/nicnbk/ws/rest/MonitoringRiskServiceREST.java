package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.monitoring.LiquidPortfolioService;
import kz.nicnbk.service.api.monitoring.MonitoringRiskService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.monitoring.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * Created by Pak on 13.06.2019.
 */

@RestController
@RequestMapping("/monitoring/risk")
public class MonitoringRiskServiceREST extends CommonServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringRiskServiceREST.class);

    @Autowired
    private MonitoringRiskService riskService;

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

//    @RequestMapping(value = "/portfolioVar/save", method = RequestMethod.POST)
//    public ResponseEntity<?> savePortfolioVar(@RequestBody MonitoringRiskHedgeFundPortfolioVarDto varDto) {
////        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
////        String username = this.tokenService.decode(token).getUsername();
//        EntitySaveResponseDto saveResponse = this.riskService.save(varDto);
//        return buildEntitySaveResponse(saveResponse);
//    }
//
//    @RequestMapping(value = "/stressTest/save", method = RequestMethod.POST)
//    public ResponseEntity<?> saveStressTest(@RequestBody MonitoringRiskHedgeFundStressTestDto stressTestDto) {
////        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
////        String username = this.tokenService.decode(token).getUsername();
//        EntitySaveResponseDto saveResponse = this.riskService.save(stressTestDto);
//        return buildEntitySaveResponse(saveResponse);
//    }
}
