package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;

import javax.persistence.*;

/**
 * Created by magzumov on 18.07.2017.
 */



@Entity
@Table(name = "rep_terra_nic_chart_of_accounts")
public class TerraNICChartOfAccounts extends BaseEntity {

    // TODO: tarragonChartOfAccountsName-nicReportingChartOfAccounts UNIQUE

    private String terraChartOfAccountsName;
    private NICReportingChartOfAccounts nicReportingChartOfAccounts;
    private Boolean addable;

    @Column(name="terra_chart_of_accounts_name", nullable = false)
    public String getTerraChartOfAccountsName() {
        return terraChartOfAccountsName;
    }

    public void setTerraChartOfAccountsName(String terraChartOfAccountsName) {
        this.terraChartOfAccountsName = terraChartOfAccountsName;
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
