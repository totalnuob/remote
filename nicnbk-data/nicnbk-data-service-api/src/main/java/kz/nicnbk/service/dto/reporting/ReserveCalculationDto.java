package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by magzumov on 30.11.2017.
 */
public class ReserveCalculationDto implements BaseDto {

    private Long id;
    private BaseDictionaryDto expenseType;
    private BaseDictionaryDto source;
    private BaseDictionaryDto recipient;
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date valueDate;
    private Double amount;
    private Double currencyRate;
    private Double amountKZT;
    private boolean canDelete;

    public BaseDictionaryDto getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(BaseDictionaryDto expenseType) {
        this.expenseType = expenseType;
    }

    public BaseDictionaryDto getSource() {
        return source;
    }

    public void setSource(BaseDictionaryDto source) {
        this.source = source;
    }

    public BaseDictionaryDto getRecipient() {
        return recipient;
    }

    public void setRecipient(BaseDictionaryDto recipient) {
        this.recipient = recipient;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(Double currencyRate) {
        this.currencyRate = currencyRate;
    }

    public Double getAmountKZT() {
        return amountKZT;
    }

    public void setAmountKZT(Double amountKZT) {
        this.amountKZT = amountKZT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }
}
