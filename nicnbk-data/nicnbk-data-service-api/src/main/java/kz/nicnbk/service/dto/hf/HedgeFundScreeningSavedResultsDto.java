package kz.nicnbk.service.dto.hf;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;


public class HedgeFundScreeningSavedResultsDto extends CreateUpdateBaseEntityDto {

    private HedgeFundScreeningFilteredResultDto filteredResult;

    private int selectedLookbackReturn;
    private int selectedLookbackAUM;

    private boolean archived;

    public HedgeFundScreeningFilteredResultDto getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(HedgeFundScreeningFilteredResultDto filteredResult) {
        this.filteredResult = filteredResult;
    }

    public int getSelectedLookbackReturn() {
        return selectedLookbackReturn;
    }

    public void setSelectedLookbackReturn(int selectedLookbackReturn) {
        this.selectedLookbackReturn = selectedLookbackReturn;
    }

    public int getSelectedLookbackAUM() {
        return selectedLookbackAUM;
    }

    public void setSelectedLookbackAUM(int selectedLookbackAUM) {
        this.selectedLookbackAUM = selectedLookbackAUM;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
