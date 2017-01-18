package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.common.Substrategy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface SubstrategyRepository extends PagingAndSortingRepository<Substrategy, Long> {

    @Query("select substrategy from Substrategy substrategy where substrategy.strategy.code=:strategyCode ")
    List<Substrategy> findByStrategy(@Param("strategyCode") String strategyCode);

    Substrategy findByCode(String code);
}
