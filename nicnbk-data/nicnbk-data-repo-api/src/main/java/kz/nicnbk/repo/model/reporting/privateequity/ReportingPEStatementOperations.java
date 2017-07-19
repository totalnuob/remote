package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_pe_s_operations")
public class ReportingPEStatementOperations extends CreateUpdateBaseEntity{

    private String name;
    private Double tarragonMFTotal;
    private Double tarragonMFShareGP;
    private Double tarragonMFShareNICK;
    private Double tarragonLP;
    private Double consolidationAdjustments;

    private PeriodicReport report;

    private PEOperationsType type;
    private Integer tranche;

    @Column(name = "name", length = DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "tarragon_mf_total")
    public Double getTarragonMFTotal() {
        return tarragonMFTotal;
    }

    public void setTarragonMFTotal(Double tarragonMFTotal) {
        this.tarragonMFTotal = tarragonMFTotal;
    }

    @Column(name = "tarragon_mf_gp")
    public Double getTarragonMFShareGP() {
        return tarragonMFShareGP;
    }

    public void setTarragonMFShareGP(Double tarragonMFShareGP) {
        this.tarragonMFShareGP = tarragonMFShareGP;
    }

    @Column(name = "tarragon_mf_nick")
    public Double getTarragonMFShareNICK() {
        return tarragonMFShareNICK;
    }

    public void setTarragonMFShareNICK(Double tarragonMFShareNICK) {
        this.tarragonMFShareNICK = tarragonMFShareNICK;
    }

    @Column(name = "tarragon_lp")
    public Double getTarragonLP() {
        return tarragonLP;
    }

    public void setTarragonLP(Double tarragonLP) {
        this.tarragonLP = tarragonLP;
    }

    @Column(name = "consolidated_adjustment")
    public Double getConsolidationAdjustments() {
        return consolidationAdjustments;
    }

    public void setConsolidationAdjustments(Double consolidationAdjustments) {
        this.consolidationAdjustments = consolidationAdjustments;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operations_type_id"/*, nullable = false*/)
    public PEOperationsType getType() {
        return type;
    }

    public void setType(PEOperationsType type) {
        this.type = type;
    }

    @Column(name = "tranche", nullable = false)
    public Integer getTranche() {
        return tranche;
    }

    public void setTranche(Integer tranche) {
        this.tranche = tranche;
    }
}
