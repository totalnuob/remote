package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.PEBalanceTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementBalanceRepository;
import kz.nicnbk.repo.model.lookup.BalanceTypeLookup;
import kz.nicnbk.repo.model.lookup.InvestmentTypeLookup;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementBalance;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementBalanceService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
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
 * Created by magzumov on 01.07.2017.
 */
@Service
public class PEStatementBalanceServiceImpl implements PEStatementBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(PEStatementBalanceServiceImpl.class);

    @Autowired
    private PEBalanceTypeRepository peBalanceTypeRepository;

    @Autowired
    private ReportingPEStatementBalanceRepository peStatementBalanceRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Override
    public ReportingPEStatementBalance assemble(ConsolidatedReportRecordDto dto, int tranche, Long reportId) {

        String trancheName = tranche == 1 ? "[Tranche A]" : tranche == 2 ? "[Tranche B]" : "";
        ReportingPEStatementBalance entity = new ReportingPEStatementBalance();
        entity.setName(dto.getName());
        entity.setTarragonMFTotal(dto.getValues()[0]);
        entity.setTarragonMFShareGP(dto.getValues()[1]);
        entity.setTarragonMFShareNICK(dto.getValues()[2]);
        entity.setTarragonLP(dto.getValues()[3]);
        entity.setNICKMFShareTotal(dto.getValues()[4]);
        entity.setConsolidationAdjustments(dto.getValues()[5]);
        entity.setNICKMFShareConsolidated(dto.getValues()[6]);

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        entity.setTranche(tranche);

        // balance type

        if(isPartnersCapital(dto)){
            // Partners Capital variations
            PEBalanceType balanceType = this.peBalanceTypeRepository.findByCode(BalanceTypeLookup.PARTNERS_CAPITAL.getCode());
            if(balanceType != null){
                entity.setType(balanceType);
            }
        }else if(isLiabilitiesAndPartnersCapital(dto)){
            // Liabilities and Partners Capital variations
            PEBalanceType balanceType = this.peBalanceTypeRepository.findByCode(BalanceTypeLookup.LIABILITIES_AND_PARTNERS_CAPITAL.getCode());
            if(balanceType != null){
                entity.setType(balanceType);
            }
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
            logger.error(trancheName + " Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.  Check for possible spaces in names.");
            throw new ExcelFileParseException(trancheName + " Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.  Check for possible spaces in names.");
        }

        return entity;
    }

    @Override
    public List<ReportingPEStatementBalance> assembleList(List<ConsolidatedReportRecordDto> dtoList, int tranche, Long reportId) {
        List<ReportingPEStatementBalance> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEStatementBalance entity = assemble(dto, tranche, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEStatementBalance> entities) {
        // TODO: boolean result, check for error?
        if(entities != null){
            peStatementBalanceRepository.save(entities);
        }
        return true;
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

    // TODO: refactor
//    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementBalance> entities){
//        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
//        if(entities != null && !entities.isEmpty()){
//            PEBalanceType currentType = null;
//
//            Map<String, Double[]> totalSums = new HashMap<>();
//
//            for(ReportingPEStatementBalance entity: entities) {
//
//                boolean balanceTypeSwitch = currentType != null && entity.getType() != null
//                        && !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());
//
//                // add total by type
//                if(balanceTypeSwitch) {
//                    records.add(buildTotalRecord(currentType.getNameEn(), totalSums.get(currentType.getNameEn())));
//                    //totalSums.put(currentType.getNameEn(), null);
//                }
//
//                // add headers by type
//                if(balanceTypeSwitch || currentType == null){
//                    // check if need header
//                    Set<String> keySet = totalSums.keySet();
//                    List<String> headers = new ArrayList<>();
//                    if(keySet == null || !keySet.contains(entity.getType().getNameEn())){
//                        // add headers
//                        PEBalanceType parent = entity.getType();
//                        while(parent != null){
//                            headers.add(parent.getNameEn());
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
//                PEBalanceType type = entity.getType();
//                while(type != null){
//                    Double[] sums = totalSums.get(type.getNameEn());
//                    if (sums != null){
//                        ArrayUtils.addArrayValues(sums, values);
//                    }
//                    type = type.getParent();
//                }
//            }
//
//            // ADD TOTAL LIABILITIES AND PARTNERS CAPITAL
//            // TODO: refactor string literals
//            if(totalSums.get("Liabilities") != null && totalSums.get("Partners' capital") != null){
//                Double[] sumArray = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
//                ArrayUtils.addArrayValues(sumArray, totalSums.get("Liabilities"));
//                ArrayUtils.addArrayValues(sumArray, totalSums.get("Partners' capital"));
//                records.add(new ConsolidatedReportRecordDto("Total liabilities and partners' capital", null, sumArray, null, true, true));
//            }
//
//        }
//        return records;
//    }
//
//
//    // TODO: refactor common
//    private ConsolidatedReportRecordDto buildTotalRecord(String name, Double[] totalSums){
//        ConsolidatedReportRecordDto recordDtoTotal = new ConsolidatedReportRecordDto();
//        recordDtoTotal.setName("Total " + name);
//        recordDtoTotal.setValues(totalSums);
//        recordDtoTotal.setHeader(true);
//        recordDtoTotal.setTotalSum(true);
//        return recordDtoTotal;
//    }
}
