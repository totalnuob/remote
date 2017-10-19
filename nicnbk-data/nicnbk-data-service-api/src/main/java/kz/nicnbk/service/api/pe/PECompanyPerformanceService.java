package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PECompanyPerformanceDto;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceResultDto;
import kz.nicnbk.service.dto.pe.PEFundTrackRecordResultDto;

import java.util.List;

/**
 * Created by Pak on 10.10.2017.
 */
public interface PECompanyPerformanceService {

    Long save(PECompanyPerformanceDto performanceDto, Long fundId);

    PECompanyPerformanceResultDto saveList(List<PECompanyPerformanceDto> performanceDtoList, Long fundId);

//    PECompanyPerformanceResultDto recalculatePerformance(Long fundId);

    List<PECompanyPerformanceDto> findByFundId(Long fundId);

//    boolean deleteByFundId(Long fundId);

    PEFundTrackRecordResultDto calculateTrackRecord(Long fundId);
}
