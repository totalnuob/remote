package kz.nicnbk.service.dto.pe;

import java.util.Date;

/**
 * Created by Pak on 06.10.2017.
 */
public class PEFundTrackRecordDto {

    //KEY FUND STATISTICS
    private Integer numberOfInvestments;
    private Double investedAmount;
    private Double realized;
    private Double unrealized;
    private Double dpi;
    private Double netIrr;
    private Double netTvpi; // MOIC
    private Double grossIrr;
    private Double grossTvpi;
    private Date asOfDate;
    private Double benchmarkNetIrr;
    private Double benchmarkNetTvpi;
    private String benchmarkName;

    public PEFundTrackRecordDto() {
    }

    public PEFundTrackRecordDto(Integer numberOfInvestments, Double investedAmount, Double realized, Double unrealized, Double dpi, Double netIrr, Double netTvpi, Double grossIrr, Double grossTvpi, Date asOfDate, Double benchmarkNetIrr, Double benchmarkNetTvpi, String benchmarkName) {
        this.numberOfInvestments = numberOfInvestments;
        this.investedAmount = investedAmount;
        this.realized = realized;
        this.unrealized = unrealized;
        this.dpi = dpi;
        this.netIrr = netIrr;
        this.netTvpi = netTvpi;
        this.grossIrr = grossIrr;
        this.grossTvpi = grossTvpi;
        this.asOfDate = asOfDate;
        this.benchmarkNetIrr = benchmarkNetIrr;
        this.benchmarkNetTvpi = benchmarkNetTvpi;
        this.benchmarkName = benchmarkName;
    }

    public Integer getNumberOfInvestments() {
        return numberOfInvestments;
    }

    public void setNumberOfInvestments(Integer numberOfInvestments) {
        this.numberOfInvestments = numberOfInvestments;
    }

    public Double getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(Double investedAmount) {
        this.investedAmount = investedAmount;
    }

    public Double getRealized() {
        return realized;
    }

    public void setRealized(Double realized) {
        this.realized = realized;
    }

    public Double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(Double unrealized) {
        this.unrealized = unrealized;
    }

    public Double getDpi() {
        return dpi;
    }

    public void setDpi(Double dpi) {
        this.dpi = dpi;
    }

    public Double getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(Double netIrr) {
        this.netIrr = netIrr;
    }

    public Double getNetTvpi() {
        return netTvpi;
    }

    public void setNetTvpi(Double netTvpi) {
        this.netTvpi = netTvpi;
    }

    public Double getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(Double grossIrr) {
        this.grossIrr = grossIrr;
    }

    public Double getGrossTvpi() {
        return grossTvpi;
    }

    public void setGrossTvpi(Double grossTvpi) {
        this.grossTvpi = grossTvpi;
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }

    public Double getBenchmarkNetIrr() {
        return benchmarkNetIrr;
    }

    public void setBenchmarkNetIrr(Double benchmarkNetIrr) {
        this.benchmarkNetIrr = benchmarkNetIrr;
    }

    public Double getBenchmarkNetTvpi() {
        return benchmarkNetTvpi;
    }

    public void setBenchmarkNetTvpi(Double benchmarkNetTvpi) {
        this.benchmarkNetTvpi = benchmarkNetTvpi;
    }

    public String getBenchmarkName() {
        return benchmarkName;
    }

    public void setBenchmarkName(String benchmarkName) {
        this.benchmarkName = benchmarkName;
    }
}
