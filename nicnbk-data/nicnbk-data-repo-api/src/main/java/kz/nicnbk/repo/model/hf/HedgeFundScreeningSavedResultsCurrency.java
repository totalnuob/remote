package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.CurrencyRates;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_saved_results_currency")
public class HedgeFundScreeningSavedResultsCurrency extends BaseEntity {

    private HedgeFundScreeningSavedResults savedResults;

    private Currency currency;
    private Date date;
    private Double value; // KZT value
    private Double averageValue;
    private Double averageValueYear;
    private Double valueUSD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    @Column(nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name="value")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Column(name="avg_value")
    public Double getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(Double averageValue) {
        this.averageValue = averageValue;
    }

    @Column(name="avg_value_year")
    public Double getAverageValueYear() {
        return averageValueYear;
    }

    public void setAverageValueYear(Double averageValueYear) {
        this.averageValueYear = averageValueYear;
    }

    @Column(name="value_usd")
    public Double getValueUSD() {
        return valueUSD;
    }

    public void setValueUSD(Double valueUSD) {
        this.valueUSD = valueUSD;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="saved_result_id", nullable = false)
    public HedgeFundScreeningSavedResults getSavedResults() {
        return savedResults;
    }

    public void setSavedResults(HedgeFundScreeningSavedResults savedResults) {
        this.savedResults = savedResults;
    }

}
