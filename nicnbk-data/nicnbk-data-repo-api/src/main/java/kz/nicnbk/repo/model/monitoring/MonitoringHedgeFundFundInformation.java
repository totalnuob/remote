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
@Table(name="monitoring_hf_fund_info")
public class MonitoringHedgeFundFundInformation extends CreatorBaseEntity implements TypedEntity<MonitoringHedgeFundClassType> {

    private MonitoringHedgeFundReportDate reportDate;
    //private Date monitoringDate;
    private MonitoringHedgeFundClassType type;

    private MonitoringHedgeFundInfoType fundInfoType;
    private String fundName;
    private String strategy;
    private Double ytd;
    private Double allocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_date_id", nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_info_type_id", nullable = false)
    public MonitoringHedgeFundInfoType getFundInfoType() {
        return fundInfoType;
    }

    public void setFundInfoType(MonitoringHedgeFundInfoType fundInfoType) {
        this.fundInfoType = fundInfoType;
    }

    @Column(name="fund_name")
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name="strategy")
    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Column(name="ytd")
    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    @Column(name="allocation")
    public Double getAllocation() {
        return allocation;
    }

    public void setAllocation(Double allocation) {
        this.allocation = allocation;
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
