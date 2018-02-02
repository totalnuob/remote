package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;

import javax.persistence.*;

/**
 * Created by magzumov on 18.07.2017.
 */



@Entity
@Table(name = "rep_tarragon_nic_chart_of_accounts")
public class TarragonNICChartOfAccounts extends BaseEntity {


    // TODO: tarragonChartOfAccountsName-nicReportingChartOfAccounts UNIQUE

    private String tarragonChartOfAccountsName;
    private NICReportingChartOfAccounts nicReportingChartOfAccounts;
    private Boolean addable;

    @Column(name="tarragon_chart_of_accounts_name", nullable = false)
    public String getTarragonChartOfAccountsName() {
        return tarragonChartOfAccountsName;
    }

    public void setTarragonChartOfAccountsName(String tarragonChartOfAccountsName) {
        this.tarragonChartOfAccountsName = tarragonChartOfAccountsName;
    }

    @ManyToOne
    @JoinColumn(name = "nic_chart_accounts_id", nullable = false)
    public NICReportingChartOfAccounts getNicReportingChartOfAccounts() {
        return nicReportingChartOfAccounts;
    }

    public void setNicReportingChartOfAccounts(NICReportingChartOfAccounts nicReportingChartOfAccounts) {
        this.nicReportingChartOfAccounts = nicReportingChartOfAccounts;
    }

    public Boolean getAddable() {
        return addable;
    }

    public void setAddable(Boolean addable) {
        this.addable = addable;
    }
}
