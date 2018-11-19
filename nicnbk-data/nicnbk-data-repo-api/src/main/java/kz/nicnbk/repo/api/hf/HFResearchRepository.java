package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HFResearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by zhambyl on 06/11/2018.
 */
public interface HFResearchRepository extends PagingAndSortingRepository<HFResearch, Long> {


    @Query("select research from HFResearch research where research.manager.id=:managerId ")
    HFResearch findByManagerId(@Param("managerId") Long managerId);


}
