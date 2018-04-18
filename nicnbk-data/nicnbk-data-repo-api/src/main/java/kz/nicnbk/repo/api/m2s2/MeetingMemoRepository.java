package kz.nicnbk.repo.api.m2s2;

import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by magzumov on 05.07.2016.
 */
public interface MeetingMemoRepository extends PagingAndSortingRepository<MeetingMemo, Long> {

    @Query("select memo from MeetingMemo memo where (memo.deleted is null or memo.deleted = FALSE) " +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findAllByOrderByMeetingDateDesc(Pageable pageable);

    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and (UPPER(memo.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '')" +
            " and (memo.creator.username=:username or :username is null or :username = '')" +
            " and (memo.deleted is null or memo.deleted = FALSE)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findWithoutDates(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                       @Param("firmName")String firmName, @Param("fundName")String fundName, @Param("username")String username, Pageable pageable);

    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and (UPPER(memo.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom) and (memo.meetingDate <= :dateTo)" +
            " and (memo.creator.username=:username or :username is null or :username = '')" +
            " and (memo.deleted is null or memo.deleted = FALSE)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findBothDates(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                    @Param("firmName")String firmName,
                                    @Param("fundName")String fundName, @Param("dateFrom")@Temporal(TemporalType.DATE) Date dateFrom,
                                    @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, @Param("username")String username, Pageable pageable);

    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and (UPPER(memo.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom)" +
            " and (memo.creator.username=:username or :username is null or :username = '')" +
            " and (memo.deleted is null or memo.deleted = FALSE)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findDateFrom(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                   @Param("firmName")String firmName,
                                   @Param("fundName")String fundName, @Temporal(TemporalType.DATE) @Param("dateFrom") Date dateFrom, @Param("username")String username, Pageable pageable);


    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and (UPPER(memo.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate <= :dateTo)" +
            " and (memo.creator.username=:username or :username is null or :username = '')" +
            " and (memo.deleted is null or memo.deleted = FALSE)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findDateTo(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                 @Param("firmName")String firmName,
                                 @Param("fundName")String fundName,
                                 @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, @Param("username")String username, Pageable pageable);

}
