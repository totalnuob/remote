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
    private List<HedgeFundScreeningParsedDataDto> toCheckFunds;
    private List<HedgeFundScreeningParsedDataDto> addedFunds;
    private List<HedgeFundScreeningParsedDataDto> editedFunds;
    private List<HedgeFundScreeningParsedDataDto> excludedFunds;
    private List<HedgeFundScreeningParsedDataDto> autoExcludedFunds;

    private HedgeFundScreeningDto screening;

    private boolean editable;

    private String description;

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

    public List<HedgeFundScreeningParsedDataDto> getToCheckFunds() {
        return toCheckFunds;
    }

    public void setToCheckFunds(List<HedgeFundScreeningParsedDataDto> toCheckFunds) {
        this.toCheckFunds = toCheckFunds;
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

    public List<HedgeFundScreeningParsedDataDto> getAutoExcludedFunds() {
        return autoExcludedFunds;
    }

    public void setAutoExcludedFunds(List<HedgeFundScreeningParsedDataDto> autoExcludedFunds) {
        this.autoExcludedFunds = autoExcludedFunds;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public HedgeFundScreeningDto getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreeningDto screening) {
        this.screening = screening;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<HedgeFundScreeningParsedDataDto> getEditedFunds() {
        return editedFunds;
    }

    public void setEditedFunds(List<HedgeFundScreeningParsedDataDto> editedFunds) {
        this.editedFunds = editedFunds;
    }
}


