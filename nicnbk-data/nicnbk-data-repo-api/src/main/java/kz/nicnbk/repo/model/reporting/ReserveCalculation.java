package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_reserve_calculation")
public class ReserveCalculation extends CreateUpdateBaseEntity{

    private ReserveCalculationExpenseType expenseType;
    private ReserveCalculationEntityType source;
    private ReserveCalculationEntityType recipient;
    private Date date;
    private Date valueDate;
    private Double amount;
    private Double amountToSPV;
    private String referenceInfo;
    private Boolean excludeFromTerraCalculation;
    private Boolean excludeOppositeFromTerraCalculation;

    private Boolean excludeFromTarragonCalculation;
    private Boolean excludeOppositeFromTarragonCalculation;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_type_id", nullable = false)
    public ReserveCalculationExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ReserveCalculationExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_type_id", nullable = false)
    public ReserveCalculationEntityType getSource() {
        return source;
    }

    public void setSource(ReserveCalculationEntityType source) {
        this.source = source;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_type_id", nullable = false)
    public ReserveCalculationEntityType getRecipient() {
        return recipient;
    }

    public void setRecipient(ReserveCalculationEntityType recipient) {
        this.recipient = recipient;
    }

    @Column(name="date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name="amount", nullable = false)
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="amount_to_spv")
    public Double getAmountToSPV() {
        return amountToSPV;
    }

    public void setAmountToSPV(Double amountToSPV) {
        this.amountToSPV = amountToSPV;
    }

    @Column(name="value_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    @Column(name="reference_info")
    public String getReferenceInfo() {
        return referenceInfo;
    }

    public void setReferenceInfo(String referenceInfo) {
        this.referenceInfo = referenceInfo;
    }

    @Column(name="exclude_from_terra_calc")
    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }

    @Column(name="exclude_opposite_from_terra_calc")
    public Boolean getExcludeOppositeFromTerraCalculation() {
        return excludeOppositeFromTerraCalculation;
    }

    public void setExcludeOppositeFromTerraCalculation(Boolean excludeOppositeFromTerraCalculation) {
        this.excludeOppositeFromTerraCalculation = excludeOppositeFromTerraCalculation;
    }

    @Column(name="exclude_from_tarragon_calc")
    public Boolean getExcludeFromTarragonCalculation() {
        return excludeFromTarragonCalculation;
    }

    public void setExcludeFromTarragonCalculation(Boolean excludeFromTarragonCalculation) {
        this.excludeFromTarragonCalculation = excludeFromTarragonCalculation;
    }

    @Column(name="exclude_opposite_from_tarragon_calc")
    public Boolean getExcludeOppositeFromTarragonCalculation() {
        return excludeOppositeFromTarragonCalculation;
    }

    public void setExcludeOppositeFromTarragonCalculation(Boolean excludeOppositeFromTarragonCalculation) {
        this.excludeOppositeFromTarragonCalculation = excludeOppositeFromTarragonCalculation;
    }
}
