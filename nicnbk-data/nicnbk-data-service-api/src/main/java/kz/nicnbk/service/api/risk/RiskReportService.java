package kz.nicnbk.service.api.risk;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.riskmanagement.LiquidPortfolioReportDto;

/**
 * Created by magzumov on 01.11.2016.
 */
public interface RiskReportService extends BaseService{

    public LiquidPortfolioReportDto getLiquidPortfolioReport();
}
