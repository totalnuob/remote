package kz.nicnbk.service.converter.common;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;
import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm1;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerTypedEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedBalanceFormRecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class BaseTypeEntityConverter extends BaseDozerTypedEntityConverter<BaseTypeEntityImpl, BaseDictionaryDto> {

    @Override
    public BaseTypeEntityImpl assemble(BaseDictionaryDto dto) {
        if (dto == null) {
            return null;
        }
        BaseTypeEntityImpl entity = super.assemble(dto);

        return entity;
    }

    @Override
    public BaseDictionaryDto disassemble(BaseTypeEntityImpl entity) {
        if (entity == null) {
            return null;
        }
        BaseDictionaryDto dto = super.disassemble(entity);

        return dto;
    }
}
