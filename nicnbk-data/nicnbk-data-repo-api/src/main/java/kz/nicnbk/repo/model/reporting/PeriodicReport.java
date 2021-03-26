package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "periodic_report")
public class PeriodicReport extends CreateUpdateBaseEntity{

    private PeriodicReportType type;
    private Date reportDate;
    private ReportStatus status;

    public PeriodicReport(){}

    public PeriodicReport(Long id){
        setId(id);
    }

    @Column(name="report_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_type_id", nullable = false)
    public PeriodicReportType getType() {
        return type;
    }

    public void setType(PeriodicReportType type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

}
