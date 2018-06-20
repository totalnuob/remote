package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_re_securities_cost")
public class ReportingRESecuritiesCost extends CreateUpdateBaseEntity{

    private String name;
    private Double totalPosition;
    private Double costPerShareFCY;
    private Double totalCostFCY;
    private Double costLCYHistorical;
    private Double costLCYCurrentFXRate;
    private Double marketPriceFCY;
    private Double unrealizedGainFCY;
    private Double unrealizedGainLCY;
    private Double FXGainLCY;
    private Double marketValueFCY;
    private Boolean isTotalSum;

    private PeriodicReport report;

    private Boolean excludeFromTerraCalculation;


    @Column(name = "name", length = DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "total_position")
    public Double getTotalPosition() {
        return totalPosition;
    }

    public void setTotalPosition(Double totalPosition) {
        this.totalPosition = totalPosition;
    }

    @Column(name = "cost_per_share_fcy")
    public Double getCostPerShareFCY() {
        return costPerShareFCY;
    }

    public void setCostPerShareFCY(Double costPerShareFCY) {
        this.costPerShareFCY = costPerShareFCY;
    }

    @Column(name = "total_cost_fcy")
    public Double getTotalCostFCY() {
        return totalCostFCY;
    }

    public void setTotalCostFCY(Double totalCostFCY) {
        this.totalCostFCY = totalCostFCY;
    }

    @Column(name = "cost_lcy_historical")
    public Double getCostLCYHistorical() {
        return costLCYHistorical;
    }

    public void setCostLCYHistorical(Double costLCYHistorical) {
        this.costLCYHistorical = costLCYHistorical;
    }

    @Column(name = "cost_lcy_current_fx_rate")
    public Double getCostLCYCurrentFXRate() {
        return costLCYCurrentFXRate;
    }

    public void setCostLCYCurrentFXRate(Double costLCYCurrentFXRate) {
        this.costLCYCurrentFXRate = costLCYCurrentFXRate;
    }

    @Column(name = "unrealized_gain_fcy")
    public Double getUnrealizedGainFCY() {
        return unrealizedGainFCY;
    }

    public void setUnrealizedGainFCY(Double unrealizedGainFCY) {
        this.unrealizedGainFCY = unrealizedGainFCY;
    }

    @Column(name = "unrealized_gain_lcy")
    public Double getUnrealizedGainLCY() {
        return unrealizedGainLCY;
    }

    public void setUnrealizedGainLCY(Double unrealizedGainLCY) {
        this.unrealizedGainLCY = unrealizedGainLCY;
    }

    @Column(name = "fx_gain_lcy")
    public Double getFXGainLCY() {
        return FXGainLCY;
    }

    public void setFXGainLCY(Double FXGainLCY) {
        this.FXGainLCY = FXGainLCY;
    }

    @Column(name = "market_value_fcy")
    public Double getMarketValueFCY() {
        return marketValueFCY;
    }

    public void setMarketValueFCY(Double marketValueFCY) {
        this.marketValueFCY = marketValueFCY;
    }

    @Column(name = "is_total_sum")
    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @Column(name="exclude_from_terra_calc")
    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }

    @Column(name="market_price_fcy")
    public Double getMarketPriceFCY() {
        return marketPriceFCY;
    }

    public void setMarketPriceFCY(Double marketPriceFCY) {
        this.marketPriceFCY = marketPriceFCY;
    }
}
