package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_hf_general_ledger_balance")
public class ReportingHFGeneralLedgerBalance extends CreateUpdateBaseEntity{

    private int tranche;
    private Date balanceDate;
    private FinancialStatementCategory financialStatementCategory;
    private String GLAccount;
    private HFChartOfAccountsType chartAccountsType;
    private String chartAccountsLongDescription;
    private String shortName;
    private Double GLAccountBalance;
    private Currency segValCCY;
    private Currency fundCCY;
    private PeriodicReport report;

    private Double adjustedRedemption;
    private String interestRate;


    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
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

    public String getGLAccount() {
        return GLAccount;
    }

    public void setGLAccount(String GLAccount) {
        this.GLAccount = GLAccount;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chart_account_type_id"/*, nullable = false*/)
    public HFChartOfAccountsType getChartAccountsType() {
        return chartAccountsType;
    }

    public void setChartAccountsType(HFChartOfAccountsType chartAccountsType) {
        this.chartAccountsType = chartAccountsType;
    }

    public String getChartAccountsLongDescription() {
        return chartAccountsLongDescription;
    }

    public void setChartAccountsLongDescription(String chartAccountsLongDescription) {
        this.chartAccountsLongDescription = chartAccountsLongDescription;
    }

    public Double getGLAccountBalance() {
        return GLAccountBalance;
    }

    public void setGLAccountBalance(Double GLAccountBalance) {
        this.GLAccountBalance = GLAccountBalance;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seg_val_currency_id"/*, nullable = false*/)
    public Currency getSegValCCY() {
        return segValCCY;
    }

    public void setSegValCCY(Currency segValCCY) {
        this.segValCCY = segValCCY;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fund_currency_id"/*, nullable = false*/)
    public Currency getFundCCY() {
        return fundCCY;
    }

    public void setFundCCY(Currency fundCCY) {
        this.fundCCY = fundCCY;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @Column(name="short_name")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name="adjusted_redemption")
    public Double getAdjustedRedemption() {
        return adjustedRedemption;
    }

    public void setAdjustedRedemption(Double adjustedRedemption) {
        this.adjustedRedemption = adjustedRedemption;
    }

    @Column(name="interest_rate")
    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }
}
