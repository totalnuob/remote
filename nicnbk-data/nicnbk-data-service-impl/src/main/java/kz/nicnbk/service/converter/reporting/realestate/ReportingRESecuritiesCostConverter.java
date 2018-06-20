package kz.nicnbk.service.converter.reporting.realestate;

import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.realestate.REBalanceType;
import kz.nicnbk.repo.model.reporting.realestate.ReportingREBalanceSheet;
import kz.nicnbk.repo.model.reporting.realestate.ReportingRESecuritiesCost;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.realestate.TerraBalanceSheetRecordDto;
import kz.nicnbk.service.dto.reporting.realestate.TerraSecuritiesCostRecordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ReportingRESecuritiesCostConverter extends BaseDozerEntityConverter<ReportingRESecuritiesCost, TerraSecuritiesCostRecordDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportingRESecuritiesCost assemble(TerraSecuritiesCostRecordDto dto) {
        if (dto == null) {
            return null;
        }
        ReportingRESecuritiesCost entity = super.assemble(dto);
        if(dto.getReport() != null && dto.getReport().getId() != null){
            entity.setReport(new PeriodicReport(dto.getReport().getId()));
        }

        return entity;
    }

    @Override
    public TerraSecuritiesCostRecordDto disassemble(ReportingRESecuritiesCost entity) {
        if (entity == null) {
            return null;
        }
        TerraSecuritiesCostRecordDto dto = super.disassemble(entity);
        //report
        if(entity.getReport() != null) {
            dto.setReport(this.periodicReportConverter.disassemble(entity.getReport()));
        }

        return dto;
    }
}
