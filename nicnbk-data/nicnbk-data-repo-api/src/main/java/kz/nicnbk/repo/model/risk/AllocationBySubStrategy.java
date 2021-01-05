package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "monitoring_risk_allocation_by_sub_strategy")
public class AllocationBySubStrategy extends BaseEntity {

    private Employee updater;

    private Files file;

    private String subStrategyName;

    @Column(nullable = false)
    private Date firstDate;

    @Column(nullable =  false)
    private Date lastDate;

    private Double currentValue;

    private Double previousValue;

    public AllocationBySubStrategy() {
    }

    public AllocationBySubStrategy(Employee updater, Files file, String subStrategyName, Date firstDate, Date lastDate,
                                   Double currentValue, Double previousValue) {
        this.updater = updater;
        this.file = file;
        this.subStrategyName = subStrategyName;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
        this.currentValue = currentValue;
        this.previousValue = previousValue;
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

    public Date getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(Date currentDate) {
        this.firstDate = currentDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date previousDate) {
        this.lastDate = previousDate;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public Double getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(Double previousValue) {
        this.previousValue = previousValue;
    }

    public String getSubStrategyName() {
        return subStrategyName;
    }

    public void setSubStrategyName(String subStrategyName) {
        this.subStrategyName = subStrategyName;
    }
}
