package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import kz.nicnbk.repo.model.reporting.PeriodicDataChartAccountsType;

import javax.persistence.*;

/**
 * Created by magzumov on 18.07.2017.
 */



@Entity
@Table(name = "rep_singularity_nic_chart_of_accounts")
public class SingularityNICChartOfAccounts extends BaseEntity {

    private String singularityAccountNumber;
    private NICReportingChartOfAccounts nicReportingChartOfAccounts;

    //private Boolean addable;
//    private Boolean positiveOnly;
//    private Boolean negativeOnly;
    private PeriodicDataChartAccountsType chartAccountsType;


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
