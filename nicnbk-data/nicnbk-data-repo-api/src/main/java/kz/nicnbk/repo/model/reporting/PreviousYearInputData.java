package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_prev_year_input_data")
public class PreviousYearInputData extends CreateUpdateBaseEntity{

    private NICReportingChartOfAccounts chartOfAccounts;
    private Double accountBalance;
    private Double accountBalanceKZT;
    private PeriodicReport report;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nic_chart_accounts_id"/*, nullable = false*/)
    public NICReportingChartOfAccounts getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(NICReportingChartOfAccounts chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    @Column(name="account_balance")
    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Column(name="account_balance_kzt")
    public Double getAccountBalanceKZT() {
        return accountBalanceKZT;
    }

    public void setAccountBalanceKZT(Double accountBalanceKZT) {
        this.accountBalanceKZT = accountBalanceKZT;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }


}
