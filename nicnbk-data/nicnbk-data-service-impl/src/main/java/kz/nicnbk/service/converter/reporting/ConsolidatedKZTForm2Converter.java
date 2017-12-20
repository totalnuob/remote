package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm2;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedBalanceFormRecordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ConsolidatedKZTForm2Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm2, ConsolidatedBalanceFormRecordDto> {

    @Override
    public ConsolidatedReportKZTForm2 assemble(ConsolidatedBalanceFormRecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm2 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedBalanceFormRecordDto disassemble(ConsolidatedReportKZTForm2 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedBalanceFormRecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm2> assembleList(List<ConsolidatedBalanceFormRecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm2> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedBalanceFormRecordDto dto: dtoList){
                ConsolidatedReportKZTForm2 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
