package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingAttendeeAbsenceType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov
 */
public interface ICMeetingAttendeeAbsenceTypeRepository extends PagingAndSortingRepository<ICMeetingAttendeeAbsenceType, Long> {

    ICMeetingAttendeeAbsenceType findByCode(String code);

}
