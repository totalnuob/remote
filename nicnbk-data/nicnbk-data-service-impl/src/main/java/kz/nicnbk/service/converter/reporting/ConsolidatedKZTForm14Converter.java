package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm14;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedKZTForm14RecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm14Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm14, ConsolidatedKZTForm14RecordDto> {

    @Override
    public ConsolidatedReportKZTForm14 assemble(ConsolidatedKZTForm14RecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm14 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedKZTForm14RecordDto disassemble(ConsolidatedReportKZTForm14 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedKZTForm14RecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm14> assembleList(List<ConsolidatedKZTForm14RecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm14> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedKZTForm14RecordDto dto: dtoList){
                ConsolidatedReportKZTForm14 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
