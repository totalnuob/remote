package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundTopFundInfoDto implements BaseDto{

    private String fundName;
    private String strategy;
    private Double ytd;
    private Double allocation;

    public MonitoringHedgeFundTopFundInfoDto(){}

    public MonitoringHedgeFundTopFundInfoDto(String fundName, String strategy, Double ytd, Double allocation){
        this.fundName = fundName;
        this.strategy = strategy;
        this.ytd = ytd;
        this.allocation = allocation;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    public Double getAllocation() {
        return allocation;
    }

    public void setAllocation(Double allocation) {
        this.allocation = allocation;
    }
}
