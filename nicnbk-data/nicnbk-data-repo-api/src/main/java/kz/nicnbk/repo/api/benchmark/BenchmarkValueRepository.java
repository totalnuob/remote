package kz.nicnbk.repo.api.benchmark;

import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface BenchmarkValueRepository extends PagingAndSortingRepository<BenchmarkValue, Long> {

    @Query("SELECT value FROM BenchmarkValue value WHERE value.date >= :fromDate AND value.benchmark.code = :benchmarkCode")
    Page<BenchmarkValue> getValuesFromDate(@Param("fromDate") @Temporal(TemporalType.DATE) Date fromDate,
                                           @Param("benchmarkCode")String benchmarkCode, Pageable pageable);

    @Query("SELECT value FROM BenchmarkValue value WHERE value.date >= :fromDate AND value.date <= :toDate AND " +
            " (:benchmarkCode is null OR value.benchmark.code = :benchmarkCode)")
    Page<BenchmarkValue> getValuesBetweenDates(@Param("fromDate") @Temporal(TemporalType.DATE) Date fromDate,
                                           @Param("toDate") @Temporal(TemporalType.DATE) Date toDate,
                                           @Param("benchmarkCode")String benchmarkCode, Pageable pageable);

    @Query("SELECT entity FROM BenchmarkValue entity WHERE entity.date = ?1 AND entity.benchmark.code = ?2")
    BenchmarkValue getValuesForDateAndType(@Param("date") @Temporal(TemporalType.DATE) Date date,
                                           @Param("typeCode") String typeCode);

}
