package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm22;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm22RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm22Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm22, ConsolidatedKZTForm22RecordDto> {

    @Override
    public ConsolidatedReportKZTForm22 assemble(ConsolidatedKZTForm22RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm22 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm22RecordDto disassemble(ConsolidatedReportKZTForm22 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm22RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm22> assembleList(List<ConsolidatedKZTForm22RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm22> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm22RecordDto dto: dtoList){
                ConsolidatedReportKZTForm22 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
