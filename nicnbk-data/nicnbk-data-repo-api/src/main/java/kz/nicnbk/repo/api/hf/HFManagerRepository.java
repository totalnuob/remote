package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HFManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface HFManagerRepository extends PagingAndSortingRepository<HFManager, Long> {

    @Query("select manager from HFManager manager where UPPER(manager.name) LIKE UPPER(CONCAT('%',:name,'%'))" )
    Page<HFManager> findByName(@Param("name") String name, Pageable pageable);
}