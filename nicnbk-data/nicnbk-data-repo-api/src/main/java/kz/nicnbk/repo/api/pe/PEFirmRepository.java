package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEFirm;
import kz.nicnbk.repo.model.pe.PEFund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by zhambyl on 14-Nov-16.
 */
public interface PEFirmRepository extends PagingAndSortingRepository<PEFirm, Long> {

    @Query("select firm from PEFirm firm where UPPER(firm.firmName) LIKE UPPER(CONCAT('%',:firmName, '%'))")
    Page<PEFirm> findByName(@Param("firmName") String firmName, Pageable pageable);
}