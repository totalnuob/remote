package kz.nicnbk.service.converter.benchmark;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.repo.model.benchmark.Benchmark;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 05.01.2017.
 */

@Component
public class BenchmarkValueEntityConverter extends BaseDozerEntityConverter<BenchmarkValue, BenchmarkValueDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public BenchmarkValue assemble(BenchmarkValueDto dto){
        BenchmarkValue entity = super.assemble(dto);
        if (dto.getBenchmark() != null && dto.getBenchmark().getCode() != null) {
            Benchmark currency = this.lookupService.findByTypeAndCode(Benchmark.class, dto.getBenchmark().getCode());
            entity.setBenchmark(currency);
        }
        if(dto.getDate() == null || DateUtils.getMonth(dto.getDate()) != 11 || DateUtils.getDay(dto.getDate()) != 31 ){
            entity.setYtd(null);
        }
        return entity;
    }

    @Override
    public BenchmarkValueDto disassemble(BenchmarkValue entity){
        BenchmarkValueDto dto = super.disassemble(entity);
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }
        if(entity.getUpdater() != null){
            dto.setUpdater(entity.getUpdater().getUsername());
        }
        return dto;
    }
}
