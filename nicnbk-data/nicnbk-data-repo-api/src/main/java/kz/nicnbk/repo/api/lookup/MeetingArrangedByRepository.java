package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface MeetingArrangedByRepository extends PagingAndSortingRepository<MeetingArrangedBy, Long> {

    MeetingArrangedBy findByCode(String code);
}
