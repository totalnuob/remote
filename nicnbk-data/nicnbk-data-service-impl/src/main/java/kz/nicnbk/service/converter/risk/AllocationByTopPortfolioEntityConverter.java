package kz.nicnbk.service.converter.risk;

import kz.nicnbk.repo.model.risk.AllocationByTopPortfolio;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.monitoring.MonitoringRiskHedgeFundFundAllocationDto;
import org.springframework.stereotype.Component;

@Component
public class AllocationByTopPortfolioEntityConverter extends BaseDozerEntityConverter<AllocationByTopPortfolio, MonitoringRiskHedgeFundFundAllocationDto> {
}
