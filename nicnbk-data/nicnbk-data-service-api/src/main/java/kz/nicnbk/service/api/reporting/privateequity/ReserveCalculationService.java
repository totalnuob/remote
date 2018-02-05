package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ReserveCalculationDto;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReserveCalculationService extends BaseService {

    List<ReserveCalculationDto> getReserveCalculations();

    List<ReserveCalculationDto> getReserveCalculationsForMonth(String code, Date date);

    Double getReserveCalculationSumForMonth(String code, Date date);

    boolean save(List<ReserveCalculationDto> records);

    List<ReserveCalculationDto> getReserveCalculationsByExpenseType(String code);
}