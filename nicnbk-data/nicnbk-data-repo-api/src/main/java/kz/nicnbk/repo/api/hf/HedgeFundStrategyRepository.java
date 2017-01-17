package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundStrategy;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface HedgeFundStrategyRepository extends PagingAndSortingRepository<HedgeFundStrategy, Long> {

    HedgeFundStrategy findByCode(String code);
}
