package kz.nicnbk.service.converter.risk;

import kz.nicnbk.repo.model.risk.AllocationBySubStrategy;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.monitoring.MonitoringRiskHedgeFundAllocationSubStrategyDto;
import org.springframework.stereotype.Component;

@Component
public class AllocationBySubStrategyEntityConverter extends BaseDozerEntityConverter<AllocationBySubStrategy, MonitoringRiskHedgeFundAllocationSubStrategyDto> {
}
