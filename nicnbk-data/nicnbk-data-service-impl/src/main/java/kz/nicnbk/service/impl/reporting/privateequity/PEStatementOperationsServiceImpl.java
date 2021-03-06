package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.PEOperationsTypeRepository;
import kz.nicnbk.repo.api.lookup.PETrancheTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementOperationsRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEOperationsType;
import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementOperations;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementOperationsService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.converter.reporting.ReportingPEStatementOperationsConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.StatementBalanceOperationsDto;
import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonStatementBalanceOperationsHolderDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonStatementBalanceOperationsTrancheDto;
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
public class PEStatementOperationsServiceImpl implements PEStatementOperationsService {

    private static final Logger logger = LoggerFactory.getLogger(PEStatementOperationsServiceImpl.class);

    @Autowired
    private PEOperationsTypeRepository peOperationsTypeRepository;

    @Autowired
    private PETrancheTypeRepository peTrancheTypeRepository;

    @Autowired
    private ReportingPEStatementOperationsRepository peStatementOperationsRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Autowired
    private ReportingPEStatementOperationsConverter statementOperationsConverter;

    @Override
    public ReportingPEStatementOperations assemble(ConsolidatedReportRecordDto dto, String trancheTypeNameEn, Long reportId) {
        String trancheName = trancheTypeNameEn;
        ReportingPEStatementOperations entity = new ReportingPEStatementOperations();
        entity.setName(dto.getName());
        entity.setTarragonMFTotal(dto.getValues()[0]);
        entity.setTarragonMFShareGP(dto.getValues()[1]);
        entity.setTarragonMFShareNICK(dto.getValues()[2]);
        entity.setTarragonLP(dto.getValues()[3]);
        entity.setNICKMFShareTotal(dto.getValues()[4]);
        entity.setConsolidationAdjustments(dto.getValues()[5]);
        entity.setNICKMFShareConsolidated(dto.getValues()[6]);
        entity.setTotalSum(dto.isTotalSum());

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        //entity.setTranche(tranche);
        if(trancheTypeNameEn != null){
            PETrancheType trancheType = this.peTrancheTypeRepository.findByNameEnIgnoreCase(trancheTypeNameEn);
            if(trancheType != null){
                entity.setTrancheType(trancheType);
            }else{
                String errorMessage = " Tranche type could not be determined for record '" + entity.getName() + "' (" + trancheTypeNameEn + ")";
                logger.error(errorMessage);
                throw new ExcelFileParseException(errorMessage);
            }
        }

        // operations type
        for(String classification: dto.getClassifications()){
            if(classification != null) {
                PEOperationsType operationsType = this.peOperationsTypeRepository.findByNameEnIgnoreCase(classification.trim());
                if(operationsType != null){
                    entity.setType(operationsType);
                    //break;
                }
            }
        }
        if(entity.getType() == null){
            PEOperationsType operationsType = this.peOperationsTypeRepository.findByNameEnIgnoreCase(dto.getName().trim());
            if(operationsType != null){
                entity.setType(operationsType);
                //break;
            }
        }

        if(entity.getType() == null && !isNetSum(entity.getName()) && dto.isClassificationRequired()){
            logger.error(trancheName + " Operations record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Income', 'Expenses', etc. Check for possible spaces in names.");
            throw new ExcelFileParseException(trancheName + " Operations record type could not be determined for record '" + entity.getName() +
                    "'. Expected values are 'Income', 'Expenses', etc. Check for possible spaces in names.");
        }

        return entity;
    }

    @Override
    public List<ReportingPEStatementOperations> assembleList(List<ConsolidatedReportRecordDto> dtoList, String trancheTypeNameEn, Long reportId) {
        List<ReportingPEStatementOperations> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEStatementOperations entity = assemble(dto, trancheTypeNameEn, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEStatementOperations> entities) {
        try {
            if (entities != null) {
                peStatementOperationsRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving Statement of Operations entities. ", ex);
            return false;
        }
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
            result.setReport(periodicReportConverter.disassemble(entitiesTrancheA.get(0).getReport()));
        }else if(entitiesTrancheB != null && !entitiesTrancheB.isEmpty()){
            result.setReport(periodicReportConverter.disassemble(entitiesTrancheB.get(0).getReport()));
        }

        return result;
    }

    @Override
    public TarragonStatementBalanceOperationsHolderDto getStatementOperations(Long reportId){
        List<ReportingPEStatementOperations> entities = this.peStatementOperationsRepository.getEntitiesByReportIdOrderByIdAsc(reportId);
        TarragonStatementBalanceOperationsHolderDto holderDto = new TarragonStatementBalanceOperationsHolderDto();
        List<TarragonStatementBalanceOperationsTrancheDto> recordsByTranche = disassembleByTranche(entities);
        holderDto.setOperationsRecords(recordsByTranche);
        if(entities != null && !entities.isEmpty()) {
            holderDto.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
        }
        return holderDto;
    }

    @Override
    public List<StatementBalanceOperationsDto> getStatementOperationsRecords(Long reportId) {
//        List<ReportingPEStatementOperations> entitiesTrancheA = this.peStatementOperationsRepository.getEntitiesByReportIdAndTranche(reportId, 1,
//                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//
//        List<ReportingPEStatementOperations> entitiesTrancheB = this.peStatementOperationsRepository.getEntitiesByReportIdAndTranche(reportId, 2,
//                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//
//        List<StatementBalanceOperationsDto> dtoListTrancheA = disassembleStatementOperationsList(entitiesTrancheA);
//        List<StatementBalanceOperationsDto> dtoListTrancheB = disassembleStatementOperationsList(entitiesTrancheB);
//        dtoListTrancheA.addAll(dtoListTrancheB);


        List<ReportingPEStatementOperations> entities = this.peStatementOperationsRepository.getEntitiesByReportIdOrderByIdAsc(reportId);
        List<StatementBalanceOperationsDto> dtoList= disassembleStatementOperationsList(entities);

        return dtoList;
    }

    @Override
    public boolean deleteByReportId(Long reportId) {
        try {
            this.peStatementOperationsRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting schedule of investments records with report id=" + reportId);
            return false;
        }
    }

    private List<StatementBalanceOperationsDto> disassembleStatementOperationsList(List<ReportingPEStatementOperations> entities){
        List<StatementBalanceOperationsDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(ReportingPEStatementOperations entity: entities){
                StatementBalanceOperationsDto dto = this.statementOperationsConverter.disassemble(entity);
                if(dto != null){
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }

    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementOperations> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null){
            PEOperationsType currentType = null;
            Set<String> types = new HashSet<>();
            for(ReportingPEStatementOperations entity: entities){
                boolean operationsTypeSwitch = currentType != null && entity.getType() != null
                        && !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());
                // add headers by type
                if(operationsTypeSwitch || currentType == null){
                    // check if need header
                    List<String> headers = new ArrayList<>();
                    if(types == null || (entity.getType() != null && !types.contains(entity.getType().getNameEn()))){
                        // add headers
                        PEOperationsType parent = entity.getType();
                        while(parent != null && !types.contains(parent.getNameEn())){
                            headers.add(parent.getNameEn());
                            parent = parent.getParent();
                        }

                        for(int i = headers.size() - 1; i >= 0; i--){
                            records.add(new ConsolidatedReportRecordDto(headers.get(i), null, null, null, true, false));
                            types.add(headers.get(i));
                        }
                    }
                }

                currentType = entity.getType();
                records.add(disassemble(entity));
            }
        }
        return records;
    }

    private List<TarragonStatementBalanceOperationsTrancheDto> disassembleByTranche(List<ReportingPEStatementOperations> entities){
        Map<String, List<ConsolidatedReportRecordDto>> trancheMap = new HashMap<>();
        if(entities != null){
            PEOperationsType currentType = null;
            Set<String> types = new HashSet<>();
            for(ReportingPEStatementOperations entity: entities){
                boolean operationsTypeSwitch = currentType != null && entity.getType() != null
                        && !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());
                // add headers by type
                if(operationsTypeSwitch || currentType == null){
                    // check if need header
                    List<String> headers = new ArrayList<>();
                    if(types == null || (entity.getType() != null && !types.contains(entity.getType().getNameEn()))){
                        // add headers
                        PEOperationsType parent = entity.getType();
                        while(parent != null && !types.contains(parent.getNameEn())){
                            headers.add(parent.getNameEn());
                            parent = parent.getParent();
                        }

                        for(int i = headers.size() - 1; i >= 0; i--){
                            if(trancheMap.get(entity.getTrancheType().getNameEn()) == null){
                                trancheMap.put(entity.getTrancheType().getNameEn(), new ArrayList<>());
                            }
                            trancheMap.get(entity.getTrancheType().getNameEn()).add(new ConsolidatedReportRecordDto(headers.get(i), null, null, null, true, false));
                            types.add(headers.get(i));
                        }
                    }
                }

                currentType = entity.getType();
                if(trancheMap.get(entity.getTrancheType().getNameEn()) == null){
                    trancheMap.put(entity.getTrancheType().getNameEn(), new ArrayList<>());
                }
                trancheMap.get(entity.getTrancheType().getNameEn()).add(disassemble(entity));
            }
        }
        List<TarragonStatementBalanceOperationsTrancheDto> resultRecords = new ArrayList<>();
        trancheMap.forEach((trancheName, values)->{
            TarragonStatementBalanceOperationsTrancheDto trancheDto = new TarragonStatementBalanceOperationsTrancheDto();
            trancheDto.setTrancheType(new BaseDictionaryDto(null, trancheName, null, null));
            trancheDto.setRecords(values);
            resultRecords.add(trancheDto);
        });
        return resultRecords;
    }

    public ConsolidatedReportRecordDto disassemble(ReportingPEStatementOperations entity){
        // values
//        Double total = null;
//        if(entity.getTranche() == 1) {
//            total = entity.getTarragonMFShareNICK() != null && entity.getTarragonLP() != null ?
//                    (entity.getTarragonMFShareNICK().doubleValue() + entity.getTarragonLP().doubleValue()) : null;
//        }else if(entity.getTranche() == 2){
//            total = entity.getTarragonMFTotal() != null && entity.getTarragonLP() != null ?
//                    (entity.getTarragonMFTotal().doubleValue() + entity.getTarragonLP().doubleValue()) : null;
//        }
//
//        Double trancheConsolidated = total != null && entity.getConsolidationAdjustments() != null ?
//                total + entity.getConsolidationAdjustments().doubleValue() : null;
        Double[] values = {entity.getTarragonMFTotal(), entity.getTarragonMFShareGP(), entity.getTarragonMFShareNICK(), entity.getTarragonLP(),
                /*total*/entity.getNICKMFShareTotal(), entity.getConsolidationAdjustments(), /*trancheConsolidated*/ entity.getNICKMFShareConsolidated()};

        boolean isTotalValue = (entity.getType() != null &&
                (entity.getName().equalsIgnoreCase("Total " + entity.getType().getNameEn()) ||
                        entity.getName().equalsIgnoreCase("Net " + entity.getType().getNameEn()))) ||
                entity.getName().equalsIgnoreCase("Net increase (decrease) in partners' capital resulting from operations");

        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, isTotalValue, isTotalValue);
        return recordDto;
    }

    private boolean isNetSum(String name){
        return StringUtils.isNotEmpty(name) && (name.equalsIgnoreCase("Net increase (decrease) in partners' capital resulting from operations"));
    }

    @Override
    public boolean existEntityWithType(String code) {
        int count = this.peStatementOperationsRepository.getEntitiesCountByType(code);
        return count > 0;
    }

    @Override
    public boolean excludeIncludeTarragonRecord(Long recordId) {
        try {
            ReportingPEStatementOperations entity = peStatementOperationsRepository.findOne(recordId);
            boolean value = entity.getExcludeFromTarragonCalculation() != null ? entity.getExcludeFromTarragonCalculation().booleanValue() : false;
            entity.setExcludeFromTarragonCalculation(!value);
            peStatementOperationsRepository.save(entity);
            return true;
        }catch (Exception ex){
            logger.error("Error saving ReportingPEStatementOperations with id=" + recordId, ex);
            return false;
        }
    }

    // TODO: refactor
//    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementOperations> entities){
//        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
//        if(entities != null && !entities.isEmpty()){
//            PEOperationsType currentType = null;
//
//            Map<String, Double[]> totalSums = new HashMap<>();
//            Double[] totals = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
//
//            ReportingPEStatementOperations lastEntity = null;
//            for(ReportingPEStatementOperations entity: entities) {
//
//                boolean typeChange = currentType != null && entity.getType() != null &&
//                        !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());
//
//                // add total by type
//                if(typeChange) {
//                    records.add(buildNetRecord(currentType.getNameEn(), totalSums.get(currentType.getNameEn())));
//
//                    PEOperationsType parent = currentType.getParent();
//                    while(parent != null){
//
//                        PEOperationsType entityParent = entity.getType().getParent();
//                        boolean commonParent = false;
//                        while(entityParent != null){
//                            if(parent.getCode().equalsIgnoreCase(entityParent.getCode())){
//                                commonParent = true;
//                                break;
//                            }
//                            entityParent = entityParent.getParent();
//                        }
//                        if(!commonParent){
//                            records.add(buildNetRecord(parent.getNameEn(), totalSums.get(parent.getNameEn())));
//                        }
//                        parent = parent.getParent();
//                    }
//
//                    //totalSums.put(currentType.getNameEn(), null);
//                }
//
//                // add headers by type
//                if(typeChange || currentType == null){
//                    // check if need header
//                    Set<String> keySet = totalSums.keySet();
//                    List<String> headers = new ArrayList<>();
//                    if(keySet == null || !keySet.contains(entity.getType().getNameEn())){
//                        // add headers
//                        PEOperationsType parent = entity.getType();
//                        while(parent != null){
//                            if(!keySet.contains(parent.getNameEn())) {
//                                headers.add(parent.getNameEn());
//                            }
//                            parent = parent.getParent();
//                        }
//
//                        for(int i = headers.size() - 1; i >= 0; i--){
//                            records.add(new ConsolidatedReportRecordDto(headers.get(i), null, null, null, true, false));
//                            totalSums.put(headers.get(i), new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
//                        }
//                    }
//                }
//
//                currentType = entity.getType();
//                // add entity values to output
//                Double total = null;
//                if(entity.getTranche() == 1) {
//                    total = entity.getTarragonMFShareNICK() != null && entity.getTarragonLP() != null ?
//                            (entity.getTarragonMFShareNICK().doubleValue() + entity.getTarragonLP().doubleValue()) : null;
//                }else if(entity.getTranche() == 2){
//                    total = entity.getTarragonMFTotal() != null && entity.getTarragonLP() != null ?
//                            (entity.getTarragonMFTotal().doubleValue() + entity.getTarragonLP().doubleValue()) : null;
//                }
//
//                Double trancheConsolidated = total != null && entity.getConsolidationAdjustments() != null ?
//                        total + entity.getConsolidationAdjustments().doubleValue() : null;
//                Double[] values = {entity.getTarragonMFTotal(), entity.getTarragonMFShareGP(), entity.getTarragonMFShareNICK(), entity.getTarragonLP(),
//                        total, entity.getConsolidationAdjustments(), trancheConsolidated};
//                records.add(new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false));
//
//                // add entity values to sums by type
//                PEOperationsType type = entity.getType();
//                while(type != null){
//                    Double[] sums = totalSums.get(type.getNameEn());
//                    if (sums != null){
//                        ArrayUtils.addArrayValues(sums, values);
//                    }
//                    type = type.getParent();
//                }
//
//                // add totals
//                ArrayUtils.addArrayValues(totals, values);
//                lastEntity = entity;
//            }
//
//            // last record sum by type
//            if(lastEntity != null){
//                PEOperationsType type = lastEntity.getType();
//                while(type != null){
//                    Double[] sums = totalSums.get(type.getNameEn());
//                    if(sums != null){
//                        records.add(buildNetRecord(type.getNameEn(), sums));
//                    }
//                    type = type.getParent();
//                }
//            }
//
//            // NET INCREASE (DECREASE) IN PARTNERS' CAPITAL
//            // TODO: refactor string literals
//            if(entities != null && !entities.isEmpty()){
//                records.add(new ConsolidatedReportRecordDto("Net increase (decrease) in partners' capital", null, totals, null, true, true));
//            }
//
//        }
//        return records;
//    }
//
//    // TODO: refactor common
//    private ConsolidatedReportRecordDto buildNetRecord(String name, Double[] totalSums){
//        ConsolidatedReportRecordDto recordDtoTotal = new ConsolidatedReportRecordDto();
//        recordDtoTotal.setName("Net " + name);
//        recordDtoTotal.setValues(totalSums);
//        recordDtoTotal.setHeader(true);
//        recordDtoTotal.setTotalSum(true);
//        return recordDtoTotal;
//    }
}
