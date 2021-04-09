package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.benchmark.Benchmark;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResults;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultsBenchmark;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultsCurrency;
import kz.nicnbk.repo.model.markers.Lookup;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningSavedResultBenchmarkDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningSavedResultCurrencyDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HedgeFundScreeningSavedResultsBenchmarkEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningSavedResultsBenchmark, HedgeFundScreeningSavedResultBenchmarkDto> {

    @Autowired
    private LookupService lookupService;

    public HedgeFundScreeningSavedResultsBenchmark assemble(BenchmarkValueDto benchmarkValueDto, Long savedResultId){
        HedgeFundScreeningSavedResultsBenchmark entity = new HedgeFundScreeningSavedResultsBenchmark();
        entity.setSavedResults(new HedgeFundScreeningSavedResults(savedResultId));
        Benchmark benchmark = this.lookupService.findByTypeAndCode(Benchmark.class, benchmarkValueDto.getBenchmark().getCode());
        entity.setBenchmark(benchmark);
        entity.setDate(benchmarkValueDto.getDate());
        //entity.setReturnValue(benchmarkValueDto.getReturnValue());
        entity.setIndexValue(benchmarkValueDto.getIndexValue());
        entity.setYtd(benchmarkValueDto.getYtd());
        return  entity;
    }

    public List<HedgeFundScreeningSavedResultsBenchmark> assembleListFromBenchmarkValues(List<BenchmarkValueDto> benchmarkValuesList, Long savedResultId){
        List<HedgeFundScreeningSavedResultsBenchmark> entities = new ArrayList<>();
        if(benchmarkValuesList != null){
            for(BenchmarkValueDto benchmarkDto: benchmarkValuesList){
                entities.add(assemble(benchmarkDto, savedResultId));
            }
        }
        return entities;
    }
}
