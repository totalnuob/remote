package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskHedgeFundPerformanceRecordDto implements BaseDto {
    private String name;
    private Double portfolioValue;
    private Double benchmarkValue;

    private String portfolioValueTxt;
    private String benchmarkValueTxt;


    public MonitoringRiskHedgeFundPerformanceRecordDto(){}

    public MonitoringRiskHedgeFundPerformanceRecordDto(String name, Double portfolioValue, Double benchmarkValue){
        this.name = name;
        this.portfolioValue = portfolioValue;
        this.benchmarkValue = benchmarkValue;
    }

    public MonitoringRiskHedgeFundPerformanceRecordDto(String name, Double portfolioValue, Double benchmarkValue, String portfolioValueTxt, String benchmarkValueTxt){
        this.name = name;
        this.portfolioValue = portfolioValue;
        this.benchmarkValue = benchmarkValue;
        this.portfolioValueTxt = portfolioValueTxt;
        this.benchmarkValueTxt = benchmarkValueTxt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(Double portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public Double getBenchmarkValue() {
        return benchmarkValue;
    }

    public void setBenchmarkValue(Double benchmarkValue) {
        this.benchmarkValue = benchmarkValue;
    }

    public String getPortfolioValueTxt() {
        return portfolioValueTxt;
    }

    public void setPortfolioValueTxt(String portfolioValueTxt) {
        this.portfolioValueTxt = portfolioValueTxt;
    }

    public String getBenchmarkValueTxt() {
        return benchmarkValueTxt;
    }

    public void setBenchmarkValueTxt(String benchmarkValueTxt) {
        this.benchmarkValueTxt = benchmarkValueTxt;
    }
}
