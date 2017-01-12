package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.news.NewsType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface NewsTypeRepository extends PagingAndSortingRepository<NewsType, Long> {

    NewsType findByCode(String code);
}
