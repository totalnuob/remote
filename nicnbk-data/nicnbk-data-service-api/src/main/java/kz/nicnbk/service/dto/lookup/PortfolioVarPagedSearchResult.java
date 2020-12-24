package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.PageableResult;
import kz.nicnbk.service.dto.risk.PortfolioVarValueDto;

import java.util.ArrayList;
import java.util.List;

public class PortfolioVarPagedSearchResult extends PageableResult {
    List<PortfolioVarValueDto> portfolioVars;
    private String searchParams;

    public List<PortfolioVarValueDto> getPortfolioVars() {
        return portfolioVars;
    }

    public void setPortfolioVars(List<PortfolioVarValueDto> portfolioVars) {
        this.portfolioVars = portfolioVars;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public void add(PortfolioVarValueDto benchmark){
        if(this.portfolioVars == null){
            this.portfolioVars = new ArrayList<>();
        }
        this.portfolioVars.add(benchmark);
    }
}
