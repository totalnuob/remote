package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.PageableResult;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class PeriodicDataPagedSearchResultDto extends PageableResult {

    private List<PeriodicDataDto> periodicData;
    private String searchParams;

    public List<PeriodicDataDto> getPeriodicData() {
        return periodicData;
    }

    public void setPeriodicData(List<PeriodicDataDto> periodicData) {
        this.periodicData = periodicData;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public void add(PeriodicDataDto ratesDto){
        if(this.periodicData == null){
            this.periodicData = new ArrayList<>();
        }
        this.periodicData.add(ratesDto);
    }
}
