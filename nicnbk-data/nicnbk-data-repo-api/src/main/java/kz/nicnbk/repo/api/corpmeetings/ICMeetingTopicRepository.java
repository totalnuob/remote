package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by magzumov.
 */
public interface ICMeetingTopicRepository extends PagingAndSortingRepository<ICMeetingTopic, Long> {




    // TODO: search by test with no date params --> does not show entities with date not specified


    @Query("select e from ICMeetingTopic e LEFT JOIN e.icMeeting ic where " +
            " (e.icMeeting is null OR (e.icMeeting.date >= ?1 AND e.icMeeting.date <= ?2)) " +
            " and (?3 is null OR UPPER(e.shortName) LIKE UPPER(CONCAT('%', ?3 ,'%')) " +
            " or UPPER(e.longDescription) LIKE UPPER(CONCAT('%', ?3 ,'%'))) " +
            " and e.deleted is null OR e.deleted=false" )
    Page<ICMeetingTopic> search(@Temporal(TemporalType.DATE)  Date dateFrom,
                                @Temporal(TemporalType.DATE) Date dateTo,
                                String searchText, Pageable pageable);

    @Query("select e from ICMeetingTopic e where e.deleted is null OR e.deleted=false")
    Page<ICMeetingTopic> searchAll(Pageable pageable);
}
