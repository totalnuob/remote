package kz.nicnbk.service.api.benchmark;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 05.01.2017.
 */
public interface BenchmarkService extends BaseService {

    List<BenchmarkValueDto> getValuesFromDateAsList(Date fromDate, String benchmarkCode);

    double[] getReturnValuesFromDateAsArray(Date date, String benchmarkCode);

    double[] getReturnValuesBetweenDatesAsArray(Date dateFrom, Date dateTo, String benchmarkCode);

}
