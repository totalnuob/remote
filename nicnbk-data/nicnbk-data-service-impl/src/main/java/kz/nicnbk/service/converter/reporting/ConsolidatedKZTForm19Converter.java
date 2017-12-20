package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm19;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm19RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm19Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm19, ConsolidatedKZTForm19RecordDto> {

    @Override
    public ConsolidatedReportKZTForm19 assemble(ConsolidatedKZTForm19RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm19 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm19RecordDto disassemble(ConsolidatedReportKZTForm19 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm19RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm19> assembleList(List<ConsolidatedKZTForm19RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm19> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm19RecordDto dto: dtoList){
                ConsolidatedReportKZTForm19 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
