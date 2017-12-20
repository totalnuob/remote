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

    private Double invested;
    private Double realized;
    private Double unrealized;
    private Double grossCF;
    private Boolean autoCalculation;
//    private Double irr;
//    private PEFundDto fund;

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

    public Double getGrossCF() {
        return grossCF;
    }

    public void setGrossCF(Double grossCF) {
        this.grossCF = grossCF;
    }

    public Boolean getAutoCalculation() {
        return autoCalculation;
    }

    public void setAutoCalculation(Boolean autoCalculation) {
        this.autoCalculation = autoCalculation;
    }

    //    public Double getIrr() {
//        return irr;
//    }

//    public void setIrr(Double irr) {
//        this.irr = irr;
//    }

//    public PEFundDto getFund() {
//        return fund;
//    }

//    public void setFund(PEFundDto fund) {
//        this.fund = fund;
//    }

    @Override
    public int compareTo(PEGrossCashflowDto dto) {
        return this.date.compareTo(dto.getDate());
    }
}
