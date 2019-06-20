package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.LiquidPortfolio;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Pak on 20.06.2019.
 */

public interface LiquidPortfolioRepository extends PagingAndSortingRepository<LiquidPortfolio, Long> {

    List<LiquidPortfolio> findAllByOrderByDateAsc();
}
