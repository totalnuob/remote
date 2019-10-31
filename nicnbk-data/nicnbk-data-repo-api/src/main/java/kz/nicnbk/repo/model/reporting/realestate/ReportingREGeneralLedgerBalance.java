package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.hedgefunds.HFChartOfAccountsType;
import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rep_re_general_ledger_balance")
public class ReportingREGeneralLedgerBalance extends CreateUpdateBaseEntity{

    //private int tranche;
    private RETrancheType trancheType;
    private Date balanceDate;
    private FinancialStatementCategory financialStatementCategory;
    //private REChartOfAccountsType glSubclass;
    private String glSubclass;
    private String portfolioFund;

    private Double accountBalanceGP;
    private Double accountBalanceNICKMF;
    private Double accountBalanceGrandTotal;

    private Boolean excludeFromTerraCalculation;

//    private REChartOfAccountsType chartAccountsType;
//    private String chartAccountsLongDescription;
//    private String shortName;
//    private Double GLAccountBalance;
//    private Currency segValCCY;
//    private Currency fundCCY;
    private PeriodicReport report;
//
//    private Double adjustedRedemption;
//    private String interestRate;
//    private String comment;


//    public int getTranche() {
//        return tranche;
//    }
//
//    public void setTranche(int tranche) {
//        this.tranche = tranche;
//    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "re_tranche_type_id"/*, nullable = false*/)
    public RETrancheType getTrancheType() {
        return trancheType;
    }

    public void setTrancheType(RETrancheType trancheType) {
        this.trancheType = trancheType;
    }

    public Date getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(Date balanceDate) {
        this.balanceDate = balanceDate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fin_statmnt_type_id"/*, nullable = false*/)
    public FinancialStatementCategory getFinancialStatementCategory() {
        return financialStatementCategory;
    }

    public void setFinancialStatementCategory(FinancialStatementCategory financialStatementCategory) {
        this.financialStatementCategory = financialStatementCategory;
    }

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "re_chart_accounts_type_id"/*, nullable = false*/)
//    public REChartOfAccountsType getGlSubclass() {
//        return glSubclass;
//    }
//
//    public void setGlSubclass(REChartOfAccountsType glSubclass) {
//        this.glSubclass = glSubclass;
//    }


    @Column(name="gl_subclass")
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

    //    public String getGLAccount() {
//        return GLAccount;
//    }
//
//    public void setGLAccount(String GLAccount) {
//        this.GLAccount = GLAccount;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "chart_account_type_id"/*, nullable = false*/)
//    public REChartOfAccountsType getChartAccountsType() {
//        return chartAccountsType;
//    }
//
//    public void setChartAccountsType(REChartOfAccountsType chartAccountsType) {
//        this.chartAccountsType = chartAccountsType;
//    }
//
//    public String getChartAccountsLongDescription() {
//        return chartAccountsLongDescription;
//    }
//
//    public void setChartAccountsLongDescription(String chartAccountsLongDescription) {
//        this.chartAccountsLongDescription = chartAccountsLongDescription;
//    }
//
//    public Double getGLAccountBalance() {
//        return GLAccountBalance;
//    }
//
//    public void setGLAccountBalance(Double GLAccountBalance) {
//        this.GLAccountBalance = GLAccountBalance;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "seg_val_currency_id"/*, nullable = false*/)
//    public Currency getSegValCCY() {
//        return segValCCY;
//    }
//
//    public void setSegValCCY(Currency segValCCY) {
//        this.segValCCY = segValCCY;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "fund_currency_id"/*, nullable = false*/)
//    public Currency getFundCCY() {
//        return fundCCY;
//    }
//
//    public void setFundCCY(Currency fundCCY) {
//        this.fundCCY = fundCCY;
//    }
//
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }
//
//    @Column(name="short_name")
//    public String getShortName() {
//        return shortName;
//    }
//
//    public void setShortName(String shortName) {
//        this.shortName = shortName;
//    }
//
//    @Column(name="adjusted_redemption")
//    public Double getAdjustedRedemption() {
//        return adjustedRedemption;
//    }
//
//    public void setAdjustedRedemption(Double adjustedRedemption) {
//        this.adjustedRedemption = adjustedRedemption;
//    }
//
//    @Column(name="interest_rate")
//    public String getInterestRate() {
//        return interestRate;
//    }
//
//    public void setInterestRate(String interestRate) {
//        this.interestRate = interestRate;
//    }
//
//
//    @Column(name="comment")
//    public String getComment() {
//        return comment;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }


    @Column(name="exclude_from_terra_calc")
    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }
}
