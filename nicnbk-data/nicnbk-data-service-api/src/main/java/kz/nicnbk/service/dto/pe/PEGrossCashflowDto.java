package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public class PEGrossCashflowDto extends BaseEntityDto<PEGrossCashflow> implements Comparable<PEGrossCashflowDto>{
    private String companyName;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private double invested;
    private double realized;
    private double unrealized;
    private double grossCF;
    private double irr;
    private PEFundDto fund;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public double getGrossCF() {
        return grossCF;
    }

    public void setGrossCF(double grossCF) {
        this.grossCF = grossCF;
    }

    public double getIrr() {
        return irr;
    }

    public void setIrr(double irr) {
        this.irr = irr;
    }

    public PEFundDto getFund() {
        return fund;
    }

    public void setFund(PEFundDto fund) {
        this.fund = fund;
    }

    @Override
    public int compareTo(PEGrossCashflowDto dto) {
        return this.date.compareTo(dto.getDate());
    }
}
