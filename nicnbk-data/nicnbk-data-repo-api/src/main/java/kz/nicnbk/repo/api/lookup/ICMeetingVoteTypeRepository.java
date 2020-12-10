package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingVoteType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov
 */
public interface ICMeetingVoteTypeRepository extends PagingAndSortingRepository<ICMeetingVoteType, Integer> {

    ICMeetingVoteType findByCode(String code);

}
