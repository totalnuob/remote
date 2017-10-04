package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.pe.PEFundCompaniesPerformance;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by zhambyl on 09-Jan-17.
 */
public class PEFundCompaniesPerformanceDto extends BaseEntityDto<PEFundCompaniesPerformance> implements Comparable<PEFundCompaniesPerformanceDto> {

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private String companyName;
    private Double invested;
    private Double realized;
    private Double unrealized;
    private Double totalValue;
    private Double multiple;
    private Double grossIrr;
    private PEFundDto fund;

    public PEFundCompaniesPerformanceDto(String test, Double i, Double i1, Double i2, Double i3, Double i4, Double i5) {
        companyName = test;
        invested = i;
        realized = i1;
        unrealized = i2;
        totalValue = i3;
        multiple = i4;
        grossIrr = i5;
    }

    public PEFundCompaniesPerformanceDto(){

    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getInvested() {
        return invested;
    }

    public void setInvested(Double invested) {
        this.invested = invested;
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

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public Double getMultiple() {
        return multiple;
    }

    public void setMultiple(Double multiple) {
        this.multiple = multiple;
    }

    public Double getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(Double grossIrr) {
        this.grossIrr = grossIrr;
    }

    public PEFundDto getFund() {
        return fund;
    }

    public void setFund(PEFundDto fund) {
        this.fund = fund;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(PEFundCompaniesPerformanceDto dto) {
        return this.date.compareTo(dto.getDate());
    }
}