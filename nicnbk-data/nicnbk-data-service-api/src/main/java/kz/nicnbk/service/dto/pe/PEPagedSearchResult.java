package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by zhambyl on 17-Jan-17.
 */
public class PEPagedSearchResult extends PageableResult {

    private List<PEFirmDto> firms;
    private String searchParams;

    public List<PEFirmDto> getFirms() {
        return firms;
    }

    public void setFirms(List<PEFirmDto> firms) {
        this.firms = firms;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
