package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEOnePagerDescriptions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Pak on 02/03/2018.
 */
public interface PEOnePagerDescriptionsRepository extends CrudRepository<PEOnePagerDescriptions, Long> {

    @Query("SELECT e from pe_one_pager_descriptions e where e.fund.id=?1")
    List<PEOnePagerDescriptions> getEntitiesByFundId(Long fundId);

    @Query("SELECT e from pe_one_pager_descriptions e where e.fund.id=?1 and e.type=?2")
    List<PEOnePagerDescriptions> getEntitiesByFundIdAndType(Long fundId, int type);
}
