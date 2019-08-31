package kz.nicnbk.repo.api.hr;

import kz.nicnbk.repo.model.hr.HRNews;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface HRNewsRepository extends PagingAndSortingRepository<HRNews, Long> {
}
