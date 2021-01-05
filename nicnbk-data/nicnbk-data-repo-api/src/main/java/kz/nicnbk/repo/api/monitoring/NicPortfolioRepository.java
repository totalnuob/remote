package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.NicPortfolio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Pak on 13.06.2019.
 */

public interface NicPortfolioRepository extends PagingAndSortingRepository<NicPortfolio, Long> {

    List<NicPortfolio> findAllByOrderByDateAsc();

    @Query("SELECT max(e.date) FROM monitoring_nic_portfolio e")
    Date getMostRecentDate();
}
