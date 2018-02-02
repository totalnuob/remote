package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm7;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm7RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm7Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm7, ConsolidatedKZTForm7RecordDto> {

    @Override
    public ConsolidatedReportKZTForm7 assemble(ConsolidatedKZTForm7RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm7 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm7RecordDto disassemble(ConsolidatedReportKZTForm7 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm7RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm7> assembleList(List<ConsolidatedKZTForm7RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm7> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm7RecordDto dto: dtoList){
                ConsolidatedReportKZTForm7 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
