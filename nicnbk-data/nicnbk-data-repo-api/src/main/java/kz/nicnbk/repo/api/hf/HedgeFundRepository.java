package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface HedgeFundRepository extends PagingAndSortingRepository<HedgeFund, Long> {

    @Query("select fund from HedgeFund fund where fund.manager.id=:managerId ")
    Page<HedgeFund> findByManager(@Param("managerId") Long managerId, Pageable pageable);


    @Query("select fund from HedgeFund fund where UPPER(fund.name) LIKE UPPER(CONCAT('%',:fundName,'%'))" )
            //" or UPPER(fund.manager.name) LIKE UPPER(CONCAT('%',:fundName,'%'))")
    Page<HedgeFund> findByName(@Param("fundName") String fundName,  Pageable pageable);

}