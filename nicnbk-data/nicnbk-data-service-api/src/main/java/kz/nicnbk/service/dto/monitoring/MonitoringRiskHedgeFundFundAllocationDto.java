package kz.nicnbk.service.dto.monitoring;

import com.bloomberglp.blpapi.impl.D;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.MathUtils;

import java.util.Date;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskHedgeFundFundAllocationDto implements BaseDto, Comparable {
    private String fundName;
    private String className;
    private Double nav;
    private Double navPercent;
    private Double mtd;
    private Double qtd;
    private Double ytd;
    private Double contributionToYTD;
    private Double contributionToVAR;
    //private Double ctr;

    public MonitoringRiskHedgeFundFundAllocationDto(){}

    public MonitoringRiskHedgeFundFundAllocationDto(String fundName, String className, Double nav, Double navPercent,
                                                    Double mtd, Double qtd, Double ytd, Double contributionToYTD, Double contributionToVAR){
        this.fundName = fundName;
        this.className = className;
        this.nav = nav;
        this.navPercent = navPercent;
        this.mtd = mtd;
        this.qtd = qtd;
        this.ytd = ytd;
        this.contributionToYTD = contributionToYTD;
        this.contributionToVAR = contributionToVAR;

    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getNav() {
        return nav;
    }

    public void setNav(Double nav) {
        this.nav = nav;
    }

    public Double getMtd() {
        return mtd;
    }

    public void setMtd(Double mtd) {
        this.mtd = mtd;
    }

    public Double getQtd() {
        return qtd;
    }

    public void setQtd(Double qtd) {
        this.qtd = qtd;
    }

    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    public Double getContributionToYTD() {
        return contributionToYTD;
    }

    public void setContributionToYTD(Double contributionToYTD) {
        this.contributionToYTD = contributionToYTD;
    }

    public Double getContributionToVAR() {
        return contributionToVAR;
    }

    public void setContributionToVAR(Double contributionToVAR) {
        this.contributionToVAR = contributionToVAR;
    }

    public Double getNavPercent() {
        return navPercent;
    }

    public void setNavPercent(Double navPercent) {
        this.navPercent = navPercent;
    }

//    public Double getCtr() {
//        return ctr;
//    }
//
//    public void setCtr(Double ctr) {
//        this.ctr = ctr;
//    }

    @Override
    public int compareTo(Object o) {
        if(this.nav != null && ((MonitoringRiskHedgeFundFundAllocationDto) o).nav != null){
            Double diff = MathUtils.subtract(this.nav, ((MonitoringRiskHedgeFundFundAllocationDto) o).nav);
            return diff > 0 ? -1 : diff < 0 ? 1 : 0;
        }else if(this.nav != null){
            return 1;
        }else{
            return -1;
        }
    }
}
