package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.util.StringUtils;

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

    public boolean isEmpty(){
        return StringUtils.isEmpty(super.getAcronym()) && StringUtils.isEmpty(super.getFinancialStatementCategory())
                && StringUtils.isEmpty(super.getFinancialStatementCategoryDescription())
                && StringUtils.isEmpty(super.getChartAccountsDescription())
                &&  StringUtils.isEmpty(super.getChartAccountsLongDescription())
                && StringUtils.isEmpty(super.getShortName())
                && (super.getGLAccountBalance() == null || super.getGLAccountBalance().doubleValue() == 0.0);
    }
}
