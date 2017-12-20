package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportUSDFormBalance;
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
public class ConsolidatedUSDFormBalanceConverter extends BaseDozerEntityConverter<ConsolidatedReportUSDFormBalance, ConsolidatedBalanceFormRecordDto> {

    @Override
    public ConsolidatedReportUSDFormBalance assemble(ConsolidatedBalanceFormRecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportUSDFormBalance entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedBalanceFormRecordDto disassemble(ConsolidatedReportUSDFormBalance entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedBalanceFormRecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportUSDFormBalance> assembleList(List<ConsolidatedBalanceFormRecordDto> dtoList, Long reportId){
        List<ConsolidatedReportUSDFormBalance> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedBalanceFormRecordDto dto: dtoList){
                ConsolidatedReportUSDFormBalance entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
