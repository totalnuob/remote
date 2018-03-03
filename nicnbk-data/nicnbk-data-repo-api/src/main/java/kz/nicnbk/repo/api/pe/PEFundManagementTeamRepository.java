package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEFundManagementTeam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public interface PEFundManagementTeamRepository extends CrudRepository<PEFundManagementTeam, Long> {

    @Query("SELECT e from pe_fund_management_team e where e.fund.id=?1")
    List<PEFundManagementTeam> getEntitiesByFundId(Long fundId);
}
