package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.MathUtils;

public class MonitoringRiskHedgeFundAllocationSubStrategyDto implements BaseDto, Comparable {
    private String strategyName;
    private String subStrategyName;
    private String className;
    private Double nav;
    private Double navPercent;
    private Double mtd;
    private Double ytd;



    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getSubStrategyName() {
        return subStrategyName;
    }

    public void setSubStrategyName(String subStrategyName) {
        this.subStrategyName = subStrategyName;
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

    public Double getNavPercent() {
        return navPercent;
    }

    public void setNavPercent(Double navPercent) {
        this.navPercent = navPercent;
    }

    public Double getMtd() {
        return mtd;
    }

    public void setMtd(Double mtd) {
        this.mtd = mtd;
    }

    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    @Override
    public int compareTo(Object o) {
        if(this.nav != null && ((MonitoringRiskHedgeFundAllocationSubStrategyDto) o).nav != null){
            Double diff = MathUtils.subtract(this.nav, ((MonitoringRiskHedgeFundAllocationSubStrategyDto) o).nav);
            return diff > 0 ? -1 : diff < 0 ? 1 : 0;
        }else if(this.nav != null){
            return 1;
        }else{
            return -1;
        }
    }
}
