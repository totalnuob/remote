package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.PEBalanceTypeRepository;
import kz.nicnbk.repo.api.lookup.PETrancheTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementBalanceRepository;
import kz.nicnbk.repo.model.lookup.BalanceTypeLookup;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementBalance;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementBalanceService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.converter.reporting.ReportingPEStatementBalanceConverter;
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
 * Created by magzumov on 01.07.2017.
 */
@Service
public class PEStatementBalanceServiceImpl implements PEStatementBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(PEStatementBalanceServiceImpl.class);

    @Autowired
    private ReportingPEStatementBalanceConverter statementBalanceConverter;

    @Autowired
    private PEBalanceTypeRepository peBalanceTypeRepository;

    @Autowired
    private PETrancheTypeRepository peTrancheTypeRepository;

    @Autowired
    private ReportingPEStatementBalanceRepository peStatementBalanceRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportingPEStatementBalance assemble(ConsolidatedReportRecordDto dto, String trancheTypeNameEn, Long reportId) {

        ReportingPEStatementBalance entity = new ReportingPEStatementBalance();
        entity.setName(dto.getName());
        if(StringUtils.isEmpty(entity.getName())){
            String errorMessage = "Statement Balance record name is missing.";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }
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

        // balance type

        if(isPartnersCapital(dto)){
            // Partners Capital variations
            PEBalanceType balanceType = this.peBalanceTypeRepository.findByCode(BalanceTypeLookup.PARTNERS_CAPITAL.getCode());
            if(balanceType != null){
                entity.setType(balanceType);
            }
//        }else if(isLiabilitiesAndPartnersCapital(dto)){
//            // Liabilities and Partners Capital variations
//            PEBalanceType balanceType = this.peBalanceTypeRepository.findByCode(BalanceTypeLookup.LIABILITIES_AND_PARTNERS_CAPITAL.getCode());
//            if(balanceType != null){
//                entity.setType(balanceType);
//            }
        }else {
            // Other
            for (int i = 0; i < dto.getClassifications().length; i++) {
                String classification = dto.getClassifications()[i];
                if (classification != null) {
                    PEBalanceType balanceType = this.peBalanceTypeRepository.findByNameEnIgnoreCase(classification.trim());
                    if (balanceType != null) {
                        entity.setType(balanceType);
                        //break;
                    }
                }
            }
        }

        if (entity.getType() == null) {
            PEBalanceType balanceType = this.peBalanceTypeRepository.findByNameEnIgnoreCase(dto.getName().trim());
            if (balanceType != null) {
                entity.setType(balanceType);
                //break;
            }
        }

        if(entity.getType() == null && !isTotalTypeSum(entity.getName()) && dto.isClassificationRequired()){
            logger.error("[" + trancheTypeNameEn + "]"  + " Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.  Check for possible spaces in names.");
            throw new ExcelFileParseException("[" + trancheTypeNameEn + "]" + " Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.  Check for possible spaces in names.");
        }

        return entity;
    }

    @Override
    public List<ReportingPEStatementBalance> assembleList(List<ConsolidatedReportRecordDto> dtoList, String trancheTypeNameEn, Long reportId) {
        List<ReportingPEStatementBalance> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEStatementBalance entity = assemble(dto, trancheTypeNameEn, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEStatementBalance> entities) {
        try {
            if (entities != null) {
                peStatementBalanceRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving Statement of Balance entities. ", ex);
            return false;
        }
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
        List<ReportingPEStatementBalance> entitiesTrancheA = this.peStatementBalanceRepository.getEntitiesByReportIdAndTranche(reportId, 1,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        List<ReportingPEStatementBalance> entitiesTrancheB = this.peStatementBalanceRepository.getEntitiesByReportIdAndTranche(reportId, 2,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<ConsolidatedReportRecordDto> trancheA = disassembleList(entitiesTrancheA);
        List<ConsolidatedReportRecordDto> trancheB = disassembleList(entitiesTrancheB);

        result.setBalanceTrancheA(trancheA);
        result.setBalanceTrancheB(trancheB);
        if(entitiesTrancheA != null && !entitiesTrancheA.isEmpty()) {
            result.setReport(periodicReportConverter.disassemble(entitiesTrancheA.get(0).getReport()));
        }else if(entitiesTrancheB != null && !entitiesTrancheB.isEmpty()){
            result.setReport(periodicReportConverter.disassemble(entitiesTrancheB.get(0).getReport()));
        }

        return result;
    }

    @Override
    public TarragonStatementBalanceOperationsHolderDto getStatementBalanceDto(Long reportId) {
        List<ReportingPEStatementBalance> entities = this.peStatementBalanceRepository.getEntitiesByReportIdOrderByIdAsc(reportId);
        TarragonStatementBalanceOperationsHolderDto holderDto = new TarragonStatementBalanceOperationsHolderDto();
        List<TarragonStatementBalanceOperationsTrancheDto> recordsByTranche = disassembleByTranche(entities);
        holderDto.setBalanceRecords(recordsByTranche);
        if(entities != null && !entities.isEmpty()) {
            holderDto.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
        }
        return holderDto;
    }

    @Override
    public boolean excludeIncludeTarragonRecord(Long recordId) {
        try {
            ReportingPEStatementBalance entity = peStatementBalanceRepository.findOne(recordId);
            boolean value = entity.getExcludeFromTarragonCalculation() != null ? entity.getExcludeFromTarragonCalculation().booleanValue() : false;
            entity.setExcludeFromTarragonCalculation(!value);
            peStatementBalanceRepository.save(entity);
            return true;
        }catch (Exception ex){
            logger.error("Error saving ReportingPEStatementBalance with id=" + recordId, ex);
            return false;
        }
    }

    @Override
    public boolean existEntityWithType(String code) {
        if(StringUtils.isEmpty(code)){
            return false;
        }
        int count = this.peStatementBalanceRepository.getEntitiesCountByType(code);
        return count > 0;
    }

    @Override
    public List<StatementBalanceOperationsDto> getStatementBalanceRecords(Long reportId) {
//        List<ReportingPEStatementBalance> entitiesTrancheA = this.peStatementBalanceRepository.getEntitiesByReportIdAndTranche(reportId, 1,
//                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//
//        List<ReportingPEStatementBalance> entitiesTrancheB = this.peStatementBalanceRepository.getEntitiesByReportIdAndTranche(reportId, 2,
//                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//
//        List<StatementBalanceOperationsDto> dtoListTrancheA = disassembleStatementBalanceList(entitiesTrancheA);
//        List<StatementBalanceOperationsDto> dtoListTrancheB = disassembleStatementBalanceList(entitiesTrancheB);


        List<ReportingPEStatementBalance> entities = this.peStatementBalanceRepository.getEntitiesByReportIdOrderByIdAsc(reportId);
        List<StatementBalanceOperationsDto> dtoList = disassembleStatementBalanceList(entities);

        return dtoList;
    }

    @Override
    public boolean deleteByReportId(Long reportId) {
        try {
            this.peStatementBalanceRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting schedule of investments records with report id=" + reportId);
            return false;
        }
    }

    private List<StatementBalanceOperationsDto> disassembleStatementBalanceList(List<ReportingPEStatementBalance> entities){
        List<StatementBalanceOperationsDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(ReportingPEStatementBalance entity: entities){
                StatementBalanceOperationsDto dto = this.statementBalanceConverter.disassemble(entity);
                if(dto != null){
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }

    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementBalance> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null){
            PEBalanceType currentType = null;
            Set<String> types = new HashSet<>();
            for(ReportingPEStatementBalance entity: entities){

                boolean balanceTypeSwitch = currentType != null && entity.getType() != null
                        && !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());

                // add headers by type
                if(balanceTypeSwitch || currentType == null){
                    // check if need header
                    List<String> headers = new ArrayList<>();
                    if(types == null || !types.contains(entity.getType().getNameEn())){
                        // add headers
                        PEBalanceType parent = entity.getType();
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


    private List<TarragonStatementBalanceOperationsTrancheDto> disassembleByTranche(List<ReportingPEStatementBalance> entities){
        Map<String, List<ConsolidatedReportRecordDto>> trancheMap = new HashMap<>();
        if(entities != null){
            PEBalanceType currentType = null;
            Set<String> types = new HashSet<>();
            for(ReportingPEStatementBalance entity: entities){

                boolean balanceTypeSwitch = currentType != null && entity.getType() != null
                        && !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());

                // add headers by type
                if(balanceTypeSwitch || currentType == null){
                    // check if need header
                    List<String> headers = new ArrayList<>();
                    if(types == null || !types.contains(entity.getType().getNameEn())){
                        // add headers
                        PEBalanceType parent = entity.getType();
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

    private ConsolidatedReportRecordDto disassemble(ReportingPEStatementBalance entity){
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

        boolean isTotalValue = entity.getName().equalsIgnoreCase("Total liabilities and partners' capital") ||
                (entity.getType() != null && entity.getName().equalsIgnoreCase("Total " + entity.getType().getNameEn()));

        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, isTotalValue, isTotalValue);
        return recordDto;
    }

    private boolean isTotalTypeSum(String name){
        return StringUtils.isNotEmpty(name) && (name.equalsIgnoreCase("Total liabilities and partners' capital"));
    }

    public static boolean isPartnersCapital(ConsolidatedReportRecordDto recordDto){
        return recordDto.hasClassification("PARTNERS' CAPITAL") ||
                recordDto.hasClassification("PARTNER'S CAPITAL") ||
                recordDto.hasClassification("PARTNERS CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("PARTNERS' CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("PARTNER'S CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("PARTNERS CAPITAL");
    }

    public static boolean isLiabilitiesAndPartnersCapital(ConsolidatedReportRecordDto recordDto){
        return recordDto.hasClassification("LIABILITIES AND PARTNERS' CAPITAL") ||
                recordDto.hasClassification("LIABILITIES AND PARTNER'S CAPITAL") ||
                recordDto.hasClassification("LIABILITIES AND PARTNERS CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("LIABILITIES AND PARTNERS' CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("LIABILITIES AND PARTNER'S CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("LIABILITIES AND PARTNERS CAPITAL");
    }
}
