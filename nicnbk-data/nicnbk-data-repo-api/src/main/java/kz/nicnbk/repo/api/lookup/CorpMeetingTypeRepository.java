package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.corpmeetings.CorpMeetingType;
import kz.nicnbk.repo.model.tripmemo.TripType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface CorpMeetingTypeRepository extends PagingAndSortingRepository<CorpMeetingType, Integer> {

    CorpMeetingType findByCode(String code);

}