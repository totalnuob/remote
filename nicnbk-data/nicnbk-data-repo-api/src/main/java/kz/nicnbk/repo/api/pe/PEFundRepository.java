package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEFund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by zhambyl on 14-Nov-16.
 */
public interface PEFundRepository extends PagingAndSortingRepository<PEFund, Long> {

    @Query("select fund from PEFund fund where fund.firm.id=:firmId ORDER BY fund.vintage ASC")
    Page<PEFund> findByFirmId(@Param("firmId") Long firmId, Pageable pageable);
}