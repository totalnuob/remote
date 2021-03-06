package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import kz.nicnbk.repo.model.reporting.PeriodicDataChartAccountsType;

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
    private boolean addable;
//    private Boolean positiveOnly;
//    private Boolean negativeOnly;

    private PeriodicDataChartAccountsType chartAccountsType;

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
