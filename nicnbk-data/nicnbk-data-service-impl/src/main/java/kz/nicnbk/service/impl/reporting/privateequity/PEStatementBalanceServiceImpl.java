package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.util.ArrayUtils;
import kz.nicnbk.repo.api.lookup.PEBalanceTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementBalanceRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementBalance;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementBalanceService;
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

    @Override
    public ReportingPEStatementBalance assemble(ConsolidatedReportRecordDto dto, int tranche, Long reportId) {
        ReportingPEStatementBalance entity = new ReportingPEStatementBalance();
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
                PEBalanceType balanceType = this.peBalanceTypeRepository.findByNameEn(classification.trim());
                if(balanceType != null){
                    entity.setType(balanceType);
                    //break;
                }
            }
        }
        if(entity.getType() == null){
            PEBalanceType balanceType = this.peBalanceTypeRepository.findByNameEn(dto.getName().trim());
            if(balanceType != null){
                entity.setType(balanceType);
                //break;
            }
        }

        if(entity.getType() == null){
            logger.error("Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.");
            throw new ExcelFileParseException("Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.");
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

        result.setTrancheA(trancheA);
        result.setTrancheB(trancheB);

        return result;
    }

    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementBalance> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null && !entities.isEmpty()){
            PEBalanceType currentType = null;

            Map<String, Double[]> totalSums = new HashMap<>();

            for(ReportingPEStatementBalance entity: entities) {

                boolean balanceTypeSwitch = currentType != null && entity.getType() != null
                        && !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());

                // add total by type
                if(balanceTypeSwitch) {
                    records.add(buildTotalRecord(currentType.getNameEn(), totalSums.get(currentType.getNameEn())));
                    //totalSums.put(currentType.getNameEn(), null);
                }

                // add headers by type
                if(balanceTypeSwitch || currentType == null){
                    // check if need header
                    Set<String> keySet = totalSums.keySet();
                    List<String> headers = new ArrayList<>();
                    if(keySet == null || !keySet.contains(entity.getType().getNameEn())){
                        // add headers
                        PEBalanceType parent = entity.getType();
                        while(parent != null){
                            headers.add(parent.getNameEn());
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
                PEBalanceType type = entity.getType();
                while(type != null){
                    Double[] sums = totalSums.get(type.getNameEn());
                    if (sums != null){
                        ArrayUtils.addArrayValues(sums, values);
                    }
                    type = type.getParent();
                }
            }

            // ADD TOTAL LIABILITIES AND PARTNERS CAPITAL
            // TODO: refactor string literals
            if(totalSums.get("Liabilities") != null && totalSums.get("Partners' capital") != null){
                Double[] totalValues = totalSums.get("Liabilities");
                ArrayUtils.addArrayValues(totalValues, totalSums.get("Partners' capital"));
                records.add(new ConsolidatedReportRecordDto("Total liabilities and partners' capital", null, totalValues, null, true, true));
            }

        }
        return records;
    }


    // TODO: refactor common
    private ConsolidatedReportRecordDto buildTotalRecord(String name, Double[] totalSums){
        ConsolidatedReportRecordDto recordDtoTotal = new ConsolidatedReportRecordDto();
        recordDtoTotal.setName("Total " + name);
        recordDtoTotal.setValues(totalSums);
        recordDtoTotal.setHeader(true);
        recordDtoTotal.setTotalSum(true);
        return recordDtoTotal;
    }
}
