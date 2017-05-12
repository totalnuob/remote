package kz.nicnbk.repo.api.macromonitor;

import kz.nicnbk.repo.model.macromonitor.MacroMonitorType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 30-Mar-17.
 */
public interface MacroMonitorTypeRepository extends PagingAndSortingRepository<MacroMonitorType, Long> {

    MacroMonitorType findByCode(String code);

}
