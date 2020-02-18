package kz.nicnbk.service.dto.hf;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.List;


public class HedgeFundScreeningFinalResultsDto extends HedgeFundScreeningSavedResultsDto {

    private List<HedgeFundScreeningSavedResultFundsDto> top50qualifiedFunds;
    private List<HedgeFundScreeningSavedResultFundsDto> qualifiedFunds;
    private List<HedgeFundScreeningSavedResultFundsDto> unqualifiedFunds;
    private List<HedgeFundScreeningSavedResultFundsDto> undecidedFunds;

    private List<HedgeFundScreeningSavedResultFundsDto> editedFunds;

    private List<HedgeFundScreeningSavedResultBenchmarkDto> benchmarks;
    private List<HedgeFundScreeningSavedResultCurrencyDto> currencyRates;

    public List<HedgeFundScreeningSavedResultFundsDto> getQualifiedFunds() {
        return qualifiedFunds;
    }

    public void setQualifiedFunds(List<HedgeFundScreeningSavedResultFundsDto> qualifiedFunds) {
        this.qualifiedFunds = qualifiedFunds;
    }

    public List<HedgeFundScreeningSavedResultFundsDto> getUnqualifiedFunds() {
        return unqualifiedFunds;
    }

    public void setUnqualifiedFunds(List<HedgeFundScreeningSavedResultFundsDto> unqualifiedFunds) {
        this.unqualifiedFunds = unqualifiedFunds;
    }

    public List<HedgeFundScreeningSavedResultFundsDto> getUndecidedFunds() {
        return undecidedFunds;
    }

    public void setUndecidedFunds(List<HedgeFundScreeningSavedResultFundsDto> undecidedFunds) {
        this.undecidedFunds = undecidedFunds;
    }

    public List<HedgeFundScreeningSavedResultBenchmarkDto> getBenchmarks() {
        return benchmarks;
    }

    public void setBenchmarks(List<HedgeFundScreeningSavedResultBenchmarkDto> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public List<HedgeFundScreeningSavedResultCurrencyDto> getCurrencyRates() {
        return currencyRates;
    }

    public void setCurrencyRates(List<HedgeFundScreeningSavedResultCurrencyDto> currencyRates) {
        this.currencyRates = currencyRates;
    }

    public List<HedgeFundScreeningSavedResultFundsDto> getTop50qualifiedFunds() {
        return top50qualifiedFunds;
    }

    public void setTop50qualifiedFunds(List<HedgeFundScreeningSavedResultFundsDto> top50qualifiedFunds) {
        this.top50qualifiedFunds = top50qualifiedFunds;
    }

    public List<HedgeFundScreeningSavedResultFundsDto> getEditedFunds() {
        return editedFunds;
    }

    public void setEditedFunds(List<HedgeFundScreeningSavedResultFundsDto> editedFunds) {
        this.editedFunds = editedFunds;
    }
}
