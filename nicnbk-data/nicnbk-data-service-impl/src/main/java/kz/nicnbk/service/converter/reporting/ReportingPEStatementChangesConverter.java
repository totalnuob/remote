package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementChanges;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.StatementChangesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ReportingPEStatementChangesConverter extends BaseDozerEntityConverter<ReportingPEStatementChanges, StatementChangesDto> {

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportingPEStatementChanges assemble(StatementChangesDto dto) {
        if (dto == null) {
            return null;
        }
        ReportingPEStatementChanges entity = super.assemble(dto);
        if(dto.getReport() != null && dto.getReport().getId() != null){
            entity.setReport(new PeriodicReport(dto.getReport().getId()));
        }

        return entity;
    }

    @Override
    public StatementChangesDto disassemble(ReportingPEStatementChanges entity) {
        if (entity == null) {
            return null;
        }
        StatementChangesDto dto = super.disassemble(entity);
        //report
        if(entity.getReport() != null) {
            dto.setReport(this.periodicReportConverter.disassemble(entity.getReport()));
        }

        return dto;
    }
}
