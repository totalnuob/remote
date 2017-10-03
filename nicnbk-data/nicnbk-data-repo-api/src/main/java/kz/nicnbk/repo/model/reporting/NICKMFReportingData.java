package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 18.07.2017.
 */



@Entity
@Table(name = "rep_nick_mf_report_data")
public class NICKMFReportingData extends CreateUpdateBaseEntity {

    private NICReportingChartOfAccounts nicReportingChartOfAccounts;

    private Double accountBalance;

    private PeriodicReport report;

    @ManyToOne
    @JoinColumn(name = "nic_chart_accounts_id")
    public NICReportingChartOfAccounts getNicReportingChartOfAccounts() {
        return nicReportingChartOfAccounts;
    }

    public void setNicReportingChartOfAccounts(NICReportingChartOfAccounts nicReportingChartOfAccounts) {
        this.nicReportingChartOfAccounts = nicReportingChartOfAccounts;
    }

    @Column(name="account_balance")
    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
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
