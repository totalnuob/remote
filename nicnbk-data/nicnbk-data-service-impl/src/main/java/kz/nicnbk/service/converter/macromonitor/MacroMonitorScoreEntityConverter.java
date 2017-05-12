package kz.nicnbk.service.converter.macromonitor;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorField;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorScore;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.macromonitor.MacroMonitorScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhambyl on 30-Mar-17.
 */
@Component
public class MacroMonitorScoreEntityConverter extends BaseDozerEntityConverter<MacroMonitorScore, MacroMonitorScoreDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public MacroMonitorScore assemble(MacroMonitorScoreDto dto) {
        MacroMonitorScore entity = super.assemble(dto);

        if(StringUtils.isNotEmpty(dto.getField())) {
            MacroMonitorField field = lookupService.findByTypeAndCode(MacroMonitorField.class, dto.getField());
            entity.setField(field);
        }

        if(StringUtils.isNotEmpty(dto.getType())) {
            MacroMonitorType type = lookupService.findByTypeAndCode(MacroMonitorType.class, dto.getType());
            entity.setType(type);
        }

        return entity;
    }

    @Override
    public MacroMonitorScoreDto disassemble(MacroMonitorScore entity) {
        MacroMonitorScoreDto dto = super.disassemble(entity);
        return dto;
    }

    @Override
    public List<MacroMonitorScore> assembleList(List<MacroMonitorScoreDto> dtoList) {
        List<MacroMonitorScore> entityList = new ArrayList<>();
        if(dtoList != null) {
            for(MacroMonitorScoreDto dto: dtoList) {
                entityList.add(assemble(dto));
            }
        }
        return entityList;
    }

    @Override
    public List<MacroMonitorScoreDto> disassembleList(List<MacroMonitorScore> entityList) {
        List<MacroMonitorScoreDto> dtoList = new ArrayList<>();
        if(entityList != null) {
            for(MacroMonitorScore entity: entityList) {
                MacroMonitorScoreDto dto = disassemble(entity);
                dto.setField(entity.getField().getCode());
                dto.setType(entity.getType().getCode());
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}
