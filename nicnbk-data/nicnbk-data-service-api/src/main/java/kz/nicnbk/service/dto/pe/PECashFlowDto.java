package kz.nicnbk.service.dto.pe;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Pak on 23.10.2017.
 */
public class PECashFlowDto {

    private String companyName;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private Double cashFlow;

    public PECashFlowDto() {
    }

    public PECashFlowDto(String companyName, Date date, Double cashFlow) {
        this.companyName = companyName;
        this.date = date;
        this.cashFlow = cashFlow;
    }

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

    public Double getCashFlow() {
        return cashFlow;
    }

    public void setCashFlow(Double cashFlow) {
        this.cashFlow = cashFlow;
    }
}
