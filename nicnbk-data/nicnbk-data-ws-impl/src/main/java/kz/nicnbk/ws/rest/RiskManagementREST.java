package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.riskmanagement.RiskManagementReportService;
import kz.nicnbk.service.dto.riskmanagement.LiquidPortfolioReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by magzumov on 01.11.2016.
 */

@RestController
@RequestMapping("/riskManagement")
public class RiskManagementREST {

    @Autowired
    private RiskManagementReportService reportService;

    @RequestMapping(value = "/liquidPortfolioReport", method = RequestMethod.GET)
    public LiquidPortfolioReportDto getLiquidPortfolioReport(){
        return reportService.getLiquidPortfolioReport();
    }
}
