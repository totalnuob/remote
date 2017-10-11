package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceDto;
import kz.nicnbk.service.dto.pe.PEFundDto;
import kz.nicnbk.service.dto.pe.PEFundTrackRecordResultDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;

import java.util.List;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PEFundService extends BaseService {

    PEFundDto get(Long id);

    Long save(PEFundDto fundDto, String username);

    StatusResultDto savePerformance(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    PEFundTrackRecordResultDto savePerformanceAndRecalculateStatistics(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    StatusResultDto saveGrossCF(List<PEGrossCashflowDto> grossCashflowDtoList, Long fundId, String username);

    List<PEFundDto> loadFirmFunds(Long firmId, boolean report);
}