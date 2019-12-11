package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.CurrencyRepository;
import kz.nicnbk.repo.api.lookup.PEInvestmentTypeRepository;
import kz.nicnbk.repo.api.lookup.PETrancheTypeRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITD;
import kz.nicnbk.repo.model.reporting.privateequity.PEInvestmentType;
import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.ScheduleInvestmentsDto;
import kz.nicnbk.service.dto.reporting.SingularityITDRecordDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Component
public class ReportingHFITDRecordConverter extends BaseDozerEntityConverter<ReportingHFITD, SingularityITDRecordDto> {

    private static final Logger logger = LoggerFactory.getLogger(ReportingHFITDRecordConverter.class);


    @Override
    public SingularityITDRecordDto disassemble(ReportingHFITD entity){
        SingularityITDRecordDto dto = super.disassemble(entity);
        PeriodicReportDto periodicReportDto = new PeriodicReportDto();
        periodicReportDto.setReportDate(entity.getReport().getReportDate());
        periodicReportDto.setId(entity.getReport().getId());
        dto.setPeriodicReport(periodicReportDto);
        return dto;
    }

    @Override
    public ReportingHFITD assemble(SingularityITDRecordDto dto) {
        ReportingHFITD entity = super.assemble(dto);
        entity.setReport(new PeriodicReport(dto.getPeriodicReport().getId()));
        return entity;
    }

    public List<SingularityITDRecordDto> disassembleList(List<ReportingHFITD> entities){
        List<SingularityITDRecordDto> dtoList = new ArrayList<>();
        for(ReportingHFITD entity: entities){
            dtoList.add(disassemble(entity));
        }
        return dtoList;
    }

    public List<ReportingHFITD> assembleList(List<SingularityITDRecordDto> dtoList){
        List<ReportingHFITD> entities = new ArrayList<>();
        for(SingularityITDRecordDto dto: dtoList){
            entities.add(assemble(dto));
        }
        return entities;
    }

}
