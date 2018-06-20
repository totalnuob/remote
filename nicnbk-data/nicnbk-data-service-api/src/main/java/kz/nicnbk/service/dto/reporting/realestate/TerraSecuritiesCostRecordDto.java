package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;

/**
 * Created by magzumov on 09.10.2017.
 */
public class TerraSecuritiesCostRecordDto implements BaseDto {
    private Long id;
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

    private Boolean excludeFromTerraCalculation;

    private PeriodicReportDto report;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalPosition() {
        return totalPosition;
    }

    public void setTotalPosition(Double totalPosition) {
        this.totalPosition = totalPosition;
    }

    public Double getCostPerShareFCY() {
        return costPerShareFCY;
    }

    public void setCostPerShareFCY(Double costPerShareFCY) {
        this.costPerShareFCY = costPerShareFCY;
    }

    public Double getTotalCostFCY() {
        return totalCostFCY;
    }

    public void setTotalCostFCY(Double totalCostFCY) {
        this.totalCostFCY = totalCostFCY;
    }

    public Double getCostLCYHistorical() {
        return costLCYHistorical;
    }

    public void setCostLCYHistorical(Double costLCYHistorical) {
        this.costLCYHistorical = costLCYHistorical;
    }

    public Double getCostLCYCurrentFXRate() {
        return costLCYCurrentFXRate;
    }

    public void setCostLCYCurrentFXRate(Double costLCYCurrentFXRate) {
        this.costLCYCurrentFXRate = costLCYCurrentFXRate;
    }

    public Double getUnrealizedGainFCY() {
        return unrealizedGainFCY;
    }

    public void setUnrealizedGainFCY(Double unrealizedGainFCY) {
        this.unrealizedGainFCY = unrealizedGainFCY;
    }

    public Double getUnrealizedGainLCY() {
        return unrealizedGainLCY;
    }

    public void setUnrealizedGainLCY(Double unrealizedGainLCY) {
        this.unrealizedGainLCY = unrealizedGainLCY;
    }

    public Double getFXGainLCY() {
        return FXGainLCY;
    }

    public void setFXGainLCY(Double FXGainLCY) {
        this.FXGainLCY = FXGainLCY;
    }

    public Double getMarketValueFCY() {
        return marketValueFCY;
    }

    public void setMarketValueFCY(Double marketValueFCY) {
        this.marketValueFCY = marketValueFCY;
    }

    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }

    public Double getMarketPriceFCY() {
        return marketPriceFCY;
    }

    public void setMarketPriceFCY(Double marketPriceFCY) {
        this.marketPriceFCY = marketPriceFCY;
    }
}
