package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.Firm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by zhambyl on 14-Nov-16.
 */
public interface FirmRepository extends PagingAndSortingRepository<Firm, Long> {

    @Query("select firm from Firm firm where UPPER(firm.firmName) LIKE UPPER(CONCAT('%',:firmName, '%'))")
    Page<Firm> findByName(@Param("firmName") String firmName, Pageable pageable);
}
