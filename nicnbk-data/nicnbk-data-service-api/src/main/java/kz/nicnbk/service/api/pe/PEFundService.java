package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.*;

import java.util.List;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PEFundService extends BaseService {

    PEFundDto get(Long fundId);

    Long save(PEFundDto fundDto, String username);

    PECompanyPerformanceResultDto savePerformance(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    PEFundTrackRecordResultDto calculateTrackRecord(Long fundId);

    PEFundTrackRecordResultDto recalculateStatistics(Long fundId);

    PECompanyPerformanceAndFundTrackRecordResultDto savePerformanceAndRecalculateStatistics(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    PEGrossCashflowResultDto saveGrossCF(List<PEGrossCashflowDto> grossCashflowDtoList, Long fundId, String username);

    PEGrossCashflowAndCompanyPerformanceIddResultDto saveGrossCFAndRecalculatePerformanceIdd(List<PEGrossCashflowDto> grossCashflowDtoList, Long fundId, String username);

    List<PEFundDto> loadFirmFunds(Long firmId, boolean report);
}