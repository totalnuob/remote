package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.repo.model.risk.PortfolioVarValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;

public interface PortfolioVarValueRepository extends PagingAndSortingRepository<PortfolioVarValue, Long> {
    @Query("SELECT value FROM PortfolioVarValue value WHERE value.date >= :fromDate AND value.portfolioVar.code = :portfolioVarCode")
    Page<PortfolioVarValue> getValuesFromDate(@Param("fromDate") @Temporal(TemporalType.DATE) Date fromDate,
                                           @Param("portfolioVarCode")String portfolioVarCode, Pageable pageable);

    @Query("SELECT value FROM PortfolioVarValue value WHERE value.date >= :fromDate AND value.date <= :toDate AND " +
            " (:portfolioVarCode is null OR value.portfolioVar.code = :portfolioVarCode)")
    Page<PortfolioVarValue> getValuesBetweenDates(@Param("fromDate") @Temporal(TemporalType.DATE) Date fromDate,
                                               @Param("toDate") @Temporal(TemporalType.DATE) Date toDate,
                                               @Param("portfolioVarCode")String portfolioVarCode, Pageable pageable);

    @Query("SELECT entity FROM PortfolioVarValue entity WHERE entity.date = ?1 AND entity.portfolioVar.code = ?2")
    PortfolioVarValue getValuesForDateAndType(@Param("date") @Temporal(TemporalType.DATE) Date date,
                                           @Param("typeCode") String typeCode);

    @Query("SELECT entity FROM PortfolioVarValue entity WHERE entity.date = ?1")
    PortfolioVarValue getValuesForDate(@Param("date") @Temporal(TemporalType.DATE) Date date);
}
