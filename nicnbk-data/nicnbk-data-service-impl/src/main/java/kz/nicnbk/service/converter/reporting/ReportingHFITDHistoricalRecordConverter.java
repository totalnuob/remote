package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITDHistorical;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.SingularityITDHistoricalRecordDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportingHFITDHistoricalRecordConverter extends BaseDozerEntityConverter<ReportingHFITDHistorical, SingularityITDHistoricalRecordDto> {

    private static final Logger logger = LoggerFactory.getLogger(ReportingHFITDHistoricalRecordConverter.class);


    @Override
    public SingularityITDHistoricalRecordDto disassemble(ReportingHFITDHistorical entity){
        SingularityITDHistoricalRecordDto dto = super.disassemble(entity);
        PeriodicReportDto periodicReportDto = new PeriodicReportDto();
        periodicReportDto.setReportDate(entity.getReport().getReportDate());
        periodicReportDto.setId(entity.getReport().getId());
        dto.setPeriodicReport(periodicReportDto);
        return dto;
    }

    @Override
    public ReportingHFITDHistorical assemble(SingularityITDHistoricalRecordDto dto) {
        ReportingHFITDHistorical entity = super.assemble(dto);
        entity.setReport(new PeriodicReport(dto.getPeriodicReport().getId()));
        return entity;
    }

    public List<SingularityITDHistoricalRecordDto> disassembleList(List<ReportingHFITDHistorical> entities){
        List<SingularityITDHistoricalRecordDto> dtoList = new ArrayList<>();
        for(ReportingHFITDHistorical entity: entities){
            dtoList.add(disassemble(entity));
        }
        return dtoList;
    }

    public List<ReportingHFITDHistorical> assembleList(List<SingularityITDHistoricalRecordDto> dtoList, Long reportId){
        List<ReportingHFITDHistorical> entities = new ArrayList<>();
        for(SingularityITDHistoricalRecordDto dto: dtoList){
            dto.setPeriodicReport(new PeriodicReportDto(reportId));
            entities.add(assemble(dto));
        }
        return entities;
    }

}
