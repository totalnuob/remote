package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.reporting.PeriodicDataDto;
import kz.nicnbk.service.dto.reporting.PeriodicDataPagedSearchResultDto;
import kz.nicnbk.service.dto.reporting.PeriodicDataSearchParamsDto;

import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */
public interface PeriodicDataService extends BaseService{

    PeriodicDataDto get(Date date, String type);

    PeriodicDataDto getCashflowBeginningPeriod(Date date, int tranche);

    PeriodicDataDto getCashflowBeginningPeriodForYear(int year, int tranche);

    PeriodicDataPagedSearchResultDto searchPeriodicData(PeriodicDataSearchParamsDto searchParams);

    EntitySaveResponseDto save(PeriodicDataDto dto, String username);

    boolean delete(Long id, String username);
}
