package kz.nicnbk.service.api.pe;

import kz.nicnbk.repo.model.pe.PEFundCompaniesPerformance;

import java.util.List;

/**
 * Created by Pak on 10.10.2017.
 */
public interface PEFundCompaniesPerformanceService {

    Long save(PEFundCompaniesPerformance performance);

    boolean deleteByFundId(Long fundId);

    List<PEFundCompaniesPerformance> getEntitiesByFundId(Long id);
}
