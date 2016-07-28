package kz.nicnbk.repo.api.files;

import kz.nicnbk.repo.model.files.Files;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface FilesRepository extends PagingAndSortingRepository<Files, Long> {
}
