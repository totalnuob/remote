package kz.nicnbk.service.impl.macromonitor;

import kz.nicnbk.repo.api.macromonitor.MacroMonitorScoreRepository;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorScore;
import kz.nicnbk.service.api.macromonitor.MacroMonitorScoreService;
import kz.nicnbk.service.converter.macromonitor.MacroMonitorScoreEntityConverter;
import kz.nicnbk.service.dto.macromonitor.MacroMonitorScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            repository.deleteByTypeId(this.converter.assemble(macroMonitorScoreDtoList.get(0)).getType().getId());

            for (MacroMonitorScoreDto dto : macroMonitorScoreDtoList) {
                MacroMonitorScore entity = this.converter.assemble(dto);
//                if(entity.getId() != null) {
//                    repository.delete(entity.getId());
//                }
                repository.save(entity);
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
