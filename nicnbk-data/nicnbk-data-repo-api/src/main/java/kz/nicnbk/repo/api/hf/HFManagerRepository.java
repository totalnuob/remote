package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HFManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HFManagerRepository extends PagingAndSortingRepository<HFManager, Long> {

    List<HFManager> findAllByOrderByNameDesc();

    @Query("select manager from HFManager manager where UPPER(manager.name) LIKE UPPER(CONCAT('%',:name,'%'))" )
    Page<HFManager> findByName(@Param("name") String name, Pageable pageable);

    @Query("select manager from HFManager manager where manager.investedInB=true")
    List<HFManager> findInvestedInBFunds();

}