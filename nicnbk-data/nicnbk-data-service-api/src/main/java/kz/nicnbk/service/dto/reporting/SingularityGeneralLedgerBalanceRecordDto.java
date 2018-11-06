package kz.nicnbk.service.dto.reporting;

import java.util.Date;

/**
 * Created by magzumov on 25.08.2017.
 */
public class SingularityGeneralLedgerBalanceRecordDto extends GeneratedGeneralLedgerFormDto {

    private Boolean excludeFromSingularityCalculation;
    private String type;

    public Boolean getExcludeFromSingularityCalculation() {
        return excludeFromSingularityCalculation;
    }

    public void setExcludeFromSingularityCalculation(Boolean excludeFromSingularityCalculation) {
        this.excludeFromSingularityCalculation = excludeFromSingularityCalculation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
