package kz.nicnbk.repo.api.tripMemo;

import kz.nicnbk.repo.model.tripmemo.TripMemo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;


/**
 * Created by zhambyl on 04-Aug-16.
 */
public interface TripMemoRepository extends PagingAndSortingRepository<TripMemo, Long> {

    /**
     * Return all entities ordered by id descending
     *
     * @return - all entities
     */
    public List<TripMemo> findAllByOrderByIdDesc();

    /**
     * Return entities page.
     *
     * @param pageable - page params
     * @return - page
     */
    public Page<TripMemo> findAllByOrderByMeetingDateStartDesc(Pageable pageable);

    /**
     * Return entities page sorted by creationDate in descending order
     *
     * @param pageable
     * @return
     */
    public Page<TripMemo> findAllByOrderByCreationDateDesc(Pageable pageable);

    /**
     * Return entities matching specified params ordered by meeting date desc.
     * Params without dates.
     *
     * @param tripType
     * @param organization
     * @param location
     * @param status
     * @param pageable
     * @return
     */
    @Query("select tripMemo from TripMemo tripMemo where (tripMemo.tripType=:tripType or :tripType is null) " +
            " and (tripMemo.status=:status or :status is null)" +
            " and (UPPER(tripMemo.organization) LIKE UPPER(CONCAT('%',:organization,'%'))  or :organization is null or :organization = '') " +
            " and (UPPER(tripMemo.location) LIKE UPPER(CONCAT('%',:location,'%'))  or :location is null or :location = '') " +
            " ORDER BY tripMemo.meetingDateStart DESC")
    Page<TripMemo> findWithoutDates(@Param("tripType")String tripType, @Param("organization")String organization,
                                    @Param("location")String location, @Param("status")String status, Pageable pageable);


    /**
     * Return entities matching specified params ordered by meeting date desc.
     * Params include date range.
     *
     * @param tripType
     * @param organization
     * @param dateFrom
     * @param dateTo
     * @param location
     * @param status
     * @param pageable
     * @return
     */
    @Query("select tripMemo from TripMemo tripMemo where (tripMemo.tripType=:tripType or :tripType is null) " +
            " and (tripMemo.status=:status or :status is null)" +
            " and (UPPER(tripMemo.organization) LIKE UPPER(CONCAT('%',:organization,'%'))  or :organization is null or :organization = '') " +
            " and (UPPER(tripMemo.location) LIKE UPPER(CONCAT('%',:location,'%'))  or :location is null or :location = '') " +
            " and (tripMemo.meetingDateStart >= :dateFrom) and (tripMemo.meetingDateStart <= :dateTo)" +
            " ORDER BY tripMemo.meetingDateStart DESC")
    Page<TripMemo> findBothDates(@Param("tripType")String tripType, @Param("organization")String organization,
                                 @Param("dateFrom")@Temporal(TemporalType.DATE)Date dateFrom,
                                 @Param("dateTo")@Temporal(TemporalType.DATE)Date dateTo,
                                 @Param("location")String location, @Param("status")String status, Pageable pageable);

    /**
     * Return entities matching specified params ordered by meeting date desc.
     * Params date from.
     *
     * @param tripType
     * @param organization
     * @param dateFrom
     * @param location
     * @param status
     * @param pageable
     * @return
     */
    @Query("select tripMemo from TripMemo tripMemo where (tripMemo.tripType=:tripType or :tripType is null) " +
            " and (tripMemo.status=:status or :status is null)" +
            " and (UPPER(tripMemo.organization) LIKE UPPER(CONCAT('%',:organization,'%'))  or :organization is null or :organization = '') " +
            " and (UPPER(tripMemo.location) LIKE UPPER(CONCAT('%',:location,'%'))  or :location is null or :location = '') " +
            " and (tripMemo.meetingDateStart >= :dateFrom)" +
            " ORDER BY tripMemo.meetingDateStart DESC")
    Page<TripMemo> findDateFrom(@Param("tripType")String tripType, @Param("organization")String organization,
                                @Param("dateFrom")@Temporal(TemporalType.DATE)Date dateFrom,
                                @Param("location")String location, @Param("status")String status, Pageable pageable);

    /**
     * Return entities matching specified params ordered by meeting date desc.
     * Params date until.
     *
     * @param tripType
     * @param organization
     * @param dateTo
     * @param location
     * @param status
     * @param pageable
     * @return
     */
    @Query("select tripMemo from TripMemo tripMemo where (tripMemo.tripType=:tripType or :tripType is null) " +
            " and (tripMemo.status=:status or :status is null)" +
            " and (UPPER(tripMemo.organization) LIKE UPPER(CONCAT('%',:organization,'%'))  or :organization is null or :organization = '') " +
            " and (UPPER(tripMemo.location) LIKE UPPER(CONCAT('%',:location,'%'))  or :location is null or :location = '') " +
            " and (tripMemo.meetingDateStart <= :dateTo)" +
            " ORDER BY tripMemo.meetingDateStart DESC")
    Page<TripMemo> findDateTo(@Param("tripType")String tripType, @Param("organization")String organization,
                              @Param("dateTo")@Temporal(TemporalType.DATE)Date dateTo,
                              @Param("location")String location, @Param("status")String status, Pageable pageable);
}
