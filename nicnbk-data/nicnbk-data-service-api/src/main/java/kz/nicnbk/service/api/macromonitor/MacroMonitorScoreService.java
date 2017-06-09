package kz.nicnbk.service.api.macromonitor;

import kz.nicnbk.repo.model.macromonitor.MacroMonitorScore;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.macromonitor.MacroMonitorScoreDto;

import java.util.List;

/**
 * Created by zhambyl on 30-Mar-17.
 */
public interface MacroMonitorScoreService extends BaseService {


    Long save(List<MacroMonitorScoreDto> macroMonitorScoreDtoList, String username);

    List<MacroMonitorScoreDto> getList(Integer typeId);

    Long deleteAll(Integer typeId, String username);

}
