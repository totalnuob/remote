package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT DISTINCT e from ICMeetingTopic e LEFT JOIN e.tags tags LEFT JOIN e.icMeeting ic LEFT JOIN e.approveList approve " +
            "LEFT JOIN approve.employee emp LEFT JOIN approve.employee.position pos LEFT JOIN approve.employee.position.department dep where " +
            " (e.icMeeting.id is null OR (e.icMeeting.date >= :dateFrom AND e.icMeeting.date <= :dateTo)) " +
            " AND (:searchText='' OR (LOWER(e.name) LIKE CONCAT('%', :searchText, '%')) " +
            " OR (LOWER(e.nameUpd) LIKE CONCAT('%', :searchText,'%')) " +
            " OR (LOWER(e.description) LIKE CONCAT('%', :searchText,'%')) " +
            " OR (LOWER(e.decision) LIKE CONCAT('%', :searchText,'%')) " +
            " OR (LOWER(e.decisionUpd) LIKE CONCAT('%', :searchText,'%')) " +
            " OR (LOWER(tags.name) LIKE CONCAT('%', :searchText,'%'))" +
            ") " +
            " AND (:icNumber='' OR  e.icMeeting.number=:icNumber)" +
            " AND (:departmentId IS NULL OR e.department.id=:departmentId OR (:isICMember=true AND e.published=true) OR " +
            " (e.published=true AND e.id=approve.icMeetingTopic.id AND approve.employee.position.department.id=:departmentId))" +
            " AND (e.deleted is null OR e.deleted=false)" +
            " ORDER BY e.id ASC")
    Page<ICMeetingTopic> searchNonDeleted(@Param("departmentId") Integer departmentId,
                                          @Param("isICMember") Boolean isICMember,
                                          @Param("dateFrom") @Temporal(TemporalType.DATE)  Date dateFrom,
                                          @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo,
                                          @Param("searchText") String searchText, @Param("icNumber")String icNumber, Pageable pageable);


    @Query("SELECT DISTINCT e FROM ICMeetingTopic e LEFT JOIN e.approveList b " +
            " LEFT JOIN b.employee emp LEFT JOIN b.employee.position pos LEFT JOIN b.employee.position.department dep WHERE " +
            " (:departmentId IS NULL OR e.department.id=:departmentId OR (:viewICTopicAll=true AND e.published=true) OR " +
            " (e.published=true AND e.id=b.icMeetingTopic.id AND b.employee.position.department.id=:departmentId)) " +
            " AND (e.deleted is null OR e.deleted=false) " +
            " ORDER BY e.id ASC")
    Page<ICMeetingTopic> searchAllByDepartmentAndUserNonDeleted(@Param("departmentId") Integer departmentId,
                                                                @Param("viewICTopicAll") Boolean viewICTopicAll,
                                                                Pageable pageable);

//    @Query("select DISTINCT e from ICMeetingTopic e where e.icMeeting.number=?1 AND " +
//            " (e.icMeeting.deleted is null OR e.icMeeting.deleted=false) AND (e.deleted is null OR e.deleted=false)")
//    List<ICMeetingTopic> findByICNumberAndNotDeleted(String number);

    @Query("select DISTINCT e from ICMeetingTopic e LEFT JOIN e.approveList b where e.icMeeting.id=?1 AND " +
            " (?2 IS NULL OR e.department.id=?2 OR (?3=true AND e.published=true) OR " +
            " (e.published=true AND e.id=b.icMeetingTopic.id AND b.employee.position.department.id=?2)) AND " +
            " (e.icMeeting.deleted is null OR e.icMeeting.deleted=false) AND (e.deleted is null OR e.deleted=false)" +
            " ORDER BY e.id ASC")
    List<ICMeetingTopic> findByICMeetingIdAndUserNotDeleted(Long id, Integer departmentId, Boolean viewICTopicAll);

    @Query("SELECT DISTINCT e FROM ICMeetingTopic e WHERE e.icMeeting.id=?1 AND " +
            " (e.icMeeting.deleted is null OR e.icMeeting.deleted=false) AND (e.deleted is null OR e.deleted=false)" +
            " ORDER BY e.id ASC")
    List<ICMeetingTopic> findByICMeetingIdNotDeleted(Long id);

    @Query("select e from ICMeetingTopic e where e.explanatoryNote.id=?1 AND " +
            " (e.icMeeting.deleted is null OR e.icMeeting.deleted=false) AND (e.deleted is null OR e.deleted=false) " +
            " ORDER BY e.id ASC")
    ICMeetingTopic findByExplanatoryNoteIdNotDeleted(Long id);

    @Deprecated
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ICMeetingTopic e SET e.icOrder=?2 where e.id=?1")
    int updateICOrder(Long id, Integer icOrder);
}
