package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class NICChartOfAccountsPagedSearchResultDto extends PageableResult {

    private List<NICReportingChartOfAccountsDto> chartOfAccounts;
    private String searchParams;

    public List<NICReportingChartOfAccountsDto> getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(List<NICReportingChartOfAccountsDto> chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public void add(NICReportingChartOfAccountsDto dto){
        if(this.chartOfAccounts == null){
            this.chartOfAccounts = new ArrayList<>();
        }
        this.chartOfAccounts.add(dto);
    }
}
