package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;

import java.util.List;

public class MonitoringRiskHedgeFundAllocationResultDto extends ResponseDto {
    private List<MonitoringRiskHedgeFundFundAllocationDto> monitoringRiskHedgeFundFundAllocationDtos;

    public MonitoringRiskHedgeFundAllocationResultDto(List<MonitoringRiskHedgeFundFundAllocationDto> monitoringRiskHedgeFundFundAllocationDtos,
                                                      ResponseStatusType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.monitoringRiskHedgeFundFundAllocationDtos = monitoringRiskHedgeFundFundAllocationDtos;
    }

    public List<MonitoringRiskHedgeFundFundAllocationDto> getMonitoringRiskHedgeFundFundAllocationDtos() {
        return monitoringRiskHedgeFundFundAllocationDtos;
    }

    public void setMonitoringRiskHedgeFundFundAllocationDtos(List<MonitoringRiskHedgeFundFundAllocationDto> monitoringRiskHedgeFundFundAllocationDtos) {
        this.monitoringRiskHedgeFundFundAllocationDtos = monitoringRiskHedgeFundFundAllocationDtos;
    }
}
