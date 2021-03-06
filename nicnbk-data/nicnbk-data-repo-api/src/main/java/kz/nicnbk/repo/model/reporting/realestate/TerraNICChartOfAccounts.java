package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import kz.nicnbk.repo.model.reporting.PeriodicDataChartAccountsType;

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
    private boolean addable;
//    private Boolean positiveOnly;
//    private Boolean negativeOnly;
    private PeriodicDataChartAccountsType chartAccountsType;

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

    public boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

//    @Column(name="positive_only")
//    public Boolean getPositiveOnly() {
//        return positiveOnly;
//    }
//
//    public void setPositiveOnly(Boolean positiveOnly) {
//        this.positiveOnly = positiveOnly;
//    }
//
//    @Column(name="negative_only")
//    public Boolean getNegativeOnly() {
//        return negativeOnly;
//    }
//
//    public void setNegativeOnly(Boolean negativeOnly) {
//        this.negativeOnly = negativeOnly;
//    }

    @ManyToOne
    @JoinColumn(name = "chart_accounts_type_id"/*, nullable = false*/)
    public PeriodicDataChartAccountsType getChartAccountsType() {
        return chartAccountsType;
    }

    public void setChartAccountsType(PeriodicDataChartAccountsType chartAccountsType) {
        this.chartAccountsType = chartAccountsType;
    }
}
