//package kz.nicnbk.repo.api.hf;
//
//import kz.nicnbk.repo.model.hf.HedgeFundStrategyBreakdown;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//public interface HedgeFundStrategyBreakdownRepository extends PagingAndSortingRepository<HedgeFundStrategyBreakdown, Long> {
//
//    @Query("SELECT e from hf_strategy_breakdown e where e.fund.id=?1")
//    List<HedgeFundStrategyBreakdown> getEntitiesByFundId(Long fundId);
//
//
//    @Modifying
//    @Transactional
//    @Query("DELETE from hf_strategy_breakdown e where e.fund.id=?1")
//    void deleteByFundId(Long fundId);
//}