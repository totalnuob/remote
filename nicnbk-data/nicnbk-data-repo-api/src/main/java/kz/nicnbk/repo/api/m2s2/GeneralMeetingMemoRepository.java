package kz.nicnbk.repo.api.m2s2;

import kz.nicnbk.repo.model.m2s2.GeneralMeetingMemo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by magzumov on 05.07.2016.
 */
public interface GeneralMeetingMemoRepository extends PagingAndSortingRepository<GeneralMeetingMemo, Long> {

    @Query("SELECT m FROM GeneralMeetingMemo m " +
            " LEFT JOIN FETCH m.arrangedBy LEFT JOIN FETCH m.attendeesNIC " +
            //" LEFT JOIN FETCH m.memoType " +
            " LEFT JOIN FETCH m.meetingType " +
            " WHERE m.id = :id")
    GeneralMeetingMemo getFullEagerById(@Param("id") Long id);


}
