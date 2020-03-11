package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.common.ResponseDto;

import java.util.Map;

public class MonitoringRiskHedgeFundFundNAVHolderDto extends ResponseDto {

    private Map<String, MonitoringRiskHedgeFundFundAllocationDto> classA;
    private Map<String, MonitoringRiskHedgeFundFundAllocationDto> classB;

    public Map<String, MonitoringRiskHedgeFundFundAllocationDto> getClassA() {
        return classA;
    }

    public void setClassA(Map<String, MonitoringRiskHedgeFundFundAllocationDto> classA) {
        this.classA = classA;
    }

    public Map<String, MonitoringRiskHedgeFundFundAllocationDto> getClassB() {
        return classB;
    }

    public void setClassB(Map<String, MonitoringRiskHedgeFundFundAllocationDto> classB) {
        this.classB = classB;
    }
}
