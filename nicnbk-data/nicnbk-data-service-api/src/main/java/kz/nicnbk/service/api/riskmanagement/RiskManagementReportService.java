package kz.nicnbk.service.api.riskmanagement;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.riskmanagement.LiquidPortfolioReportDto;

/**
 * Created by magzumov on 01.11.2016.
 */
public interface RiskManagementReportService extends BaseService{

    public LiquidPortfolioReportDto getLiquidPortfolioReport();
}
