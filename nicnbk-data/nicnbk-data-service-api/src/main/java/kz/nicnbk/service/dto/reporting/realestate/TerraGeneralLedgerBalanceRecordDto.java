package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by magzumov on 25.08.2017.
 */
public class TerraGeneralLedgerBalanceRecordDto implements BaseDto {

    private Long id;
    private String acronym;
    private Date balanceDate;
    private String financialStatementCategory;
    private String glSubclass;
    private String portfolioFund;

    private Double accountBalanceGP;
    private Double accountBalanceNICKMF;
    private Double accountBalanceGrandTotal;

    private Boolean excludeFromTerraCalculation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Date getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(Date balanceDate) {
        this.balanceDate = balanceDate;
    }

    public String getFinancialStatementCategory() {
        return financialStatementCategory;
    }

    public void setFinancialStatementCategory(String financialStatementCategory) {
        this.financialStatementCategory = financialStatementCategory;
    }

    public String getGlSubclass() {
        return glSubclass;
    }

    public void setGlSubclass(String glSubclass) {
        this.glSubclass = glSubclass;
    }

    public String getPortfolioFund() {
        return portfolioFund;
    }

    public void setPortfolioFund(String portfolioFund) {
        this.portfolioFund = portfolioFund;
    }

    public Double getAccountBalanceGP() {
        return accountBalanceGP;
    }

    public void setAccountBalanceGP(Double accountBalanceGP) {
        this.accountBalanceGP = accountBalanceGP;
    }

    public Double getAccountBalanceNICKMF() {
        return accountBalanceNICKMF;
    }

    public void setAccountBalanceNICKMF(Double accountBalanceNICKMF) {
        this.accountBalanceNICKMF = accountBalanceNICKMF;
    }

    public Double getAccountBalanceGrandTotal() {
        return accountBalanceGrandTotal;
    }

    public void setAccountBalanceGrandTotal(Double accountBalanceGrandTotal) {
        this.accountBalanceGrandTotal = accountBalanceGrandTotal;
    }

    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }
}
