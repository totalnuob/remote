package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_saved_results")
public class HedgeFundScreeningSavedResults extends CreateUpdateBaseEntity {

    private HedgeFundScreeningFilteredResult filteredResult;

    private int selectedLookbackReturn;
    private int selectedLookbackAUM;

    public HedgeFundScreeningSavedResults(){}

    public HedgeFundScreeningSavedResults(Long id){
        setId(id);
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="filtered_result_id", nullable = false)
    public HedgeFundScreeningFilteredResult getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(HedgeFundScreeningFilteredResult filteredResult) {
        this.filteredResult = filteredResult;
    }

    @Column(name="selected_lookback_return")
    public int getSelectedLookbackReturn() {
        return selectedLookbackReturn;
    }

    public void setSelectedLookbackReturn(int selectedLookbackReturn) {
        this.selectedLookbackReturn = selectedLookbackReturn;
    }

    @Column(name="selected_lookback_aum")
    public int getSelectedLookbackAUM() {
        return selectedLookbackAUM;
    }

    public void setSelectedLookbackAUM(int selectedLookbackAUM) {
        this.selectedLookbackAUM = selectedLookbackAUM;
    }
}
