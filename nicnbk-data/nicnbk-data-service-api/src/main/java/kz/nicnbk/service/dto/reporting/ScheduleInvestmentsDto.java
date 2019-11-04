package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.StringUtils;

/**
 * Created by magzumov on 05.05.2017.
 */

public class ScheduleInvestmentsDto implements BaseDto{

    private Long id;
    private String name;
    private Double capitalCommitments;
    private Double netCost;
    private Double fairValue;
    private Double editedFairValue;
    private PeriodicReportDto report;

    private BaseDictionaryDto type;
    private BaseDictionaryDto strategy;
    private BaseDictionaryDto currency;
    private Integer tranche;
    private String description;
    private Boolean isTotalSum;

    private Boolean excludeFromTarragonCalculation;

    // updated input data for Schedule of Investments
    private String securityNo;
    private String investment;
    private BaseDictionaryDto trancheType;
    private Double exchangeRateRatioUSD;
    private Double investmentCommitment;
    private Double unfundedCommitment;
    private Double investmentCommitmentUSD;
    private Double unfundedCommitmentUSD;
    private Double contributionsUSD;
    private Double returnOfCapitalDistributionsUSD;
    private Double unrealizedGainLossUSD;
    private Double realizedGainLossUSD;
    private String operatingCompany;
    private String ownershipDetails;

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

    public Double getCapitalCommitments() {
        return capitalCommitments;
    }

    public void setCapitalCommitments(Double capitalCommitments) {
        this.capitalCommitments = capitalCommitments;
    }

    public Double getNetCost() {
        return netCost;
    }

    public void setNetCost(Double netCost) {
        this.netCost = netCost;
    }

    public Double getFairValue() {
        return fairValue;
    }

    public void setFairValue(Double fairValue) {
        this.fairValue = fairValue;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public BaseDictionaryDto getType() {
        return type;
    }

    public void setType(BaseDictionaryDto type) {
        this.type = type;
    }

    public BaseDictionaryDto getStrategy() {
        return strategy;
    }

    public void setStrategy(BaseDictionaryDto strategy) {
        this.strategy = strategy;
    }

    public BaseDictionaryDto getCurrency() {
        return currency;
    }

    public void setCurrency(BaseDictionaryDto currency) {
        this.currency = currency;
    }

    public Integer getTranche() {
        return tranche;
    }

    public void setTranche(Integer tranche) {
        this.tranche = tranche;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    public Double getEditedFairValue() {
        return editedFairValue;
    }

    public void setEditedFairValue(Double editedFairValue) {
        this.editedFairValue = editedFairValue;
    }

    public Boolean getExcludeFromTarragonCalculation() {
        return excludeFromTarragonCalculation;
    }

    public void setExcludeFromTarragonCalculation(Boolean excludeFromTarragonCalculation) {
        this.excludeFromTarragonCalculation = excludeFromTarragonCalculation;
    }

    public String getSecurityNo() {
        return securityNo;
    }

    public void setSecurityNo(String securityNo) {
        this.securityNo = securityNo;
    }

    public String getInvestment() {
        return investment;
    }

    public void setInvestment(String investment) {
        this.investment = investment;
    }

    public BaseDictionaryDto getTrancheType() {
        return trancheType;
    }

    public void setTrancheType(BaseDictionaryDto trancheType) {
        this.trancheType = trancheType;
    }

    public Double getExchangeRateRatioUSD() {
        return exchangeRateRatioUSD;
    }

    public void setExchangeRateRatioUSD(Double exchangeRateRatioUSD) {
        this.exchangeRateRatioUSD = exchangeRateRatioUSD;
    }

    public Double getInvestmentCommitment() {
        return investmentCommitment;
    }

    public void setInvestmentCommitment(Double investmentCommitment) {
        this.investmentCommitment = investmentCommitment;
    }

    public Double getUnfundedCommitment() {
        return unfundedCommitment;
    }

    public void setUnfundedCommitment(Double unfundedCommitment) {
        this.unfundedCommitment = unfundedCommitment;
    }

    public Double getInvestmentCommitmentUSD() {
        return investmentCommitmentUSD;
    }

    public void setInvestmentCommitmentUSD(Double investmentCommitmentUSD) {
        this.investmentCommitmentUSD = investmentCommitmentUSD;
    }

    public Double getUnfundedCommitmentUSD() {
        return unfundedCommitmentUSD;
    }

    public void setUnfundedCommitmentUSD(Double unfundedCommitmentUSD) {
        this.unfundedCommitmentUSD = unfundedCommitmentUSD;
    }

    public Double getContributionsUSD() {
        return contributionsUSD;
    }

    public void setContributionsUSD(Double contributionsUSD) {
        this.contributionsUSD = contributionsUSD;
    }

    public Double getReturnOfCapitalDistributionsUSD() {
        return returnOfCapitalDistributionsUSD;
    }

    public void setReturnOfCapitalDistributionsUSD(Double returnOfCapitalDistributionsUSD) {
        this.returnOfCapitalDistributionsUSD = returnOfCapitalDistributionsUSD;
    }

    public Double getUnrealizedGainLossUSD() {
        return unrealizedGainLossUSD;
    }

    public void setUnrealizedGainLossUSD(Double unrealizedGainLossUSD) {
        this.unrealizedGainLossUSD = unrealizedGainLossUSD;
    }

    public Double getRealizedGainLossUSD() {
        return realizedGainLossUSD;
    }

    public void setRealizedGainLossUSD(Double realizedGainLossUSD) {
        this.realizedGainLossUSD = realizedGainLossUSD;
    }

    public String getOperatingCompany() {
        return operatingCompany;
    }

    public void setOperatingCompany(String operatingCompany) {
        this.operatingCompany = operatingCompany;
    }

    public String getOwnershipDetails() {
        return ownershipDetails;
    }

    public void setOwnershipDetails(String ownershipDetails) {
        this.ownershipDetails = ownershipDetails;
    }
    public boolean isEmpty(){
        return StringUtils.isEmpty(this.investment) &&
                (this.trancheType == null  || StringUtils.isEmpty(this.trancheType.getCode())) &&
                (this.type == null || StringUtils.isEmpty(this.type.getCode())) &&
                (this.strategy == null || StringUtils.isEmpty(this.strategy.getCode())) &&
                this.exchangeRateRatioUSD == null && this.investmentCommitment == null &&
                this.unfundedCommitment == null && this.investmentCommitmentUSD == null &&
                this.contributionsUSD == null && this.returnOfCapitalDistributionsUSD == null &&
                this.netCost == null && this.fairValue == null &&
                this.unrealizedGainLossUSD == null && this.realizedGainLossUSD == null &&
                StringUtils.isEmpty(this.operatingCompany) && StringUtils.isEmpty(this.ownershipDetails);
    }

    public boolean isTrancheAType(){
        return this.trancheType != null && this.trancheType.getCode().startsWith("TARR_A");
    }
}
