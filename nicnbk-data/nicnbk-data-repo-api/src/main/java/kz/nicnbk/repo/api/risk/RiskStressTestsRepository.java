package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.risk.RiskStressTests;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;


public interface RiskStressTestsRepository extends PagingAndSortingRepository<RiskStressTests, Long> {

    List<RiskStressTests> findByDate(Date date);
}
