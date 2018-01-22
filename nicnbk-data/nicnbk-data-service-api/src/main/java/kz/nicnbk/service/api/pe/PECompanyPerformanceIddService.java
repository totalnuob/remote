package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddDto;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddResultDto;
import kz.nicnbk.service.dto.pe.PEFundTrackRecordResultDto;

import java.util.List;

/**
 * Created by Pak on 17.10.2017.
 */
public interface PECompanyPerformanceIddService {

    Long save(PECompanyPerformanceIddDto performanceIddDto, Long fundId);

    PECompanyPerformanceIddResultDto saveList(List<PECompanyPerformanceIddDto> performanceIddDtoList, Long fundId);

    PECompanyPerformanceIddResultDto recalculatePerformanceIdd(Long fundId);

    List<PECompanyPerformanceIddDto> findByFundId(Long fundId);

    PECompanyPerformanceIddDto findByFundIdAndCompanyName(Long fundId, String companyName);

//    boolean deleteByFundId(Long fundId);

    PEFundTrackRecordResultDto calculateTrackRecord(Long fundId);
}
