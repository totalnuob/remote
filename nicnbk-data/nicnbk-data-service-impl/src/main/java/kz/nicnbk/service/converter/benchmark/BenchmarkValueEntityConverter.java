package kz.nicnbk.service.converter.benchmark;

import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 05.01.2017.
 */

@Component
public class BenchmarkValueEntityConverter extends BaseDozerEntityConverter<BenchmarkValue, BenchmarkValueDto> {
}
