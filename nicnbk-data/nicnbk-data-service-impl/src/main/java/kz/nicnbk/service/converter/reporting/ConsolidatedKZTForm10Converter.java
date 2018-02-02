package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm10;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm10RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm10Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm10, ConsolidatedKZTForm10RecordDto> {

    @Override
    public ConsolidatedReportKZTForm10 assemble(ConsolidatedKZTForm10RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm10 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm10RecordDto disassemble(ConsolidatedReportKZTForm10 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm10RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm10> assembleList(List<ConsolidatedKZTForm10RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm10> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm10RecordDto dto: dtoList){
                ConsolidatedReportKZTForm10 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
