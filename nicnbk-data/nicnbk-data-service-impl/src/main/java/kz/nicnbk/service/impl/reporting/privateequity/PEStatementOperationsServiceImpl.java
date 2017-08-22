package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.util.ArrayUtils;
import kz.nicnbk.repo.api.lookup.PEOperationsTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementOperationsRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEOperationsType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementOperations;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementOperatinsService;
import kz.nicnbk.service.converter.reporting.PeriodReportConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.exception.ExcelFileParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by magzumov on 14.07.2017.
 */
@Service
public class PEStatementOperationsServiceImpl implements PEStatementOperatinsService {

    private static final Logger logger = LoggerFactory.getLogger(PEStatementOperationsServiceImpl.class);

    @Autowired
    private PEOperationsTypeRepository peOperationsTypeRepository;

    @Autowired
    private ReportingPEStatementOperationsRepository peStatementOperationsRepository;

    @Autowired
    private PeriodReportConverter periodReportConverter;

    @Override
    public ReportingPEStatementOperations assemble(ConsolidatedReportRecordDto dto, int tranche, Long reportId) {
        ReportingPEStatementOperations entity = new ReportingPEStatementOperations();
        entity.setName(dto.getName());
        entity.setTarragonMFTotal(dto.getValues()[0]);
        entity.setTarragonMFShareGP(dto.getValues()[1]);
        entity.setTarragonMFShareNICK(dto.getValues()[2]);
        entity.setTarragonLP(dto.getValues()[3]);
        entity.setConsolidationAdjustments(dto.getValues()[5]);

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        entity.setTranche(tranche);

        // balance type
        for(String classification: dto.getClassifications()){
            if(classification != null) {
                PEOperationsType balanceType = this.peOperationsTypeRepository.findByNameEn(classification.trim());
                if(balanceType != null){
                    entity.setType(balanceType);
                    //break;
                }
            }
        }
        if(entity.getType() == null){
            PEOperationsType balanceType = this.peOperationsTypeRepository.findByNameEn(dto.getName().trim());
            if(balanceType != null){
                entity.setType(balanceType);
                //break;
            }
        }

        if(entity.getType() == null){
            logger.error("Operations record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Income', 'Expenses', etc. Check for possible spaces in names.");
            throw new ExcelFileParseException("Operations record type could not be determined for record '" + entity.getName() +
                    "'. Expected values are 'Income', 'Expenses', etc. Check for possible spaces in names.");
        }

        return entity;
    }

    @Override
    public List<ReportingPEStatementOperations> assembleList(List<ConsolidatedReportRecordDto> dtoList, int tranche, Long reportId) {
        List<ReportingPEStatementOperations> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEStatementOperations entity = assemble(dto, tranche, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEStatementOperations> entities) {
        // TODO: boolean result, check for error?
        if(entities != null){
            peStatementOperationsRepository.save(entities);
        }
        return true;
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
        List<ReportingPEStatementOperations> entitiesTrancheA = this.peStatementOperationsRepository.getEntitiesByReportIdAndTranche(reportId, 1,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        List<ReportingPEStatementOperations> entitiesTrancheB = this.peStatementOperationsRepository.getEntitiesByReportIdAndTranche(reportId, 2,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<ConsolidatedReportRecordDto> trancheA = disassembleList(entitiesTrancheA);
        List<ConsolidatedReportRecordDto> trancheB = disassembleList(entitiesTrancheB);

        result.setOperationsTrancheA(trancheA);
        result.setOperationsTrancheB(trancheB);

        if(entitiesTrancheA != null && !entitiesTrancheA.isEmpty()) {
            result.setReport(periodReportConverter.disassemble(entitiesTrancheA.get(0).getReport()));
        }else if(entitiesTrancheB != null && !entitiesTrancheB.isEmpty()){
            result.setReport(periodReportConverter.disassemble(entitiesTrancheB.get(0).getReport()));
        }

        return result;
    }

    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementOperations> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null && !entities.isEmpty()){
            PEOperationsType currentType = null;

            Map<String, Double[]> totalSums = new HashMap<>();
            Double[] totals = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

            ReportingPEStatementOperations lastEntity = null;
            for(ReportingPEStatementOperations entity: entities) {

                boolean typeChange = currentType != null && entity.getType() != null &&
                        !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());

                // add total by type
                if(typeChange) {
                    records.add(buildNetRecord(currentType.getNameEn(), totalSums.get(currentType.getNameEn())));

                    PEOperationsType parent = currentType.getParent();
                    while(parent != null){

                        PEOperationsType entityParent = entity.getType().getParent();
                        boolean commonParent = false;
                        while(entityParent != null){
                            if(parent.getCode().equalsIgnoreCase(entityParent.getCode())){
                                commonParent = true;
                                break;
                            }
                            entityParent = entityParent.getParent();
                        }
                        if(!commonParent){
                            records.add(buildNetRecord(parent.getNameEn(), totalSums.get(parent.getNameEn())));
                        }
                        parent = parent.getParent();
                    }

                    //totalSums.put(currentType.getNameEn(), null);
                }

                // add headers by type
                if(typeChange || currentType == null){
                    // check if need header
                    Set<String> keySet = totalSums.keySet();
                    List<String> headers = new ArrayList<>();
                    if(keySet == null || !keySet.contains(entity.getType().getNameEn())){
                        // add headers
                        PEOperationsType parent = entity.getType();
                        while(parent != null){
                            if(!keySet.contains(parent.getNameEn())) {
                                headers.add(parent.getNameEn());
                            }
                            parent = parent.getParent();
                        }

                        for(int i = headers.size() - 1; i >= 0; i--){
                            records.add(new ConsolidatedReportRecordDto(headers.get(i), null, null, null, true, false));
                            totalSums.put(headers.get(i), new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
                        }
                    }
                }

                currentType = entity.getType();
                // add entity values to output
                Double total = null;
                if(entity.getTranche() == 1) {
                    total = entity.getTarragonMFShareNICK() != null && entity.getTarragonLP() != null ?
                            (entity.getTarragonMFShareNICK().doubleValue() + entity.getTarragonLP().doubleValue()) : null;
                }else if(entity.getTranche() == 2){
                    total = entity.getTarragonMFTotal() != null && entity.getTarragonLP() != null ?
                            (entity.getTarragonMFTotal().doubleValue() + entity.getTarragonLP().doubleValue()) : null;
                }

                Double trancheConsolidated = total != null && entity.getConsolidationAdjustments() != null ?
                        total + entity.getConsolidationAdjustments().doubleValue() : null;
                Double[] values = {entity.getTarragonMFTotal(), entity.getTarragonMFShareGP(), entity.getTarragonMFShareNICK(), entity.getTarragonLP(),
                        total, entity.getConsolidationAdjustments(), trancheConsolidated};
                records.add(new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false));

                // add entity values to sums by type
                PEOperationsType type = entity.getType();
                while(type != null){
                    Double[] sums = totalSums.get(type.getNameEn());
                    if (sums != null){
                        ArrayUtils.addArrayValues(sums, values);
                    }
                    type = type.getParent();
                }

                // add totals
                ArrayUtils.addArrayValues(totals, values);
                lastEntity = entity;
            }

            // last record sum by type
            if(lastEntity != null){
                PEOperationsType type = lastEntity.getType();
                while(type != null){
                    Double[] sums = totalSums.get(type.getNameEn());
                    if(sums != null){
                        records.add(buildNetRecord(type.getNameEn(), sums));
                    }
                    type = type.getParent();
                }
            }

            // NET INCREASE (DECREASE) IN PARTNER'S CAPITAL
            // TODO: refactor string literals
            if(entities != null && !entities.isEmpty()){
                records.add(new ConsolidatedReportRecordDto("Net increase (decrease) in partner's capital", null, totals, null, true, true));
            }

        }
        return records;
    }

    // TODO: refactor common
    private ConsolidatedReportRecordDto buildNetRecord(String name, Double[] totalSums){
        ConsolidatedReportRecordDto recordDtoTotal = new ConsolidatedReportRecordDto();
        recordDtoTotal.setName("Net " + name);
        recordDtoTotal.setValues(totalSums);
        recordDtoTotal.setHeader(true);
        recordDtoTotal.setTotalSum(true);
        return recordDtoTotal;
    }
}
