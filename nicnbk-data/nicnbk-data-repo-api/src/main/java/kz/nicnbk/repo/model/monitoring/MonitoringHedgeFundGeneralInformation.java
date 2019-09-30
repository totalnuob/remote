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
@Table(name="monitoring_hf_general_info")
public class MonitoringHedgeFundGeneralInformation extends CreatorBaseEntity implements TypedEntity<MonitoringHedgeFundClassType> {

    private MonitoringHedgeFundReportDate reportDate;
    private MonitoringHedgeFundClassType type;
    private String name;
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitoring_hf_report_date_id", nullable = false)
    public MonitoringHedgeFundReportDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(MonitoringHedgeFundReportDate reportDate) {
        this.reportDate = reportDate;
    }

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    public MonitoringHedgeFundClassType getType() {
        return type;
    }

    public void setType(MonitoringHedgeFundClassType type) {
        this.type = type;
    }

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

//    @Column(name="monitoring_date", nullable = false)
//    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern="dd-MM-yyyy")
//    public Date getMonitoringDate() {
//        return monitoringDate;
//    }
//
//    public void setMonitoringDate(Date monitoringDate) {
//        this.monitoringDate = monitoringDate;
//    }
}
