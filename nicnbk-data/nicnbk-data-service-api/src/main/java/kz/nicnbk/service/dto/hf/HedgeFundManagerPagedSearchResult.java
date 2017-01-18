package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class HedgeFundManagerPagedSearchResult extends PageableResult {

    private List<HFManagerDto> managers;
    private String searchParams;

    public List<HFManagerDto> getManagers() {
        return managers;
    }

    public void setManagers(List<HFManagerDto> managers) {
        this.managers = managers;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
