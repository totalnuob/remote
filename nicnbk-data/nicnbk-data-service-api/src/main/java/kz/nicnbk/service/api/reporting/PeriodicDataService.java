package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.PeriodicDataDto;

import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */
public interface PeriodicDataService extends BaseService{

    PeriodicDataDto get(Date date, String type);

    PeriodicDataDto getCashflowBeginningPeriod(Date date, int tranche);

    PeriodicDataDto getCashflowBeginningPeriodForYear(int year, int tranche);
}
