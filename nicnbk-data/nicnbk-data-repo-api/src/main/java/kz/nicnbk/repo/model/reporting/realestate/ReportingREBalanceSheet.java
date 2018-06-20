package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_re_balance_sheet")
public class ReportingREBalanceSheet extends CreateUpdateBaseEntity{

    private String name;
    private Double valueGP;
    private Double valueNICKMF;
    private Double grandTotal;
    private REBalanceType type;
    private Boolean isTotalSum;

    private PeriodicReport report;

    private Boolean excludeFromTerraCalculation;


    @Column(name = "name", length = DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "value_gp")
    public Double getValueGP() {
        return valueGP;
    }

    public void setValueGP(Double valueGP) {
        this.valueGP = valueGP;
    }

    @Column(name = "value_nickmf")
    public Double getValueNICKMF() {
        return valueNICKMF;
    }

    public void setValueNICKMF(Double valueNICKMF) {
        this.valueNICKMF = valueNICKMF;
    }

    @Column(name = "value_grand_total")
    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id"/*, nullable = false*/)
    public REBalanceType getType() {
        return type;
    }

    public void setType(REBalanceType type) {
        this.type = type;
    }

    @Column(name = "is_total_sum")
    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @Column(name="exclude_from_terra_calc")
    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }
}
