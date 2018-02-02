package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 28.09.2017.
 */
public class TarragonNICReportingChartOfAccountsDto implements BaseDto {

    private String tarragonChartOfAccountsName;
    private NICReportingChartOfAccountsDto NICChartOfAccounts;

    public String getTarragonChartOfAccountsName() {
        return tarragonChartOfAccountsName;
    }

    public void setTarragonChartOfAccountsName(String tarragonChartOfAccountsName) {
        this.tarragonChartOfAccountsName = tarragonChartOfAccountsName;
    }

    public NICReportingChartOfAccountsDto getNICChartOfAccounts() {
        return NICChartOfAccounts;
    }

    public void setNICChartOfAccounts(NICReportingChartOfAccountsDto NICChartOfAccounts) {
        this.NICChartOfAccounts = NICChartOfAccounts;
    }
}
