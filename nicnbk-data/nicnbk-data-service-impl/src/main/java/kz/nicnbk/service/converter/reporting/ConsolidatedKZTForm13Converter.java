package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm13;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm13RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm13Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm13, ConsolidatedKZTForm13RecordDto> {

    @Override
    public ConsolidatedReportKZTForm13 assemble(ConsolidatedKZTForm13RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm13 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm13RecordDto disassemble(ConsolidatedReportKZTForm13 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm13RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm13> assembleList(List<ConsolidatedKZTForm13RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm13> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm13RecordDto dto: dtoList){
                ConsolidatedReportKZTForm13 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
