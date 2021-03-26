package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "monitoring_risk_hf_report")
public class MonitoringRiskHFMonthlyReport extends CreateUpdateBaseEntity {

    private Date reportDate;

    @Column(name="report_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
