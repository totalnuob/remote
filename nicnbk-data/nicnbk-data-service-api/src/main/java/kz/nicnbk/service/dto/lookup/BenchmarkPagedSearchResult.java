package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.PageableResult;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class BenchmarkPagedSearchResult extends PageableResult {

    private List<BenchmarkValueDto> benchmarks;
    private String searchParams;

    public List<BenchmarkValueDto> getBenchmarks() {
        return benchmarks;
    }

    public void setBenchmarks(List<BenchmarkValueDto> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public void add(BenchmarkValueDto benchmark){
        if(this.benchmarks == null){
            this.benchmarks = new ArrayList<>();
        }
        this.benchmarks.add(benchmark);
    }
}
