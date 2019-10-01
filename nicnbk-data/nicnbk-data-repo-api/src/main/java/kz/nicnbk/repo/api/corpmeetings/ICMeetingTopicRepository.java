package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by magzumov.
 */
public interface ICMeetingTopicRepository extends PagingAndSortingRepository<ICMeetingTopic, Long> {




    // TODO: search by test with no date params --> does not show entities with date not specified


    @Query("select DISTINCT e from ICMeetingTopic e LEFT JOIN e.icMeeting ic LEFT JOIN e.tags tags where " +
            " (e.icMeeting.id is null OR (e.icMeeting.date >= :dateFrom AND e.icMeeting.date <= :dateTo)) " +
            " and (:searchText='' OR (LOWER(e.shortName) LIKE CONCAT('%', :searchText, '%')) " +
            " or (LOWER(e.longDescription) LIKE CONCAT('%', :searchText,'%')) " +
            " or (LOWER(e.decision) LIKE CONCAT('%', :searchText,'%')) " +
            " or (LOWER(tags.name) LIKE CONCAT('%', :searchText,'%'))) " +
            " and (:type is null OR  e.type.code=:type)" +
            " and (:icNumber='' OR  e.icMeeting.number=:icNumber )" +
            " and ((:types) is null OR e.type.code IN (:types))" +
            " and (e.deleted is null OR e.deleted=false)" )
    Page<ICMeetingTopic> search(@Param("dateFrom") @Temporal(TemporalType.DATE)  Date dateFrom,
                                @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo,
                                @Param("searchText") String searchText, @Param("icNumber")String icNumber,
                                @Param("type") String type, Pageable pageable,  @Param("types") Set<String> types);


    @Query("select e FROM ICMeetingTopic e where ((:types) is null OR e.type.code IN (:types)) AND (e.deleted is null OR e.deleted=false)")
    Page<ICMeetingTopic> searchAll(Pageable pageable, @Param("types") Set<String> types);

    @Query("select DISTINCT e from ICMeetingTopic e where e.icMeeting.number=?1 AND " +
            " (e.icMeeting.deleted is null OR e.icMeeting.deleted=false) AND (e.deleted is null OR e.deleted=false)")
    List<ICMeetingTopic> findByICNumberAndNotDeleted(String number);
}
