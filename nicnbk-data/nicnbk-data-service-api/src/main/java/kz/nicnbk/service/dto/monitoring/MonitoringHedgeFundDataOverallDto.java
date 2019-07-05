package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundDataOverallDto implements BaseDto{

    List<MonitoringHedgeFundNameTextValueDto> generalInformation;
    List<MonitoringHedgeFundNameDoubleValueDto> contributionToReturn;
    List<MonitoringHedgeFundNameDoubleValueDto> allocationByStrategy;

    List<MonitoringHedgeFundDateDoubleValueDto> returns;

    public List<MonitoringHedgeFundNameTextValueDto> getGeneralInformation() {
        return generalInformation;
    }

    public void setGeneralInformation(List<MonitoringHedgeFundNameTextValueDto> generalInformation) {
        this.generalInformation = generalInformation;
    }

    public List<MonitoringHedgeFundNameDoubleValueDto> getContributionToReturn() {
        return contributionToReturn;
    }

    public void setContributionToReturn(List<MonitoringHedgeFundNameDoubleValueDto> contributionToReturn) {
        this.contributionToReturn = contributionToReturn;
    }

    public List<MonitoringHedgeFundNameDoubleValueDto> getAllocationByStrategy() {
        return allocationByStrategy;
    }

    public void setAllocationByStrategy(List<MonitoringHedgeFundNameDoubleValueDto> allocationByStrategy) {
        this.allocationByStrategy = allocationByStrategy;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturns() {
        return returns;
    }

    public void setReturns(List<MonitoringHedgeFundDateDoubleValueDto> returns) {
        this.returns = returns;
    }

}
