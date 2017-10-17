package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddDto;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddResultDto;

import java.util.List;

/**
 * Created by Pak on 17.10.2017.
 */
public interface PECompanyPerformanceIddService {

    Long save(PECompanyPerformanceIddDto performanceIddDto, Long fundId);

    PECompanyPerformanceIddResultDto saveList(List<PECompanyPerformanceIddDto> performanceIddDtoList, Long fundId);

    PECompanyPerformanceIddResultDto recalculatePerformance(Long fundId);

    List<PECompanyPerformanceIddDto> findByFundId(Long fundId);

    boolean deleteByFundId(Long fundId);
}
