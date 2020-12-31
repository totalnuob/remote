package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import kz.nicnbk.repo.model.risk.AllocationByTopPortfolio;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface AllocationByTopPortfolioRepository extends PagingAndSortingRepository<AllocationByTopPortfolio, Long> {

    List<AllocationByTopPortfolio> findAllByOrderByDateAsc();

    AllocationByTopPortfolio findByDate(Date date);

//    AllocationByTopPortfolio findByFundName(String fundName);

    List<AllocationByTopPortfolio> findByFundName(String fundName);

    List<AllocationByTopPortfolio> findAllocationsByDate(Date date);
}
