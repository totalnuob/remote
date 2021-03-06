package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementChangesRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementChanges;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementChangesService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.converter.reporting.ReportingPEStatementChangesConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.StatementChangesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private ReportingPEStatementChangesConverter statementChangesConverter;

    @Override
    public ReportingPEStatementChanges assemble(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingPEStatementChanges entity = new ReportingPEStatementChanges();
        if(StringUtils.isEmpty(dto.getName())){
            String errorMessage = "Statement of Changes record name is missing. ";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }
        entity.setName(dto.getName());

        entity.setTrancheA(dto.getValues()[0]);
        entity.setTrancheB(dto.getValues()[1]);
        entity.setTrancheA2(dto.getValues()[2]);
        entity.setTrancheB2(dto.getValues()[3]);
        entity.setTotal(dto.getValues()[4]);

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
        try {
            if (entities != null) {
                this.peStatementChangesRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving Statement of Changes entities. ", ex);
            return false;
        }
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

    @Override
    public List<StatementChangesDto> getStatementChanges(Long reportId) {
        List<ReportingPEStatementChanges> entities = this.peStatementChangesRepository.getEntitiesByReportId(reportId,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        List<StatementChangesDto> dtoList = disassembleStatementChangesList(entities);
        return dtoList;
    }

    @Override
    public boolean deleteByReportId(Long reportId) {
        try {
            this.peStatementChangesRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting schedule of investments records with report id=" + reportId, ex);
            return false;
        }
    }

    @Override
    public boolean excludeIncludeTarragonRecord(Long recordId) {
        try {
            ReportingPEStatementChanges entity = peStatementChangesRepository.findOne(recordId);
            boolean value = entity.getExcludeFromTarragonCalculation() != null ? entity.getExcludeFromTarragonCalculation().booleanValue() : false;
            entity.setExcludeFromTarragonCalculation(!value);
            peStatementChangesRepository.save(entity);
            return true;
        }catch (Exception ex){
            logger.error("Error saving ReportingPEStatementChanges with id=" + recordId, ex);
            return false;
        }
    }

    private List<StatementChangesDto> disassembleStatementChangesList(List<ReportingPEStatementChanges> entities){
        List<StatementChangesDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(ReportingPEStatementChanges entity: entities){
                StatementChangesDto dto = this.statementChangesConverter.disassemble(entity);
                if(dto != null){
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }


    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementChanges> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null && !entities.isEmpty()){
            for(ReportingPEStatementChanges entity: entities) {

                Double[] values = {entity.getTrancheA(), entity.getTrancheB(), entity.getTrancheA2(), entity.getTrancheB2(), entity.getTotal()};
                ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false);
                records.add(recordDto);
            }
        }

        return records;
    }

}
