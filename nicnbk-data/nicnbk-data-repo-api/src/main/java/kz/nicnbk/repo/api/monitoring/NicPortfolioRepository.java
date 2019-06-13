package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Pak on 13.06.2019.
 */

public interface NicPortfolioRepository extends PagingAndSortingRepository<NicPortfolio, Long> {
}
