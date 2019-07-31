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
@Table(name="monitoring_hf_approved_fund")
public class MonitoringHedgeFundApprovedFund extends CreatorBaseEntity {

    private MonitoringHedgeFundReportDate reportDate;
    //private Date monitoringDate;
    private String fundName;
    private String managerName;
    private String strategy;
    private String protocol;
    private Date approveDate;
    private String limits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_date_id", nullable = false)
    public MonitoringHedgeFundReportDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(MonitoringHedgeFundReportDate reportDate) {
        this.reportDate = reportDate;
    }

    @Column(name="fund_name")
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name="manager_name")
    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    @Column(name="strategy")
    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Column(name="protocol")
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Column(name="approve_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date approveDate) {
        this.approveDate = approveDate;
    }

    public String getLimits() {
        return limits;
    }

    public void setLimits(String limits) {
        this.limits = limits;
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
