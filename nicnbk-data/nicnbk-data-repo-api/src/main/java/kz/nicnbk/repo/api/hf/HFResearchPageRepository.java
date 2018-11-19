package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HFResearchPage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by zhambyl on 12/11/2018.
 */
public interface HFResearchPageRepository extends PagingAndSortingRepository<HFResearchPage, Long> {

    @Query("select page from HFResearchPage page where page.manager.id=:managerId ORDER BY page.date DESC")
    List<HFResearchPage> findByManager(@Param("managerId") Long managerId);

    @Query("select MAX(page.date) from HFResearchPage page where page.manager.id=:managerId")
    Date findLatestResearch(@Param("managerId") Long managerId);
}