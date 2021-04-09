package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.common.BloombergStation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BloombergStationRepository extends PagingAndSortingRepository<BloombergStation, Long> {

    BloombergStation findByCode(String code);
}