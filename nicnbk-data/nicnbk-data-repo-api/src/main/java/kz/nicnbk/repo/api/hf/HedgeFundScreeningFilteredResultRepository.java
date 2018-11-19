package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreening;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningFilteredResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

public interface HedgeFundScreeningFilteredResultRepository extends PagingAndSortingRepository<HedgeFundScreeningFilteredResult, Long> {


    List<HedgeFundScreeningFilteredResult> findByScreeningId(Long screeningId);
}