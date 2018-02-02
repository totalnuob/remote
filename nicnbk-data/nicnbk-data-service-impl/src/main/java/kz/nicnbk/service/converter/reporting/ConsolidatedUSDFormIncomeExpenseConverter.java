package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportUSDFormIncomeExpense;
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
public class ConsolidatedUSDFormIncomeExpenseConverter extends BaseDozerEntityConverter<ConsolidatedReportUSDFormIncomeExpense, ConsolidatedBalanceFormRecordDto> {

    @Override
    public ConsolidatedReportUSDFormIncomeExpense assemble(ConsolidatedBalanceFormRecordDto dto) {
        if (dto == null) {
            return null;
        }
        ConsolidatedReportUSDFormIncomeExpense entity = super.assemble(dto);

        return entity;
    }

    @Override
    public ConsolidatedBalanceFormRecordDto disassemble(ConsolidatedReportUSDFormIncomeExpense entity) {
        if (entity == null) {
            return null;
        }
        ConsolidatedBalanceFormRecordDto dto = super.disassemble(entity);

        return dto;
    }

    public List<ConsolidatedReportUSDFormIncomeExpense> assembleList(List<ConsolidatedBalanceFormRecordDto> dtoList, Long reportId){
        List<ConsolidatedReportUSDFormIncomeExpense> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedBalanceFormRecordDto dto: dtoList){
                ConsolidatedReportUSDFormIncomeExpense entity = assemble(dto);
                entity.setReport(new PeriodicReport(reportId));
                entities.add(entity);
            }
        }
        return entities;
    }
}
