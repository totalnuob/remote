package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.files.Files;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "monitoring_risk_hf_report")
public class MonitoringRiskHFMonthlyReport extends CreateUpdateBaseEntity {

    private Date reportDate;
    private Files returnsClassAFile;
    private Files returnsClassBFile;
    private Files returnsConsFile;
    private Files allocationsConsFile;


    @Column(name="report_date", nullable = false, unique = true)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="return_classa_file_id")
    public Files getReturnsClassAFile() {
        return returnsClassAFile;
    }

    public void setReturnsClassAFile(Files returnsClassAFile) {
        this.returnsClassAFile = returnsClassAFile;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="return_classb_file_id")
    public Files getReturnsClassBFile() {
        return returnsClassBFile;
    }

    public void setReturnsClassBFile(Files returnsClassBFile) {
        this.returnsClassBFile = returnsClassBFile;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="return_cons_file_id")
    public Files getReturnsConsFile() {
        return returnsConsFile;
    }

    public void setReturnsConsFile(Files returnsConsFile) {
        this.returnsConsFile = returnsConsFile;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="allocations_cons_file_id")
    public Files getAllocationsConsFile() {
        return allocationsConsFile;
    }

    public void setAllocationsConsFile(Files allocationsConsFile) {
        this.allocationsConsFile = allocationsConsFile;
    }
}
