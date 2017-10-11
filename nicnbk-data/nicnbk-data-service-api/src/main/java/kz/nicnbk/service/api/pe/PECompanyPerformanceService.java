package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PECompanyPerformanceDto;

import java.util.List;

/**
 * Created by Pak on 10.10.2017.
 */
public interface PECompanyPerformanceService {

    Long save(PECompanyPerformanceDto performanceDto, Long fundId);

    String saveList(List<PECompanyPerformanceDto> performanceDtoList, Long fundId);

    List<PECompanyPerformanceDto> getEntityDtosByFundId(Long fundId);

    boolean deleteByFundId(Long fundId);
}
