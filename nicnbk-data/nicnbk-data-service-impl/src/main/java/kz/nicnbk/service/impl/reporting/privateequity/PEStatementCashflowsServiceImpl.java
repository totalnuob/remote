package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.util.ArrayUtils;
import kz.nicnbk.repo.api.lookup.PECashflowsTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementCashflowsRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PECashflowsType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementCashflows;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementCashflowsService;
import kz.nicnbk.service.converter.reporting.PeriodReportConverter;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.PeriodicDataDto;
import kz.nicnbk.service.dto.reporting.exception.ExcelFileParseException;
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
public class PEStatementCashflowsServiceImpl implements PEStatementCashflowsService {

    private static final Logger logger = LoggerFactory.getLogger(PEStatementCashflowsServiceImpl.class);

    @Autowired
    private PECashflowsTypeRepository peCashflowsTypeRepository;

    @Autowired
    private ReportingPEStatementCashflowsRepository peStatementCashflowsRepository;

    @Autowired
    private PeriodReportConverter periodReportConverter;

    @Autowired
    private PeriodicDataService periodicDataService;

    @Override
    public ReportingPEStatementCashflows assemble(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingPEStatementCashflows entity = new ReportingPEStatementCashflows();
        entity.setName(dto.getName());
        entity.setTrancheA(dto.getValues()[0]);
        entity.setTrancheB(dto.getValues()[1]);

        // report
        entity.setReport(new PeriodicReport(reportId));

        // cashflows type
        for(String classification: dto.getClassifications()){
            if(classification != null) {
                PECashflowsType cashflowsType = this.peCashflowsTypeRepository.findByNameEn(classification.trim());
                if(cashflowsType != null){
                    entity.setType(cashflowsType);
                    //break;
                }
            }
        }
        if(entity.getType() == null){
            PECashflowsType cashflowsType = this.peCashflowsTypeRepository.findByNameEn(dto.getName().trim());
            if(cashflowsType != null){
                entity.setType(cashflowsType);
                //break;
            }
        }

        if(entity.getType() == null){
            logger.error("Operations record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Cash flows from operating activities'," +
                    " 'Cash flows from financing activities' etc.");
            throw new ExcelFileParseException("Operations record type could not be determined for record '" + entity.getName() +
                    "'. Expected values are 'Cash flows from operating activities', 'Cash flows from financing activities', etc.");
        }

        return entity;
    }

    @Override
    public List<ReportingPEStatementCashflows> assembleList(List<ConsolidatedReportRecordDto> dtoList, Long reportId) {
        List<ReportingPEStatementCashflows> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEStatementCashflows entity = assemble(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEStatementCashflows> entities) {
        // TODO: boolean result, check for error?
        if(entities != null){
            this.peStatementCashflowsRepository.save(entities);
        }
        return true;
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
        List<ReportingPEStatementCashflows> entities = this.peStatementCashflowsRepository.getEntitiesByReportId(reportId,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<ConsolidatedReportRecordDto>  records = disassembleList(entities);

        result.setCashflows(records);

        if(entities != null) {
            result.setReport(periodReportConverter.disassemble(entities.get(0).getReport()));
        }

        return result;
    }

//    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementCashflows> entities){
//        return null;
//    }

    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementCashflows> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null && !entities.isEmpty()){
            PECashflowsType currentType = null;

            Map<String, Double[]> totalSums = new HashMap<>();
            Double[] totals = new Double[]{0.0, 0.0, 0.0};

            ReportingPEStatementCashflows lastEntity = null;
            for(ReportingPEStatementCashflows entity: entities) {

                boolean typeChange = currentType != null && entity.getType() != null &&
                        !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());

                // add total by type
                if(typeChange){
                    PECashflowsType previousParent = currentType.getParent();
                    PECashflowsType biggestPreviousParent = currentType;
                    while(previousParent != null){
                        biggestPreviousParent = previousParent;
                        previousParent = previousParent.getParent();
                    }

                    PECashflowsType newParent = entity.getType().getParent();
                    PECashflowsType biggestNewParent = entity.getType();
                    while(newParent != null){
                        biggestNewParent = newParent;
                        newParent = newParent.getParent();
                    }

                    if(biggestPreviousParent != null && biggestNewParent != null &&
                            !biggestPreviousParent.getCode().equalsIgnoreCase(biggestNewParent.getCode())){
                        records.add(buildNetRecord(biggestPreviousParent.getNameEn(), totalSums.get(biggestPreviousParent.getNameEn())));
                    }

                }

                // add headers by type
                if(typeChange || currentType == null){
                    // check if need header
                    Set<String> keySet = totalSums.keySet();
                    List<String> headers = new ArrayList<>();
                    if(keySet == null || !keySet.contains(entity.getType().getNameEn())){
                        // add headers
                        PECashflowsType parent = entity.getType();
                        while(parent != null){
                            if(!keySet.contains(parent.getNameEn())) {
                                headers.add(parent.getNameEn());
                            }
                            parent = parent.getParent();
                        }

                        for(int i = headers.size() - 1; i >= 0; i--){
                            records.add(new ConsolidatedReportRecordDto(headers.get(i), null, null, null, true, false));
                            totalSums.put(headers.get(i), new Double[]{0.0, 0.0, 0.0});
                        }
                    }
                }

                currentType = entity.getType();
                // add entity values to output
                Double sum = entity.getTrancheA() != null && entity.getTrancheB() != null ? entity.getTrancheA().doubleValue() + entity.getTrancheB().doubleValue() :
                        entity.getTrancheA() != null ? entity.getTrancheA().doubleValue() : entity.getTrancheB() != null ? entity.getTrancheB().doubleValue() : 0.0;
                Double[] values = {entity.getTrancheA(), entity.getTrancheB(), sum};
                records.add(new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false));

                // add entity values to sums by type
                PECashflowsType type = entity.getType();
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
                PECashflowsType type = lastEntity.getType();
                while(type != null){
                    Double[] sums = totalSums.get(type.getNameEn());
                    if(sums != null){
                        records.add(buildNetRecord(type.getNameEn(), sums));
                    }
                    type = type.getParent();
                }
            }

            // NET AND CALCULATED VALUES
            // TODO: refactor string literals
            if(entities != null && !entities.isEmpty()){
                records.add(new ConsolidatedReportRecordDto("Net increase (decrease) in cash and cash equivalents", null, totals, null, true, true));

                PeriodicDataDto trancheA = this.periodicDataService.getCashflowBeginningPeriod(1);
                PeriodicDataDto trancheB = this.periodicDataService.getCashflowBeginningPeriod(2);
                Double trancheAValue = trancheA != null && trancheA.getValue() != null ? trancheA.getValue().doubleValue() : 0.0;
                Double trancheBValue = trancheB != null && trancheB.getValue() != null ? trancheB.getValue().doubleValue() : 0.0;
                Double[] beginningPeriod = {trancheAValue, trancheBValue, (trancheAValue + trancheBValue)};
                records.add(new ConsolidatedReportRecordDto("Cash and cash equivalents - beginning of period", null, beginningPeriod, null, true, true));

                Double[] endPeriod = {0.0, 0.0, 0.0};
                ArrayUtils.addArrayValues(endPeriod, totals);
                ArrayUtils.addArrayValues(endPeriod, beginningPeriod);
                records.add(new ConsolidatedReportRecordDto("Cash and cash equivalents - end of period", null, endPeriod, null, true, true));
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
