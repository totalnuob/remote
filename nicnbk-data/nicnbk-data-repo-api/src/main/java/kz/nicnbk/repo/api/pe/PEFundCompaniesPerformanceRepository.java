package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEFundCompaniesPerformance;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Pak on 03.10.2017.
 */
public interface PEFundCompaniesPerformanceRepository extends PagingAndSortingRepository<PEFundCompaniesPerformance, Long> {
    List<PEFundCompaniesPerformance> getEntitiesByFundId(Long id);
}
