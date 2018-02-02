package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.PeriodicReportType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PeriodicReportTypeRepository extends PagingAndSortingRepository<PeriodicReportType, Long> {

    PeriodicReportType findByCode(String code);

}