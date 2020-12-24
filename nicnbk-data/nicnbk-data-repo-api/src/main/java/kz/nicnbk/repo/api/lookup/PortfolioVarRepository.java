package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.risk.PortfolioVar;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PortfolioVarRepository extends PagingAndSortingRepository<PortfolioVar, Long> {

    PortfolioVar findByCode(String code);
}
