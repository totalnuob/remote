package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.repo.model.news.NewsType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface FilesTypeRepository extends PagingAndSortingRepository<FilesType, Long> {

    FilesType findByCode(String code);
}
