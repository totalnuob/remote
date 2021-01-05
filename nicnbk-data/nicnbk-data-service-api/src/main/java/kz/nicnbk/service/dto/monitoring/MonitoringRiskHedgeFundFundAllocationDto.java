package kz.nicnbk.service.dto.monitoring;

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
