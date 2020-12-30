package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;

import java.util.List;

public class MonitoringRiskHedgeFundAllocationSubStrategyResultDto extends ResponseDto {
    private List<MonitoringRiskHedgeFundAllocationSubStrategyDto> monitoringRiskHedgeFundAllocationSubStrategyDtoList;

    public MonitoringRiskHedgeFundAllocationSubStrategyResultDto(List<MonitoringRiskHedgeFundAllocationSubStrategyDto> monitoringRiskHedgeFundAllocationSubStrategyDtoList,
                                                                 ResponseStatusType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.monitoringRiskHedgeFundAllocationSubStrategyDtoList = monitoringRiskHedgeFundAllocationSubStrategyDtoList;
    }

    public List<MonitoringRiskHedgeFundAllocationSubStrategyDto> getMonitoringRiskHedgeFundAllocationSubStrategyDtoList() {
        return monitoringRiskHedgeFundAllocationSubStrategyDtoList;
    }

    public void setMonitoringRiskHedgeFundAllocationSubStrategyDtoList(List<MonitoringRiskHedgeFundAllocationSubStrategyDto> monitoringRiskHedgeFundAllocationSubStrategyDtoList) {
        this.monitoringRiskHedgeFundAllocationSubStrategyDtoList = monitoringRiskHedgeFundAllocationSubStrategyDtoList;
    }
}
