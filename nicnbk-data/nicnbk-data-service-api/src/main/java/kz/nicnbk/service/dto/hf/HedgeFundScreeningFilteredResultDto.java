package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFilteredResultDto extends HedgeFundScreeningParamsDto {

    private HedgeFundScreeningFilteredResultStatisticsDto filteredResultStatistics;
    private List<HedgeFundScreeningParsedDataDto> addedFunds;
    private List<HedgeFundScreeningParsedDataDto> excludedFunds;


    public HedgeFundScreeningFilteredResultDto(){}

    public HedgeFundScreeningFilteredResultDto(HedgeFundScreeningFilteredResultDto other){
        super(other);
    }

    public HedgeFundScreeningFilteredResultDto(Long id){
        setId(id);
    }


    public HedgeFundScreeningFilteredResultStatisticsDto getFilteredResultStatistics() {
        return filteredResultStatistics;
    }

    public void setFilteredResultStatistics(HedgeFundScreeningFilteredResultStatisticsDto filteredResultStatistics) {
        this.filteredResultStatistics = filteredResultStatistics;
    }

    public List<HedgeFundScreeningParsedDataDto> getAddedFunds() {
        return addedFunds;
    }

    public void setAddedFunds(List<HedgeFundScreeningParsedDataDto> addedFunds) {
        this.addedFunds = addedFunds;
    }

    public List<HedgeFundScreeningParsedDataDto> getExcludedFunds() {
        return excludedFunds;
    }

    public void setExcludedFunds(List<HedgeFundScreeningParsedDataDto> excludedFunds) {
        this.excludedFunds = excludedFunds;
    }
}


