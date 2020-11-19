package kz.nicnbk.repo.api.tag;

import kz.nicnbk.repo.model.tag.Tag;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


/**
 * Created by magzumov.
 */
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {

    Tag findByName(String name);

    List<Tag> findByTypeCode(String code);

    Tag findByNameAndTypeCode(String name, String code);
}
