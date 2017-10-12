package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.pe.*;

import java.util.List;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PEFundService extends BaseService {

    PEFundDto get(Long id);

    Long save(PEFundDto fundDto, String username);

    PECompanyPerformanceResultDto savePerformance(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    PEFundTrackRecordResultDto recalculateStatistics(Long fundId);

    PEFundTrackRecordResultDto savePerformanceAndRecalculateStatistics(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    StatusResultDto saveGrossCF(List<PEGrossCashflowDto> grossCashflowDtoList, Long fundId, String username);

    List<PEFundDto> loadFirmFunds(Long firmId, boolean report);
}