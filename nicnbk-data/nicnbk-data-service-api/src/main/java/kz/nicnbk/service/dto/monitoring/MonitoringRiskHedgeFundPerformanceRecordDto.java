package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskHedgeFundPerformanceRecordDto implements BaseDto {
    private String name;
    private Double portfolioValue;
    private Double benchmarkValue;
    private Double portfolioBValue;
    private Double benchmarkAwcValue;

    private String portfolioValueTxt;
    private String benchmarkValueTxt;
    private String portfolioBValueTxt;
    private String benchmarkAwcValueTxt;

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

    public MonitoringRiskHedgeFundPerformanceRecordDto(String name, Double portfolioValue, Double benchmarkValue, Double portfolioBValue, Double benchmarkAwcValue,
                                                       String portfolioValueTxt, String benchmarkValueTxt, String portfolioBValueTxt, String benchmarkAwcValueTxt) {
        this.name = name;
        this.portfolioValue = portfolioValue;
        this.benchmarkValue = benchmarkValue;
        this.portfolioBValue = portfolioBValue;
        this.benchmarkAwcValue = benchmarkAwcValue;
        this.portfolioValueTxt = portfolioValueTxt;
        this.benchmarkValueTxt = benchmarkValueTxt;
        this.portfolioBValueTxt = portfolioBValueTxt;
        this.benchmarkAwcValueTxt = benchmarkAwcValueTxt;
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

    public Double getPortfolioBValue() {
        return portfolioBValue;
    }

    public void setPortfolioBValue(Double portfolioBValue) {
        this.portfolioBValue = portfolioBValue;
    }

    public Double getBenchmarkAwcValue() {
        return benchmarkAwcValue;
    }

    public void setBenchmarkAwcValue(Double benchmarkAwcValue) {
        this.benchmarkAwcValue = benchmarkAwcValue;
    }

    public String getPortfolioBValueTxt() {
        return portfolioBValueTxt;
    }

    public void setPortfolioBValueTxt(String portfolioBValueTxt) {
        this.portfolioBValueTxt = portfolioBValueTxt;
    }

    public String getBenchmarkAwcValueTxt() {
        return benchmarkAwcValueTxt;
    }

    public void setBenchmarkAwcValueTxt(String benchmarkAwcValueTxt) {
        this.benchmarkAwcValueTxt = benchmarkAwcValueTxt;
    }
}
