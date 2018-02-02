package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm3;
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
public class ConsolidatedKZTForm3Converter extends BaseDozerEntityConverter<ConsolidatedReportKZTForm3, ConsolidatedBalanceFormRecordDto> {

    @Override
    public ConsolidatedReportKZTForm3 assemble(ConsolidatedBalanceFormRecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportKZTForm3 entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedBalanceFormRecordDto disassemble(ConsolidatedReportKZTForm3 entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedBalanceFormRecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportKZTForm3> assembleList(List<ConsolidatedBalanceFormRecordDto> dtoList, Long reportId){
        List<ConsolidatedReportKZTForm3> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedBalanceFormRecordDto dto: dtoList){
                ConsolidatedReportKZTForm3 entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
