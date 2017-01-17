package kz.nicnbk.service.dto.pe;

/**
 * Created by zhambyl on 09-Jan-17.
 */
public class PEFundCompaniesPerformanceDto {
    private String companyName;
    private double invested;
    private double realized;
    private double unrealized;
    private double totalValue;
    private double multiple;
    private double grossIrr;
    private double netIrr;

    public PEFundCompaniesPerformanceDto(String test, double i, double i1, double i2, double i3, double i4, double i5, double i6) {
        companyName = test;
        invested = i;
        realized = i1;
        unrealized = i2;
        totalValue = i3;
        multiple = i4;
        grossIrr = i5;
        netIrr = i6;
    }

    public PEFundCompaniesPerformanceDto(){

    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getInvested() {
        return invested;
    }

    public void setInvested(double invested) {
        this.invested = invested;
    }

    public double getRealized() {
        return realized;
    }

    public void setRealized(double realized) {
        this.realized = realized;
    }

    public double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(double unrealized) {
        this.unrealized = unrealized;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getMultiple() {
        return multiple;
    }

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }

    public double getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(double grossIrr) {
        this.grossIrr = grossIrr;
    }

    public double getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(double netIrr) {
        this.netIrr = netIrr;
    }
}

