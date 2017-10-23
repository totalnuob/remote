package kz.nicnbk.service.dto.pe;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Pak on 23.10.2017.
 */
public class PECashflowDto {

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private Double cashFlow;

    public PECashflowDto() {
    }

    public PECashflowDto(Date date, Double cashFlow) {
        this.date = date;
        this.cashFlow = cashFlow;
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
