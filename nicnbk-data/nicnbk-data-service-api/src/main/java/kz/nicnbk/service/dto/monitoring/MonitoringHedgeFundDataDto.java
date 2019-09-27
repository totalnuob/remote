package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundDataDto implements BaseDto{

    private MonitoringHedgeFundDataOverallDto overall;
    private MonitoringHedgeFundDataClassADto classA;
    private MonitoringHedgeFundDataClassBDto classB;
    private List<MonitoringHedgeFundApprovedFundInfoDto> approvedFunds;

    //List<MonitoringHedgeFundDateDoubleValueDto> returnsHFRI;

    public MonitoringHedgeFundDataOverallDto getOverall() {
        return overall;
    }

    public void setOverall(MonitoringHedgeFundDataOverallDto overall) {
        this.overall = overall;
    }

    public MonitoringHedgeFundDataClassADto getClassA() {
        return classA;
    }

    public void setClassA(MonitoringHedgeFundDataClassADto classA) {
        this.classA = classA;
    }

    public MonitoringHedgeFundDataClassBDto getClassB() {
        return classB;
    }

    public void setClassB(MonitoringHedgeFundDataClassBDto classB) {
        this.classB = classB;
    }

//    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsHFRI() {
//        return returnsHFRI;
//    }
//
//    public void setReturnsHFRI(List<MonitoringHedgeFundDateDoubleValueDto> returnsHFRI) {
//        this.returnsHFRI = returnsHFRI;
//    }

    public List<MonitoringHedgeFundApprovedFundInfoDto> getApprovedFunds() {
        return approvedFunds;
    }

    public void setApprovedFunds(List<MonitoringHedgeFundApprovedFundInfoDto> approvedFunds) {
        this.approvedFunds = approvedFunds;
    }
}
