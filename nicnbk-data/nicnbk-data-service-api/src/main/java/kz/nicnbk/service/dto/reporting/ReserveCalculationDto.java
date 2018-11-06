package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * Created by magzumov on 30.11.2017.
 */
public class ReserveCalculationDto extends CreateUpdateBaseEntityDto<ReserveCalculation> {

    private Long id;
    private BaseDictionaryDto expenseType;
    private BaseDictionaryDto source;
    private BaseDictionaryDto recipient;
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date valueDate;
    private Double amount;
    private Double amountToSPV;
    private Double currencyRate;
    private Double amountKZT;
    private boolean canDelete;
    private String referenceInfo;
    private Set<FilesDto> files;
    private Boolean excludeFromTerraCalculation;
    private Boolean excludeOppositeFromTerraCalculation;

    private Boolean excludeFromTarragonCalculation;
    private Boolean excludeOppositeFromTarragonCalculation;

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

    public Double getAmountToSPV() {
        return amountToSPV;
    }

    public void setAmountToSPV(Double amountToSPV) {
        this.amountToSPV = amountToSPV;
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

    public String getReferenceInfo() {
        return referenceInfo;
    }

    public void setReferenceInfo(String referenceInfo) {
        this.referenceInfo = referenceInfo;
    }

    public Set<FilesDto> getFiles() {
        return files;
    }

    public void setFiles(Set<FilesDto> files) {
        this.files = files;
    }

    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }

    public Boolean getExcludeOppositeFromTerraCalculation() {
        return excludeOppositeFromTerraCalculation;
    }

    public void setExcludeOppositeFromTerraCalculation(Boolean excludeOppositeFromTerraCalculation) {
        this.excludeOppositeFromTerraCalculation = excludeOppositeFromTerraCalculation;
    }

    public Boolean getExcludeFromTarragonCalculation() {
        return excludeFromTarragonCalculation;
    }

    public void setExcludeFromTarragonCalculation(Boolean excludeFromTarragonCalculation) {
        this.excludeFromTarragonCalculation = excludeFromTarragonCalculation;
    }

    public Boolean getExcludeOppositeFromTarragonCalculation() {
        return excludeOppositeFromTarragonCalculation;
    }

    public void setExcludeOppositeFromTarragonCalculation(Boolean excludeOppositeFromTarragonCalculation) {
        this.excludeOppositeFromTarragonCalculation = excludeOppositeFromTarragonCalculation;
    }
}
