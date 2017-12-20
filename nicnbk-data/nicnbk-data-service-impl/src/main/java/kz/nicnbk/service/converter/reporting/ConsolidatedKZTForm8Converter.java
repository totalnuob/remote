package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm8;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm8RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm8Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm8, ConsolidatedKZTForm8RecordDto> {

    @Override
    public ConsolidatedReportKZTForm8 assemble(ConsolidatedKZTForm8RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm8 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm8RecordDto disassemble(ConsolidatedReportKZTForm8 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm8RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm8> assembleList(List<ConsolidatedKZTForm8RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm8> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm8RecordDto dto: dtoList){
                ConsolidatedReportKZTForm8 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
