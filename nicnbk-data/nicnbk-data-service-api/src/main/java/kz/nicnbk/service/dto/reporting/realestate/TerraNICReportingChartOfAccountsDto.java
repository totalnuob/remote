package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.reporting.NICReportingChartOfAccountsDto;

/**
 * Created by magzumov on 28.09.2017.
 */
public class TerraNICReportingChartOfAccountsDto implements BaseDto {

    private String terraChartOfAccountsName;
    private NICReportingChartOfAccountsDto NICChartOfAccounts;

    public String getTerraChartOfAccountsName() {
        return terraChartOfAccountsName;
    }

    public void setTerraChartOfAccountsName(String terraChartOfAccountsName) {
        this.terraChartOfAccountsName = terraChartOfAccountsName;
    }

    public NICReportingChartOfAccountsDto getNICChartOfAccounts() {
        return NICChartOfAccounts;
    }

    public void setNICChartOfAccounts(NICReportingChartOfAccountsDto NICChartOfAccounts) {
        this.NICChartOfAccounts = NICChartOfAccounts;
    }
}
