package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;

import javax.persistence.*;

/**
 * Created by magzumov on 18.07.2017.
 */



@Entity
@Table(name = "rep_singularity_nic_chart_of_accounts")
public class SingularityNICChartOfAccounts extends BaseEntity {

    private String singularityAccountNumber;
    private NICReportingChartOfAccounts nicReportingChartOfAccounts;

    @Column(name="singularity_account_number", nullable = false)
    public String getSingularityAccountNumber() {
        return singularityAccountNumber;
    }

    public void setSingularityAccountNumber(String singularityAccountNumber) {
        this.singularityAccountNumber = singularityAccountNumber;
    }

    @ManyToOne
    @JoinColumn(name = "nic_chart_accounts_id", nullable = false)
    public NICReportingChartOfAccounts getNicReportingChartOfAccounts() {
        return nicReportingChartOfAccounts;
    }

    public void setNicReportingChartOfAccounts(NICReportingChartOfAccounts nicReportingChartOfAccounts) {
        this.nicReportingChartOfAccounts = nicReportingChartOfAccounts;
    }
}
