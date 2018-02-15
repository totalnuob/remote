package kz.nicnbk.repo.api.files;

import kz.nicnbk.repo.model.files.Files;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by magzumov on 04.07.2016.
 */
@Transactional
public interface FilesRepository extends PagingAndSortingRepository<Files, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Files f SET f.deleted=true where f.id=?1")
    int setDeleted(Long fileId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Files f SET f.deleted=false where f.id=?1")
    int revertDeleted(Long fileId);
}
