package kz.nicnbk.service.impl.macromonitor;

import kz.nicnbk.repo.api.macromonitor.MacroMonitorScoreRepository;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorScore;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorType;
import kz.nicnbk.service.api.macromonitor.MacroMonitorScoreService;
import kz.nicnbk.service.converter.macromonitor.MacroMonitorScoreEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.macromonitor.MacroMonitorScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhambyl on 30-Mar-17.
 */
@Service
public class MacroMonitorScoreServiceImpl implements MacroMonitorScoreService {

    @Autowired
    private MacroMonitorScoreRepository repository;

    @Autowired
    private MacroMonitorScoreEntityConverter converter;


    @Override
    public Long save(List<MacroMonitorScoreDto> macroMonitorScoreDtoList) {
        if(macroMonitorScoreDtoList != null) {
            for (MacroMonitorScoreDto dto : macroMonitorScoreDtoList) {
                repository.save(this.converter.assemble(dto));
            }
        }
        return 1L;
    }

    @Override
    public List<MacroMonitorScoreDto> getList(Integer typeId) {
        List<MacroMonitorScore> entities = this.repository.getEntitiesByType(typeId);
        List<MacroMonitorScoreDto> dtoList = this.converter.disassembleList(entities);

        return dtoList;
    }
}
