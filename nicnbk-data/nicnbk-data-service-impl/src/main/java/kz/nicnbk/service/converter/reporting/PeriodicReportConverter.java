package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.PeriodicReportType;
import kz.nicnbk.repo.model.reporting.ReportStatus;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class PeriodicReportConverter extends BaseDozerEntityConverter<PeriodicReport, PeriodicReportDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public PeriodicReport assemble(PeriodicReportDto dto) {
        if (dto == null) {
            return null;
        }
        PeriodicReport entity = super.assemble(dto);

        entity.setReportDate(DateUtils.getLastDayOfCurrentMonth(dto.getReportDate()));
        // type
        if(StringUtils.isNotEmpty(dto.getType())){
            PeriodicReportType reportType = lookupService.findByTypeAndCode(PeriodicReportType.class, dto.getType());
            entity.setType(reportType);
        }

        // status
        if(dto.getId() == null){

            // TODO: status 'NEW'

            ReportStatus newReportStatus = new ReportStatus();
            newReportStatus.setId(1);
            entity.setStatus(newReportStatus);
        }
//        else if(StringUtils.isNotEmpty(dto.getStatus())){
//            ReportStatus status = lookupService.findByTypeAndCode(ReportStatus.class, dto.getStatus());
//            entity.setStatus(status);
//        }

        return entity;
    }

    @Override
    public PeriodicReportDto disassemble(PeriodicReport entity) {
        if (entity == null) {
            return null;
        }
        PeriodicReportDto dto = super.disassemble(entity);
        // type
        if(entity.getType() != null){
            dto.setType(entity.getType().getCode());
        }

        // status
        if(entity.getStatus() != null){
            dto.setStatus(entity.getStatus().getCode());
        }

        // creator
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }

        return dto;
    }
}
