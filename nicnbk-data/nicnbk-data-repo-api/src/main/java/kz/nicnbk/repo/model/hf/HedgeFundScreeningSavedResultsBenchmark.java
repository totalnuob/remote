package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.benchmark.Benchmark;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.repo.model.common.Currency;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_saved_results_benchmark")
public class HedgeFundScreeningSavedResultsBenchmark extends BaseEntity {

    private HedgeFundScreeningSavedResults savedResults;

    private Benchmark benchmark;
    private Date date;
    private Double returnValue;
    private Double indexValue;
    private Double calculatedMonthReturn;
    private Double ytd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benchmark_id", nullable = false)
    public Benchmark getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    @Column(name="asof_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column (name = "return_value", nullable = false)
    public Double getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Double returnValue) {
        this.returnValue = returnValue;
    }

    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="saved_result_id", nullable = false)
    public HedgeFundScreeningSavedResults getSavedResults() {
        return savedResults;
    }

    public void setSavedResults(HedgeFundScreeningSavedResults savedResults) {
        this.savedResults = savedResults;
    }

    @Column (name = "index_value" /*, nullable = false*/)
    public Double getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Double indexValue) {
        this.indexValue = indexValue;
    }

    @Column (name = "calc_month_return_value" /*, nullable = false*/)
    public Double getCalculatedMonthReturn() {
        return calculatedMonthReturn;
    }

    public void setCalculatedMonthReturn(Double calculatedMonthReturn) {
        this.calculatedMonthReturn = calculatedMonthReturn;
    }
}
