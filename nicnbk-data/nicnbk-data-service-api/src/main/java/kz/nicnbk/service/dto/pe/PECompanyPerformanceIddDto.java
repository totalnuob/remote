package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.pe.PECompanyPerformanceIdd;

/**
 * Created by Pak on 17.10.2017.
 */
public class PECompanyPerformanceIddDto extends BaseEntityDto<PECompanyPerformanceIdd> {

    private String companyName;
    private Double invested;
    private Double realized;
    private Double unrealized;
    private Double totalValue;
    private Double multiple;
    private Boolean autoCalculation;
    private Double grossIrr;
    private Double netIrr;
//    private PEFundDto fund;

    private String companyDescription;
    private String industry;
    private String country;
    private String typeOfInvestment;
    private String control;
    private String dealSource;
    private String currency;

    public PECompanyPerformanceIddDto() {
    }

    public PECompanyPerformanceIddDto(String companyName,
                                      Double invested,
                                      Double realized,
                                      Double unrealized,
                                      Double totalValue,
                                      Double multiple,
                                      Boolean autoCalculation,
                                      Double grossIrr,
                                      Double netIrr,
                                      String companyDescription,
                                      String industry,
                                      String country,
                                      String typeOfInvestment,
                                      String control,
                                      String dealSource,
                                      String currency) {
        this.companyName = companyName;
        this.invested = invested;
        this.realized = realized;
        this.unrealized = unrealized;
        this.totalValue = totalValue;
        this.multiple = multiple;
        this.autoCalculation = autoCalculation;
        this.grossIrr = grossIrr;
        this.netIrr = netIrr;
        this.companyDescription = companyDescription;
        this.industry = industry;
        this.country = country;
        this.typeOfInvestment = typeOfInvestment;
        this.control = control;
        this.dealSource = dealSource;
        this.currency = currency;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getInvested() {
        return invested;
    }

    public void setInvested(Double invested) {
        this.invested = invested;
    }

    public Double getRealized() {
        return realized;
    }

    public void setRealized(Double realized) {
        this.realized = realized;
    }

    public Double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(Double unrealized) {
        this.unrealized = unrealized;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public Double getMultiple() {
        return multiple;
    }

    public void setMultiple(Double multiple) {
        this.multiple = multiple;
    }

    public Boolean getAutoCalculation() {
        return autoCalculation;
    }

    public void setAutoCalculation(Boolean autoCalculation) {
        this.autoCalculation = autoCalculation;
    }

    public Double getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(Double grossIrr) {
        this.grossIrr = grossIrr;
    }

    public Double getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(Double netIrr) {
        this.netIrr = netIrr;
    }

//    public PEFundDto getFund() {
//        return fund;
//    }
//
//    public void setFund(PEFundDto fund) {
//        this.fund = fund;
//    }


    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTypeOfInvestment() {
        return typeOfInvestment;
    }

    public void setTypeOfInvestment(String typeOfInvestment) {
        this.typeOfInvestment = typeOfInvestment;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getDealSource() {
        return dealSource;
    }

    public void setDealSource(String dealSource) {
        this.dealSource = dealSource;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
