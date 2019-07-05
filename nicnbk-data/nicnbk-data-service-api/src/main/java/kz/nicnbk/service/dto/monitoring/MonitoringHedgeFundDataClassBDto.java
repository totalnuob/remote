package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundDataClassBDto implements BaseDto{

    List<MonitoringHedgeFundNameTextValueDto> generalInformation;
    List<MonitoringHedgeFundNameDoubleValueDto> allocationByStrategy;

    List<MonitoringHedgeFundTopFundInfoDto> fundAllocations;

    List<MonitoringHedgeFundDateDoubleValueDto> returns;

    public List<MonitoringHedgeFundNameTextValueDto> getGeneralInformation() {
        return generalInformation;
    }

    public void setGeneralInformation(List<MonitoringHedgeFundNameTextValueDto> generalInformation) {
        this.generalInformation = generalInformation;
    }

    public List<MonitoringHedgeFundNameDoubleValueDto> getAllocationByStrategy() {
        return allocationByStrategy;
    }

    public void setAllocationByStrategy(List<MonitoringHedgeFundNameDoubleValueDto> allocationByStrategy) {
        this.allocationByStrategy = allocationByStrategy;
    }

    public List<MonitoringHedgeFundTopFundInfoDto> getFundAllocations() {
        return fundAllocations;
    }

    public void setFundAllocations(List<MonitoringHedgeFundTopFundInfoDto> fundAllocations) {
        this.fundAllocations = fundAllocations;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturns() {
        return returns;
    }

    public void setReturns(List<MonitoringHedgeFundDateDoubleValueDto> returns) {
        this.returns = returns;
    }
}
