package kz.nicnbk.repo.api.news;

import kz.nicnbk.repo.model.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface NewsRepository extends PagingAndSortingRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE n.type.code = :type")
    Page<News> findByType(@Param("type")String type, Pageable pageable);
}
