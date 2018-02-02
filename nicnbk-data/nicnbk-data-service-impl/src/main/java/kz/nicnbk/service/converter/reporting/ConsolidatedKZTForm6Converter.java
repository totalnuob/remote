package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm6;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm6RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm6Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm6, ConsolidatedKZTForm6RecordDto> {

    @Override
    public ConsolidatedReportKZTForm6 assemble(ConsolidatedKZTForm6RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm6 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm6RecordDto disassemble(ConsolidatedReportKZTForm6 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm6RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm6> assembleList(List<ConsolidatedKZTForm6RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm6> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm6RecordDto dto: dtoList){
                ConsolidatedReportKZTForm6 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
