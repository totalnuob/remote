package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PEFundCompaniesPerformanceDto;

import java.util.List;

/**
 * Created by Pak on 10.10.2017.
 */
public interface PEFundCompaniesPerformanceService {

    Long save(PEFundCompaniesPerformanceDto performanceDto, Long fundId);

    String saveList(List<PEFundCompaniesPerformanceDto> performanceDtoList, Long fundId);

    List<PEFundCompaniesPerformanceDto> getEntityDtosByFundId(Long fundId);

    boolean deleteByFundId(Long fundId);
}
