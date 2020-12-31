package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.service.dto.common.ResponseDto;

import java.util.Date;
import java.util.List;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskHedgeFundReportDto extends ResponseDto {
    private Date date;
    private List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations;
    private String topFundAllocationsWarning;
    private String topFundAllocationsError;

    private List<MonitoringRiskHedgeFundBetaFactorDto> factorBetas;
    private String factorBetasWarning;
    private String factorBetasError;

    private List<MonitoringRiskHedgeFundPortfolioVarDto> portfolioVars;
    private String portfolioVarsWarning;
    private String portfolioVarsError;

    private List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesMSCI;
    private List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesBarclays;
    private String marketSensitivityWarning;
    private String marketSensitivityError;

    private List<MonitoringRiskHedgeFundPerformanceRecordDto> performance12M;
    private List<MonitoringRiskHedgeFundPerformanceRecordDto> performanceSinceInception;
    private String performanceWarning;
    private String performanceError;

    private List<RiskStressTestsDto> stressTests;
    private String stressTestsWarning;
    private String stressTestsError;

    private List<MonitoringRiskHedgeFundAllocationSubStrategyDto> subStrategyAllocations;
    private String subStrategyAllocationsWarning;
    private String subStrategyAllocationsError;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<MonitoringRiskHedgeFundFundAllocationDto> getTopFundAllocations() {
        return topFundAllocations;
    }

    public void setTopFundAllocations(List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations) {
        this.topFundAllocations = topFundAllocations;
    }

    public List<MonitoringRiskHedgeFundBetaFactorDto> getFactorBetas() {
        return factorBetas;
    }

    public void setFactorBetas(List<MonitoringRiskHedgeFundBetaFactorDto> factorBetas) {
        this.factorBetas = factorBetas;
    }

    public List<MonitoringRiskHedgeFundPortfolioVarDto> getPortfolioVars() {
        return portfolioVars;
    }

    public void setPortfolioVars(List<MonitoringRiskHedgeFundPortfolioVarDto> portfolioVars) {
        this.portfolioVars = portfolioVars;
    }

    public List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> getMarketSensitivitesMSCI() {
        return marketSensitivitesMSCI;
    }

    public void setMarketSensitivitesMSCI(List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesMSCI) {
        this.marketSensitivitesMSCI = marketSensitivitesMSCI;
    }

    public List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> getMarketSensitivitesBarclays() {
        return marketSensitivitesBarclays;
    }

    public void setMarketSensitivitesBarclays(List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesBarclays) {
        this.marketSensitivitesBarclays = marketSensitivitesBarclays;
    }

    public List<MonitoringRiskHedgeFundPerformanceRecordDto> getPerformance12M() {
        return performance12M;
    }

    public void setPerformance12M(List<MonitoringRiskHedgeFundPerformanceRecordDto> performance12M) {
        this.performance12M = performance12M;
    }

    public List<MonitoringRiskHedgeFundPerformanceRecordDto> getPerformanceSinceInception() {
        return performanceSinceInception;
    }

    public void setPerformanceSinceInception(List<MonitoringRiskHedgeFundPerformanceRecordDto> performanceSinceInception) {
        this.performanceSinceInception = performanceSinceInception;
    }

    public List<RiskStressTestsDto> getStressTests() {
        return stressTests;
    }

    public void setStressTests(List<RiskStressTestsDto> stressTests) {
        this.stressTests = stressTests;
    }

    public String getTopFundAllocationsWarning() {
        return topFundAllocationsWarning;
    }

    public void setTopFundAllocationsWarning(String topFundAllocationsWarning) {
        this.topFundAllocationsWarning = topFundAllocationsWarning;
    }

    public String getTopFundAllocationsError() {
        return topFundAllocationsError;
    }

    public void setTopFundAllocationsError(String topFundAllocationsError) {
        this.topFundAllocationsError = topFundAllocationsError;
    }

    public String getFactorBetasWarning() {
        return factorBetasWarning;
    }

    public void setFactorBetasWarning(String factorBetasWarning) {
        this.factorBetasWarning = factorBetasWarning;
    }

    public String getFactorBetasError() {
        return factorBetasError;
    }

    public void setFactorBetasError(String factorBetasError) {
        this.factorBetasError = factorBetasError;
    }

    public String getMarketSensitivityWarning() {
        return marketSensitivityWarning;
    }

    public void setMarketSensitivityWarning(String marketSensitivityWarning) {
        this.marketSensitivityWarning = marketSensitivityWarning;
    }

    public String getMarketSensitivityError() {
        return marketSensitivityError;
    }

    public void setMarketSensitivityError(String marketSensitivityError) {
        this.marketSensitivityError = marketSensitivityError;
    }

    public String getPortfolioVarsWarning() {
        return portfolioVarsWarning;
    }

    public void setPortfolioVarsWarning(String portfolioVarsWarning) {
        this.portfolioVarsWarning = portfolioVarsWarning;
    }

    public String getPortfolioVarsError() {
        return portfolioVarsError;
    }

    public void setPortfolioVarsError(String portfolioVarsError) {
        this.portfolioVarsError = portfolioVarsError;
    }

    public String getPerformanceWarning() {
        return performanceWarning;
    }

    public void setPerformanceWarning(String performanceWarning) {
        this.performanceWarning = performanceWarning;
    }

    public String getPerformanceError() {
        return performanceError;
    }

    public void setPerformanceError(String performanceError) {
        this.performanceError = performanceError;
    }

    public String getStressTestsWarning() {
        return stressTestsWarning;
    }

    public void setStressTestsWarning(String stressTestsWarning) {
        this.stressTestsWarning = stressTestsWarning;
    }

    public String getStressTestsError() {
        return stressTestsError;
    }

    public void setStressTestsError(String stressTestsError) {
        this.stressTestsError = stressTestsError;
    }

    public List<MonitoringRiskHedgeFundAllocationSubStrategyDto> getSubStrategyAllocations() {
        return subStrategyAllocations;
    }

    public void setSubStrategyAllocations(List<MonitoringRiskHedgeFundAllocationSubStrategyDto> subStrategyAllocations) {
        this.subStrategyAllocations = subStrategyAllocations;
    }

    public String getSubStrategyAllocationsWarning() {
        return subStrategyAllocationsWarning;
    }

    public void setSubStrategyAllocationsWarning(String subStrategyAllocationsWarning) {
        this.subStrategyAllocationsWarning = subStrategyAllocationsWarning;
    }

    public String getSubStrategyAllocationsError() {
        return subStrategyAllocationsError;
    }

    public void setSubStrategyAllocationsError(String subStrategyAllocationsError) {
        this.subStrategyAllocationsError = subStrategyAllocationsError;
    }
}
