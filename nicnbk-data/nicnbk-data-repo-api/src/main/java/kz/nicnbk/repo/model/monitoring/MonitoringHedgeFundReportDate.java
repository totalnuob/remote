package kz.nicnbk.repo.model.monitoring;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.CreatorBaseEntity;
import kz.nicnbk.repo.model.base.TypedEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="monitoring_hf_report_date")
public class MonitoringHedgeFundReportDate extends CreateUpdateBaseEntity{
    @Basic
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    @Column(name="monitoring_date", nullable = false, unique = true)
    public Date getMonitoringDate() {
        return date;
    }

    public void setMonitoringDate(Date date) {
        this.date = date;
    }
}
