package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 15.12.2016.
 */
@Entity(name="hf_returns")
public class HedgeFundReturn extends BaseEntity {

    //private Date period;
    //private Double value;

    private int year;
    private Double january;
    private Double february;
    private Double march;
    private Double april;
    private Double may;
    private Double june;
    private Double july;
    private Double august;
    private Double september;
    private Double october;
    private Double november;
    private Double december;
    private HedgeFund fund;

//    @Column(name="period")
//    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern="dd-MM-yyyy")
//    public Date getPeriod() {
//        return period;
//    }
//
//    public void setPeriod(Date period) {
//        this.period = period;
//    }


    @Column(name="year", nullable = false)
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getJanuary() {
        return january;
    }

    public void setJanuary(Double january) {
        this.january = january;
    }

    public Double getFebruary() {
        return february;
    }

    public void setFebruary(Double february) {
        this.february = february;
    }

    public Double getMarch() {
        return march;
    }

    public void setMarch(Double march) {
        this.march = march;
    }

    public Double getApril() {
        return april;
    }

    public void setApril(Double april) {
        this.april = april;
    }

    public Double getMay() {
        return may;
    }

    public void setMay(Double may) {
        this.may = may;
    }

    public Double getJune() {
        return june;
    }

    public void setJune(Double june) {
        this.june = june;
    }

    public Double getJuly() {
        return july;
    }

    public void setJuly(Double july) {
        this.july = july;
    }

    public Double getAugust() {
        return august;
    }

    public void setAugust(Double august) {
        this.august = august;
    }

    public Double getSeptember() {
        return september;
    }

    public void setSeptember(Double september) {
        this.september = september;
    }

    public Double getOctober() {
        return october;
    }

    public void setOctober(Double october) {
        this.october = october;
    }

    public Double getNovember() {
        return november;
    }

    public void setNovember(Double november) {
        this.november = november;
    }

    public Double getDecember() {
        return december;
    }

    public void setDecember(Double december) {
        this.december = december;
    }

//    @Column(name="value")
//    public Double getValue() {
//        return value;
//    }
//
//    public void setValue(Double value) {
//        this.value = value;
//    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fund_id", nullable = false)
    public HedgeFund getFund() {
        return fund;
    }

    public void setFund(HedgeFund fund) {
        this.fund = fund;
    }
}
