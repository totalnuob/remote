package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ICCorpMeetingTypeRepository extends PagingAndSortingRepository<ICMeetingType, Long> {

    ICMeetingType findByCode(String code);
}
