package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.PeriodicReportType;
import kz.nicnbk.repo.model.reporting.ReportOtherInfo;
import kz.nicnbk.repo.model.reporting.ReportStatus;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.ReportOtherInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Deprecated
@Component
public class ReportingOtherInfoConverter extends BaseDozerEntityConverter<ReportOtherInfo, ReportOtherInfoDto> {

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportOtherInfo assemble(ReportOtherInfoDto dto) {
        ReportOtherInfo entity = super.assemble(dto);
        if(dto.getReport() != null && dto.getReport().getId() != null){
            entity.setReport(new PeriodicReport(dto.getReport().getId()));
        }
        return entity;
    }

    @Override
    public ReportOtherInfoDto disassemble(ReportOtherInfo entity) {
        ReportOtherInfoDto dto = super.disassemble(entity);
        // report
        if(entity.getReport() != null){
            dto.setReport(this.periodicReportConverter.disassemble(entity.getReport()));
        }
        return dto;
    }
}
