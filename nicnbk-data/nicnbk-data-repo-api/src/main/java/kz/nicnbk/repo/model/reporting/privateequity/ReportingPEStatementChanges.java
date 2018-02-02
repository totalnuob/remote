package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_pe_s_changes")
public class ReportingPEStatementChanges extends CreateUpdateBaseEntity{

    private String name;

    private PeriodicReport report;

    private Double trancheA;
    private Double trancheB;
    private Double total;


    @Column(name = "name", length = DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @Column(name = "tranche_a")
    public Double getTrancheA() {
        return trancheA;
    }

    public void setTrancheA(Double trancheA) {
        this.trancheA = trancheA;
    }

    @Column(name = "tranche_b")
    public Double getTrancheB() {
        return trancheB;
    }

    public void setTrancheB(Double trancheB) {
        this.trancheB = trancheB;
    }

    @Column(name = "total")
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
