package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEFirm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zhambyl on 14-Nov-16.
 */
public interface PEFirmRepository extends PagingAndSortingRepository<PEFirm, Long> {

    List<PEFirm> findAllByOrderByFirmNameAsc();

    @Query("select firm from PEFirm firm where UPPER(firm.firmName) LIKE UPPER(CONCAT('%',:firmName, '%')) order by firm.firmName")
    Page<PEFirm> findByName(@Param("firmName") String firmName, Pageable pageable);
}