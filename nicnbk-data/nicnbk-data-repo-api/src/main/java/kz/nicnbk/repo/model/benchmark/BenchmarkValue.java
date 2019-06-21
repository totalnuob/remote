package kz.nicnbk.repo.model.benchmark;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 05.01.2017.
 */

@Entity
@Table(name = "benchmark_value")
public class BenchmarkValue extends CreateUpdateBaseEntity {
    private Benchmark benchmark;
    private Date date;
    private Double returnValue;
    private Double indexValue;

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

    @Column (name = "index_value", nullable = false)
    public Double getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Double indexValue) {
        this.indexValue = indexValue;
    }
}
