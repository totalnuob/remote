package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity(name = "monitoring_risk_allocation_by_top_portfolio")
public class AllocationByTopPortfolio extends BaseEntity {

    private Employee updater;
    private Files file;
    private Date date;
    private String fundName;
    private String className;
    private Double navPercent;
    private Double mtd;
    private Double qtd;
    private Double ytd;
    private Double contributionToYTD;
    private Double contributionToVAR;

    public AllocationByTopPortfolio() {
    }

    public AllocationByTopPortfolio(Employee updater, Files file, Date date, String fundName, String className, Double navPercent,
                                    Double mtd, Double qtd, Double ytd, Double contributionToYTD, Double contributionToVAR) {
        this.updater = updater;
        this.file = file;
        this.date = date;
        this.fundName = fundName;
        this.className = className;
        this.navPercent = navPercent;
        this.mtd = mtd;
        this.qtd = qtd;
        this.ytd = ytd;
        this.contributionToYTD = contributionToYTD;
        this.contributionToVAR = contributionToVAR;

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy")
    public Employee getUpdater() {
        return updater;
    }

    public void setUpdater(Employee updater) {
        this.updater = updater;
    }

    @ManyToOne
    @JoinColumn(name = "file_id")
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getNavPercent() {
        return navPercent;
    }

    public void setNavPercent(Double navPercent) {
        this.navPercent = navPercent;
    }

    public Double getMtd() {
        return mtd;
    }

    public void setMtd(Double mtd) {
        this.mtd = mtd;
    }

    public Double getQtd() {
        return qtd;
    }

    public void setQtd(Double qtd) {
        this.qtd = qtd;
    }

    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    public Double getContributionToYTD() {
        return contributionToYTD;
    }

    public void setContributionToYTD(Double contributionToYTD) {
        this.contributionToYTD = contributionToYTD;
    }

    public Double getContributionToVAR() {
        return contributionToVAR;
    }

    public void setContributionToVAR(Double contributionToVAR) {
        this.contributionToVAR = contributionToVAR;
    }
}
