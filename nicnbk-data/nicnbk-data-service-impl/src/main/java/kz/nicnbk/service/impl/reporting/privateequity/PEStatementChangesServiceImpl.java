package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementChangesRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementChanges;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementChangesService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by magzumov on 17.07.2017.
 */
@Service
public class PEStatementChangesServiceImpl implements PEStatementChangesService {

    private static final Logger logger = LoggerFactory.getLogger(PEStatementChangesServiceImpl.class);

    @Autowired
    private ReportingPEStatementChangesRepository peStatementChangesRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportingPEStatementChanges assemble(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingPEStatementChanges entity = new ReportingPEStatementChanges();
        entity.setName(dto.getName());

        entity.setTrancheA(dto.getValues()[0]);
        entity.setTrancheB(dto.getValues()[1]);
        entity.setTotal(dto.getValues()[2]);

        // report
        entity.setReport(new PeriodicReport(reportId));

        return entity;
    }

    @Override
    public List<ReportingPEStatementChanges> assembleList(List<ConsolidatedReportRecordDto> dtoList, Long reportId) {
        List<ReportingPEStatementChanges> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEStatementChanges entity = assemble(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEStatementChanges> entities) {
        // TODO: boolean result, check for error?
        if(entities != null){
            this.peStatementChangesRepository.save(entities);
        }
        return true;
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
        List<ReportingPEStatementChanges> entities = this.peStatementChangesRepository.getEntitiesByReportId(reportId,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<ConsolidatedReportRecordDto>  records = disassembleList(entities);

        result.setChanges(records);

        if(entities != null) {
            result.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
        }

        return result;
    }


    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementChanges> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null && !entities.isEmpty()){
            for(ReportingPEStatementChanges entity: entities) {

                Double[] values = {entity.getTrancheA(), entity.getTrancheB(), entity.getTotal()};
                ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false);
                records.add(recordDto);
            }
        }

        return records;
    }

}
