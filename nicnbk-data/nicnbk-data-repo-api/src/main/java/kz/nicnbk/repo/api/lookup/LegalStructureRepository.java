package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.LegalStructure;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface LegalStructureRepository extends PagingAndSortingRepository<LegalStructure, Long> {
    LegalStructure findByCode(String code);
}
