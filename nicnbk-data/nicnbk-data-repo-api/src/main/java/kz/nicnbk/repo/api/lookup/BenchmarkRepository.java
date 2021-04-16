package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.benchmark.Benchmark;
import kz.nicnbk.repo.model.common.Currency;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 04.08.2016.
 */
public interface BenchmarkRepository extends PagingAndSortingRepository<Benchmark, Long> {

    Benchmark findByCode(String code);

    @Query("SELECT e FROM Benchmark e WHERE e.deleted IS NULL OR e.deleted=false")
    List<Benchmark> findNonDeleted();
}
