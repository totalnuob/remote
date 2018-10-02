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
    private Boolean positiveOnly;
    private Boolean negativeOnly;

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

    @Column(name="positive_only")
    public Boolean getPositiveOnly() {
        return positiveOnly;
    }

    public void setPositiveOnly(Boolean positiveOnly) {
        this.positiveOnly = positiveOnly;
    }

    @Column(name="negative_only")
    public Boolean getNegativeOnly() {
        return negativeOnly;
    }

    public void setNegativeOnly(Boolean negativeOnly) {
        this.negativeOnly = negativeOnly;
    }
}
