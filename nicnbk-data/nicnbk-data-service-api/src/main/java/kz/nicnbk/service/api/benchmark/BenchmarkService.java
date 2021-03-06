package kz.nicnbk.service.api.benchmark;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.lookup.BenchmarkPagedSearchResult;
import kz.nicnbk.service.dto.lookup.BenchmarkSearchParams;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 05.01.2017.
 */
public interface BenchmarkService extends BaseService {

    //List<BenchmarkValueDto> getValuesFromDateAsList(Date fromDate, String benchmarkCode);

    //double[] getMonthReturnValuesFromDateAsArray(Date date, String benchmarkCode);

    double[] getMonthReturnValuesBetweenDatesAsArray(Date dateFrom, Date dateTo, String benchmarkCode);


    BenchmarkPagedSearchResult search(BenchmarkSearchParams params);

    EntitySaveResponseDto save(BenchmarkValueDto dto, String username);

    EntityListSaveResponseDto save(List<BenchmarkValueDto> dtoList, boolean stopOnError, String username);

    EntityListSaveResponseDto downloadBenchmarksBB(BenchmarkSearchParams searchParams, String username);

    List<BenchmarkValueDto> getBenchmarkValuesForDatesAndType(Date dateFrom, Date dateTo, String benchmarkCode);

    List<BenchmarkValueDto> getBenchmarkValuesEndOfMonthForDateRangeAndTypes(Date dateFrom, Date dateTo, int daysRange, String[] benchmarkCodes);
}
