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

    Page<MeetingMemo> findAllByOrderByMeetingDateDesc(Pageable pageable);

    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null) " +
            " and (UPPER(memo.otherPartyName) LIKE UPPER(CONCAT('%',:otherPartyName,'%')) or :otherPartyName is null or :otherPartyName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '')" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findWithoutDates(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                @Param("otherPartyName")String otherPartyName, @Param("fundName")String fundName, Pageable pageable);

    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null) " +
            " and (UPPER(memo.otherPartyName) LIKE UPPER(CONCAT('%',:otherPartyName,'%')) or :otherPartyName is null or :otherPartyName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom) and (memo.meetingDate <= :dateTo)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findBothDates(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                    @Param("otherPartyName")String otherPartyName,
                                    @Param("fundName")String fundName, @Param("dateFrom")@Temporal(TemporalType.DATE) Date dateFrom,
                                    @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, Pageable pageable);

    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null) " +
            " and (UPPER(memo.otherPartyName) LIKE UPPER(CONCAT('%',:otherPartyName,'%')) or :otherPartyName is null or :otherPartyName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findDateFrom(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                   @Param("otherPartyName")String otherPartyName,
                                   @Param("fundName")String fundName, @Temporal(TemporalType.DATE) @Param("dateFrom") Date dateFrom, Pageable pageable);


    @Query("select memo from MeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null) " +
            " and (UPPER(memo.otherPartyName) LIKE UPPER(CONCAT('%',:otherPartyName,'%')) or :otherPartyName is null or :otherPartyName = '') " +
            " and (UPPER(memo.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate <= :dateTo)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findDateTo(@Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                 @Param("otherPartyName")String otherPartyName,
                                 @Param("fundName")String fundName,
                                 @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, Pageable pageable);
}
