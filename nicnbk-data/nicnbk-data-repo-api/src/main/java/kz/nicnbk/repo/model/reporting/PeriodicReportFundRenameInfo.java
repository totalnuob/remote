package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "periodic_report_fund_rename")
public class PeriodicReportFundRenameInfo extends CreateUpdateBaseEntity{

    private PeriodicReport report;
    private String currentFundName;
    private String previousFundName;

    private Boolean usePreviousFundName;

    private String type;

    public PeriodicReportFundRenameInfo(){}

    public PeriodicReportFundRenameInfo(Long reportId, String currentFundName, String previousFundName, String type, Boolean usePreviousFundName){
        if(this.report == null){
            this.report = new PeriodicReport();
        }
        this.report.setId(reportId);
        this.currentFundName = currentFundName;
        this.previousFundName = previousFundName;
        this.type = type;
        this.usePreviousFundName = usePreviousFundName;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }


    @Column(name="current_fund_name")
    public String getCurrentFundName() {
        return currentFundName;
    }

    public void setCurrentFundName(String currentFundName) {
        this.currentFundName = currentFundName;
    }

    @Column(name="prev_fund_name")
    public String getPreviousFundName() {
        return previousFundName;
    }

    public void setPreviousFundName(String previousFundName) {
        this.previousFundName = previousFundName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name="use_prev_fund_name")
    public Boolean isUsePreviousFundName() {
        return usePreviousFundName;
    }

    public void setUsePreviousFundName(Boolean usePreviousFundName) {
        this.usePreviousFundName = usePreviousFundName;
    }
}
