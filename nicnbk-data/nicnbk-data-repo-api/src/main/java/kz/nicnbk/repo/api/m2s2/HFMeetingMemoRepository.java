package kz.nicnbk.repo.api.m2s2;

import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
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
public interface HFMeetingMemoRepository extends PagingAndSortingRepository<HedgeFundsMeetingMemo, Long> {

    @Query("SELECT m FROM HedgeFundsMeetingMemo m " +
            " LEFT JOIN FETCH m.arrangedBy LEFT JOIN FETCH m.attendeesNIC LEFT JOIN FETCH m.fundSizeCurrency " +
            " LEFT JOIN FETCH m.geographies LEFT JOIN FETCH m.strategies " +
            //" LEFT JOIN FETCH m.memoType " +
            " LEFT JOIN FETCH m.meetingType " +
            " WHERE m.id = :id")
    HedgeFundsMeetingMemo getFullEagerById(@Param("id") Long id);

    Page<HedgeFundsMeetingMemo> findAllByManagerIdOrderByMeetingDateDesc(@Param("id") Long id, Pageable pageable);

    @Query("SELECT memo FROM HedgeFundsMeetingMemo memo " +
            "where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and memo.manager.id=:id " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and (UPPER(memo.manager.name) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.name) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '')" +
            " ORDER BY memo.meetingDate DESC")
    Page<HedgeFundsMeetingMemo> findWithoutDates(@Param("id") Long id,
                                       @Param("meetingType") String meetingType,
                                       @Param("memoType") Integer memoType,
                                       @Param("firmName")String firmName,
                                       @Param("fundName")String fundName,
                                       Pageable pageable);

    @Query("select memo from HedgeFundsMeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and memo.manager.id=:id " +
            " and (UPPER(memo.manager.name) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.name) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom) and (memo.meetingDate <= :dateTo)" +
            " ORDER BY memo.meetingDate DESC")
    Page<HedgeFundsMeetingMemo> findBothDates(@Param("id") Long id, @Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                    @Param("firmName")String firmName,
                                    @Param("fundName")String fundName, @Param("dateFrom")@Temporal(TemporalType.DATE) Date dateFrom,
                                    @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, Pageable pageable);

    @Query("select memo from HedgeFundsMeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and memo.manager.id=:id " +
            " and (UPPER(memo.manager.name) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.name) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate >= :dateFrom)" +
            " ORDER BY memo.meetingDate DESC")
    Page<HedgeFundsMeetingMemo> findDateFrom(@Param("id") Long id, @Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                   @Param("firmName")String firmName,
                                   @Param("fundName")String fundName, @Temporal(TemporalType.DATE) @Param("dateFrom") Date dateFrom, Pageable pageable);

    @Query("select memo from HedgeFundsMeetingMemo memo where (memo.meetingType.code=:meetingType or :meetingType is null) " +
            " and (memo.memoType=:memoType or :memoType is null or :memoType=0) " +
            " and memo.manager.id=:id " +
            " and (UPPER(memo.manager.name) LIKE UPPER(CONCAT('%',:firmName,'%')) or :firmName is null or :firmName = '') " +
            " and (UPPER(memo.fund.name) LIKE UPPER(CONCAT('%',:fundName,'%')) or :fundName is null or :fundName = '') " +
            " and (memo.meetingDate <= :dateTo)" +
            " ORDER BY memo.meetingDate DESC")
    Page<HedgeFundsMeetingMemo> findDateTo(@Param("id") Long id, @Param("meetingType") String meetingType, @Param("memoType") Integer memoType,
                                 @Param("firmName")String firmName,
                                 @Param("fundName")String fundName,
                                 @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo, Pageable pageable);

    @Query("select manager.name from HedgeFundsMeetingMemo memo LEFT JOIN memo.manager manager where memo.id=:id")
    String getManagerNameByMemoId(@Param("id") Long id);

    @Query("select fund.name from HedgeFundsMeetingMemo memo LEFT JOIN memo.fund fund where memo.id=:id")
    String getFundNameByMemoId(@Param("id") Long id);
}
