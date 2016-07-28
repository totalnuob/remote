package kz.nicnbk.repo.api.m2s2;

import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by magzumov on 05.07.2016.
 */
public interface PEMeetingMemoRepository extends PagingAndSortingRepository<PrivateEquityMeetingMemo, Long> {

    @Query("SELECT m FROM PrivateEquityMeetingMemo m " +
            " LEFT JOIN FETCH m.arrangedBy LEFT JOIN FETCH m.attendeesNIC LEFT JOIN FETCH m.fundSizeCurrency " +
            " LEFT JOIN FETCH m.geographies LEFT JOIN FETCH m.strategies " +
            //" LEFT JOIN FETCH m.memoType " +
            " LEFT JOIN FETCH m.meetingType " +
            " WHERE m.id = :id")
    PrivateEquityMeetingMemo getFullEagerById(@Param("id") Long id);


}
