package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhambyl on 22-Sep-16.
 */
@Entity
@Table(name = "benchmark")
public class Benchmark extends CreateUpdateBaseEntity{

    private String name;
    private int vintage;
    private Date asOfDate;
    private float pctComplete;
    private int fundCount;
    private float median;
    private float pooledReturn;
    private float upperQuartile;
    private float lowerQuartile;
    private float dpi;
    private float tvpi;

    @Column(name = "benchmark_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "vintage")
    public int getVintage() {
        return vintage;
    }

    public void setVintage(int vintage) {
        this.vintage = vintage;
    }

    @Column(name = "as_of_date")
    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }

    @Column(name = "pct_complete")
    public float getPctComplete() {
        return pctComplete;
    }

    public void setPctComplete(float pctComplete) {
        this.pctComplete = pctComplete;
    }

    @Column(name = "fund_count")
    public int getFundCount() {
        return fundCount;
    }

    public void setFundCount(int fundCount) {
        this.fundCount = fundCount;
    }

    @Column(name = "median")
    public float getMedian() {
        return median;
    }

    public void setMedian(float median) {
        this.median = median;
    }

    @Column(name = "pooled_return")
    public float getPooledReturn() {
        return pooledReturn;
    }

    public void setPooledReturn(float pooledReturn) {
        this.pooledReturn = pooledReturn;
    }

    @Column(name = "upper_quartile")
    public float getUpperQuartile() {
        return upperQuartile;
    }

    public void setUpperQuartile(float upperQuartile) {
        this.upperQuartile = upperQuartile;
    }

    @Column(name = "lower_quartile")
    public float getLowerQuartile() {
        return lowerQuartile;
    }

    public void setLowerQuartile(float lowerQuartile) {
        this.lowerQuartile = lowerQuartile;
    }

    @Column(name = "dpi")
    public float getDpi() {
        return dpi;
    }

    public void setDpi(float dpi) {
        this.dpi = dpi;
    }

    @Column(name = "tvpi")
    public float getTvpi() {
        return tvpi;
    }

    public void setTvpi(float tvpi) {
        this.tvpi = tvpi;
    }
}

