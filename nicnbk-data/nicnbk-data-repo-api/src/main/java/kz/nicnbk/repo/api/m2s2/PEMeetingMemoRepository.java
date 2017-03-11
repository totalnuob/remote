package kz.nicnbk.repo.api.m2s2;

import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
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
public interface PEMeetingMemoRepository extends PagingAndSortingRepository<PrivateEquityMeetingMemo, Long> {

    @Query("SELECT m FROM PrivateEquityMeetingMemo m " +
            " LEFT JOIN FETCH m.arrangedBy LEFT JOIN FETCH m.attendeesNIC LEFT JOIN FETCH m.fundSizeCurrency " +
            " LEFT JOIN FETCH m.geographies LEFT JOIN FETCH m.strategies " +
            " LEFT JOIN FETCH m.meetingType " +
            " WHERE m.id = :id")
    PrivateEquityMeetingMemo getFullEagerById(@Param("id") Long id);


    Page<MeetingMemo> findAllByFirmIdOrderByMeetingDateDesc(@Param("id") Long id, Pageable pageable);


    @Query("SELECT memo FROM PrivateEquityMeetingMemo memo " +
            "where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and memo.firm.id=:id " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and (UPPER(memo.firm.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '')" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findWithoutDates(@Param("id") Long id,
                           @Param("meetingType") String meetingType,
                           @Param("memoType") Integer memoType,
                           @Param("firmName")String firmName,
                           @Param("fundName")String fundName,
                           Pageable pageable);


    @Query("select memo from PrivateEquityMeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and memo.firm.id=:id " +
            " and (UPPER(memo.firm.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom) and (memo.meetingDate <= :dateTo)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findBothDates(@Param("id") Long id, @Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                    @Param("firmName")String firmName,
                                    @Param("fundName")String fundName, @Param("dateFrom")@Temporal(TemporalType.DATE) Date dateFrom,
                                    @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, Pageable pageable);
//
    @Query("select memo from PrivateEquityMeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and memo.firm.id=:id " +
            " and (UPPER(memo.firm.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findDateFrom(@Param("id") Long id, @Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                   @Param("firmName")String firmName,
                                   @Param("fundName")String fundName, @Temporal(TemporalType.DATE) @Param("dateFrom") Date dateFrom, Pageable pageable);

//
    @Query("select memo from PrivateEquityMeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and memo.firm.id=:id " +
            " and (UPPER(memo.firm.firmName) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.fundName) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate <= :dateTo)" +
            " ORDER BY memo.meetingDate DESC")
    Page<MeetingMemo> findDateTo(@Param("id") Long id, @Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                 @Param("firmName")String firmName,
                                 @Param("fundName")String fundName,
                                 @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, Pageable pageable);

    @Query("select firm.firmName from PrivateEquityMeetingMemo memo LEFT JOIN memo.firm firm where memo.id=:id")
    String getFirmNameByMemoId(@Param("id") Long id);

    @Query("select fund.fundName from PrivateEquityMeetingMemo memo LEFT JOIN memo.fund fund where memo.id=:id")
    String getFundNameByMemoId(@Param("id") Long id);

}
