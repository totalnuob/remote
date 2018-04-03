package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 20.10.2017.
 */
public class SingularityFundAdjustmentDto implements BaseDto {

    private Long recordId; // Singularity General Ledger record id
    private Double adjustedRedemption;
    private String interestRate;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Double getAdjustedRedemption() {
        return adjustedRedemption;
    }

    public void setAdjustedRedemption(Double adjustedRedemption) {
        this.adjustedRedemption = adjustedRedemption;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }
}
