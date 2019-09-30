package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundApprovedFundInfoDto implements BaseDto{

    private String fundName;
    private String managerName;
    private String strategy;

    private String protocol;
    private Date approveDate;
    private String limits;


    public MonitoringHedgeFundApprovedFundInfoDto(){}

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date approveDate) {
        this.approveDate = approveDate;
    }

    public String getLimits() {
        return limits;
    }

    public void setLimits(String limits) {
        this.limits = limits;
    }
}
