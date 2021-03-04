package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicDecisionType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov
 */
public interface ICMeetingTopicDecisionTypeRepository extends PagingAndSortingRepository<ICMeetingTopicDecisionType, Integer> {

    ICMeetingTopicDecisionType findByCode(String code);

}
