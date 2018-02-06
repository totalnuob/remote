package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.repo.api.reporting.PeriodicDataRepository;
import kz.nicnbk.repo.model.reporting.PeriodicData;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.dto.reporting.PeriodicDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */
@Service
public class PeriodicDataServiceImpl implements PeriodicDataService {

    // TODO: refactor
    public static final String CASHFLOW_BEGINNING_PERIOD_TRANCHE_A = "CASH_BGN_A";
    public static final String CASHFLOW_BEGINNING_PERIOD_TRANCHE_B = "CASH_BGN_B";

    @Autowired
    private PeriodicDataRepository periodicDataRepository;

    @Override
    public PeriodicDataDto get(Date date, String type) {
        PeriodicData entity = this.periodicDataRepository.getByDateAndType(date, type);
        if(entity != null){
            PeriodicDataDto dto = new PeriodicDataDto(entity.getDate(), entity.getType().getCode(), entity.getValue());
            return dto;
        }
        return null;
    }

    @Override
    public PeriodicDataDto getCashflowBeginningPeriod(Date date, int tranche) {
        if(tranche == 1){
            return get(date, CASHFLOW_BEGINNING_PERIOD_TRANCHE_A);
        }else {
            return get(date, CASHFLOW_BEGINNING_PERIOD_TRANCHE_B);
        }
    }

    @Override
    public PeriodicDataDto getCashflowBeginningPeriodForYear(int year, int tranche) {
        Date date = DateUtils.getFirstDayYear(year);
        return getCashflowBeginningPeriod(date, tranche);
    }
}
