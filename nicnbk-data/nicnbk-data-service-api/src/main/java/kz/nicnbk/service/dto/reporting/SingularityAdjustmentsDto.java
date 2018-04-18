package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 20.10.2017.
 */
public class SingularityAdjustmentsDto implements BaseDto {

    private Long reportId;
    private List<SingularityFundAdjustmentDto> adjustedRedemptions;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public List<SingularityFundAdjustmentDto> getAdjustedRedemptions() {
        return adjustedRedemptions;
    }

    public void setAdjustedRedemptions(List<SingularityFundAdjustmentDto> adjustedRedemptions) {
        this.adjustedRedemptions = adjustedRedemptions;
    }
}
