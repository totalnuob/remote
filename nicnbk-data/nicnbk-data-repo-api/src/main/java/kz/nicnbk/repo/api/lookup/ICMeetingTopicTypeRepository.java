package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface ICMeetingTopicTypeRepository extends PagingAndSortingRepository<ICMeetingTopicType, Long> {

    ICMeetingTopicType findByCode(String code);

}