package kz.nicnbk.repo.api.macromonitor;

import kz.nicnbk.repo.model.macromonitor.MacroMonitorScore;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zhambyl on 29-Mar-17.
 */
public interface MacroMonitorScoreRepository extends PagingAndSortingRepository<MacroMonitorScore, Long> {


    @Query("SELECT m from macro_monitor_score m WHERE m.type.id =:type order by m.date desc ")
    List<MacroMonitorScore> getEntitiesByType(@Param("type") Integer type);

}
