package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.ReportStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface ReportStatusRepository extends PagingAndSortingRepository<ReportStatus, Long> {

    ReportStatus findByCode(String code);

}