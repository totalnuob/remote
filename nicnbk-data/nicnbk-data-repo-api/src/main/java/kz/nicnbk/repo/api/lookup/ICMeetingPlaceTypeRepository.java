package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingPlaceType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov
 */
public interface ICMeetingPlaceTypeRepository extends PagingAndSortingRepository<ICMeetingPlaceType, Integer> {

    ICMeetingPlaceType findByCode(String code);

}
