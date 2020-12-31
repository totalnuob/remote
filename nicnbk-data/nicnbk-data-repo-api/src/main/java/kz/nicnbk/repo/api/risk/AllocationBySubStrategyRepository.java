package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.risk.AllocationBySubStrategy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface AllocationBySubStrategyRepository extends PagingAndSortingRepository<AllocationBySubStrategy, Long> {

    List<AllocationBySubStrategy> findAllByOrderByFirstDateAsc();

    List<AllocationBySubStrategy> findByFirstDate(Date date);

    @Query("SELECT max(e.firstDate) FROM monitoring_risk_allocation_by_sub_strategy e")
    Date getMostRecentDate();
}
