package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.PageableResult;
import kz.nicnbk.service.dto.monitoring.RiskStressTestsDto;
import kz.nicnbk.service.dto.risk.PortfolioVarValueDto;

import java.util.ArrayList;
import java.util.List;

public class RiskStressTestPagedSearchResult extends PageableResult {
    List<RiskStressTestsDto> stressTests;
    private String searchParams;

    public List<RiskStressTestsDto> getStressTests() {
        return stressTests;
    }

    public void setStressTests(List<RiskStressTestsDto> stressTests) {
        this.stressTests = stressTests;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public void add(RiskStressTestsDto stressTest){
        if(this.stressTests == null){
            this.stressTests = new ArrayList<>();
        }
        this.stressTests.add(stressTest);
    }
}
