package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import kz.nicnbk.repo.model.reporting.ReserveCalculationEntityType;
import kz.nicnbk.repo.model.reporting.ReserveCalculationExpenseType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.ReserveCalculationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ReserveCalculationConverter extends BaseDozerEntityConverter<ReserveCalculation, ReserveCalculationDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public ReserveCalculation assemble(ReserveCalculationDto dto) {
        ReserveCalculation entity = super.assemble(dto);
        if(dto.getExpenseType() != null){
            ReserveCalculationExpenseType type = this.lookupService.findByTypeAndCode(ReserveCalculationExpenseType.class, dto.getExpenseType().getCode());
            entity.setExpenseType(type);
        }
        if(dto.getSource() != null){
            ReserveCalculationEntityType source = this.lookupService.findByTypeAndCode(ReserveCalculationEntityType.class, dto.getSource().getCode());
            entity.setSource(source);
        }
        if(dto.getRecipient() != null){
            ReserveCalculationEntityType recipient = this.lookupService.findByTypeAndCode(ReserveCalculationEntityType.class, dto.getRecipient().getCode());
            entity.setRecipient(recipient);
        }
        return entity;
    }

    @Override
    public ReserveCalculationDto disassemble(ReserveCalculation entity) {
        ReserveCalculationDto dto = super.disassemble(entity);
        if(entity.getExpenseType() != null){
            BaseDictionaryDto type = new BaseDictionaryDto(entity.getExpenseType().getCode(), entity.getExpenseType().getNameEn(),
                    entity.getExpenseType().getNameRu(), entity.getExpenseType().getNameKz());
            dto.setExpenseType(type);
        }
        if(entity.getSource() != null){
            BaseDictionaryDto type = new BaseDictionaryDto(entity.getSource().getCode(), entity.getSource().getNameEn(),
                    entity.getSource().getNameRu(), entity.getSource().getNameKz());
            dto.setSource(type);
        }
        if(entity.getRecipient() != null){
            BaseDictionaryDto type = new BaseDictionaryDto(entity.getRecipient().getCode(), entity.getRecipient().getNameEn(),
                    entity.getRecipient().getNameRu(), entity.getRecipient().getNameKz());
            dto.setRecipient(type);
        }

        return dto;
    }
}
