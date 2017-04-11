package kz.nicnbk.repo.api.macromonitor;

import kz.nicnbk.repo.model.macromonitor.MacroMonitorField;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 30-Mar-17.
 */
public interface MacroMonitorFieldRepository extends PagingAndSortingRepository<MacroMonitorField, Long> {

    MacroMonitorField findByCode(String code);
}
