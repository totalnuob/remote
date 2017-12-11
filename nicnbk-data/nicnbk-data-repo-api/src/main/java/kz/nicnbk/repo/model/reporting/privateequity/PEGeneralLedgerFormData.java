package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_pe_general_ledger_form_data")
public class PEGeneralLedgerFormData extends CreateUpdateBaseEntity{

    private Integer tranche;
    private FinancialStatementCategory financialStatementCategory;
    private TarragonNICChartOfAccounts tarragonNICChartOfAccounts;
    private Double GLAccountBalance;
    private PeriodicReport report;
    private String entityName;

    public Integer getTranche() {
        return tranche;
    }

    public void setTranche(Integer tranche) {
        this.tranche = tranche;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fin_statmnt_type_id"/*, nullable = false*/)
    public FinancialStatementCategory getFinancialStatementCategory() {
        return financialStatementCategory;
    }

    public void setFinancialStatementCategory(FinancialStatementCategory financialStatementCategory) {
        this.financialStatementCategory = financialStatementCategory;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tarragon_nic_chart_accounts_id"/*, nullable = false*/)
    public TarragonNICChartOfAccounts getTarragonNICChartOfAccounts() {
        return tarragonNICChartOfAccounts;
    }

    public void setTarragonNICChartOfAccounts(TarragonNICChartOfAccounts tarragonNICChartOfAccounts) {
        this.tarragonNICChartOfAccounts = tarragonNICChartOfAccounts;
    }

    public Double getGLAccountBalance() {
        return GLAccountBalance;
    }

    public void setGLAccountBalance(Double GLAccountBalance) {
        this.GLAccountBalance = GLAccountBalance;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }


    @Column(name="entity_name")
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
