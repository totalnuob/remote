package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class ReserveCalculationPagedSearchResult extends PageableResult {

    private List<ReserveCalculationDto> records;
    private String searchParams;

    public List<ReserveCalculationDto> getRecords() {
        return records;
    }

    public void setRecords(List<ReserveCalculationDto> records) {
        this.records = records;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
