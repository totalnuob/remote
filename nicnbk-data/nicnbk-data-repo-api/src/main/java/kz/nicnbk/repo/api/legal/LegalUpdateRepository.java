package kz.nicnbk.repo.api.legal;

import kz.nicnbk.repo.model.legal.LegalUpdateNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface LegalUpdateRepository extends PagingAndSortingRepository<LegalUpdateNews, Long> {
    @Query("SELECT e FROM LegalUpdateNews e")
    Page<LegalUpdateNews> findByPage(Pageable pageable);
}
