package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.common.Geography;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface GeographyRepository extends PagingAndSortingRepository<Geography, Long> {
    Geography findByCode(String code);
}
