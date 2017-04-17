package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.tripmemo.TripType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface TripTypeRepository extends PagingAndSortingRepository<TripType, Long> {

    TripType findByCode(String code);

}