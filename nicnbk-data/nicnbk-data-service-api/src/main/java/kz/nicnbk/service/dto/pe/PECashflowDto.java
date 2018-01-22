package kz.nicnbk.service.dto.pe;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Pak on 23.10.2017.
 */
public class PECashflowDto {

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private Double cashflow;

    public PECashflowDto() {
    }

    public PECashflowDto(Date date, Double cashflow) {
        this.date = date;
        this.cashflow = cashflow;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getCashflow() {
        return cashflow;
    }

    public void setCashflow(Double cashflow) {
        this.cashflow = cashflow;
    }
}
