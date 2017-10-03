package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.PECashflowsTypeRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEStatementCashflowsRepository;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PECashflowsType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementCashflows;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.privateequity.PEStatementCashflowsService;
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
    private PeriodicReportConverter periodicReportConverter;

    @Autowired
    private PeriodicDataService periodicDataService;

    @Override
    public ReportingPEStatementCashflows assemble(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingPEStatementCashflows entity = new ReportingPEStatementCashflows();
        entity.setName(dto.getName());
        entity.setTrancheA(dto.getValues()[0]);
        entity.setTrancheB(dto.getValues()[1]);
        entity.setTotal(dto.getValues()[2]);

        // report
        entity.setReport(new PeriodicReport(reportId));

        // cashflows type
        String parentClassification = null;
        for(int i = 0; i < dto.getClassifications().length; i++){

            String classification = dto.getClassifications()[i];

            if(StringUtils.isNotEmpty(classification)) {
                PECashflowsType cashflowsType = this.peCashflowsTypeRepository.findByNameEnIgnoreCase(classification.trim());
                if(cashflowsType != null){
                    if(StringUtils.isNotEmpty(parentClassification)){
                        if(cashflowsType.getParent() == null){
                            logger.error("Parent type mismatch for cash flow type '" + classification + "': found '" + parentClassification + "', expected null.");
                            throw new ExcelFileParseException("Parent type mismatch for cash flow type '" + classification + "': found '" + parentClassification + "', expected null.");
                        }else if(!cashflowsType.getParent().getNameEn().equalsIgnoreCase(parentClassification)){
                            logger.error("Parent type mismatch for cash flow type '" + classification + "': found '" +
                                    parentClassification + "', expected '" + cashflowsType.getParent().getNameEn() + "'");
                            throw new ExcelFileParseException("Parent type mismatch for cash flow type '" + classification + "': found '" +
                                    parentClassification + "', expected '" + cashflowsType.getParent().getNameEn() + "'");
                        }else{
                            //
                        }
                    }
                    entity.setType(cashflowsType);
                    parentClassification = classification;
                    //break;
                }else{
                    logger.error("Cash flows type '" + classification +"'  could not be determined for record '" + entity.getName() + "'. Expected values are 'Cash flows from operating activities'," +
                            " 'Cash flows from financing activities' etc. Check for possible spaces in names.");
                    throw new ExcelFileParseException("Cash flows type '" + classification + "'  could not be determined for record '" + entity.getName() +
                            "'. Expected values are 'Cash flows from operating activities', 'Cash flows from financing activities', etc. Check for possible spaces in names.");
                }
            }
        }
        if(entity.getType() == null){
            PECashflowsType cashflowsType = this.peCashflowsTypeRepository.findByNameEnIgnoreCase(dto.getName().trim());
            if(cashflowsType != null){
                entity.setType(cashflowsType);
                //break;
            }
        }

        if(entity.getType() == null && !isNetSum(entity.getName())){
            logger.error("Cashflows record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Cash flows from operating activities'," +
                    " 'Cash flows from financing activities' etc.");
            throw new ExcelFileParseException("Cashflows record type could not be determined for record '" + entity.getName() +
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
            result.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
        }

        return result;
    }

    private boolean isNetSum(String name){
        return StringUtils.isNotEmpty(name) && (name.equalsIgnoreCase("Net increase (decrease) in cash and cash equivalents") ||
                name.equalsIgnoreCase("Cash and cash equivalents - beginning of period") ||
                name.equalsIgnoreCase("Cash and cash equivalents - end of period"));
    }

    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEStatementCashflows> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null && !entities.isEmpty()){
            PECashflowsType currentType = null;
            Map<String, Integer> levels = new HashMap<>();
            int level = 0;
            for(ReportingPEStatementCashflows entity: entities) {

                boolean typeChange = currentType != null && entity.getType() != null &&
                        !entity.getType().getCode().equalsIgnoreCase(currentType.getCode());

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
                        //records.add(buildNetRecord(biggestPreviousParent.getNameEn(), totalSums.get(biggestPreviousParent.getNameEn())));
                        level = 0;
                        records.add(new ConsolidatedReportRecordDto("", null, null, null, false, false));
                    }

                }else if((currentType != null && entity.getType() == null) || (currentType == null && entity.getType() != null)){
                    level = 0;
                    records.add(new ConsolidatedReportRecordDto("", null, null, null, false, false));
                }

                // add headers by type
                if(typeChange || currentType == null){
                    // check if need header
                    Set<String> keySet = levels.keySet();
                    List<String> headers = new ArrayList<>();
                    if(keySet == null || (entity.getType() != null && !keySet.contains(entity.getType().getNameEn()))){
                        // add headers
                        PECashflowsType parent = entity.getType();
                        while(parent != null){
                            if(!keySet.contains(parent.getNameEn())) {
                                headers.add(parent.getNameEn());
                            }
                            parent = parent.getParent();
                        }

                        for(int i = headers.size() - 1; i >= 0; i--){
                            PECashflowsType type = getCashflowTypeByName(headers.get(i));
                            if(type != null){
                                type = type.getParent();
                                while(type != null){
                                    Integer tempLevel = levels.get(type.getNameEn());
                                    if( tempLevel != null){
                                        level = tempLevel + 1;
                                        //level = tempLevel;
                                        break;
                                    }
                                    type = type.getParent();
                                }
                            }

                            ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(headers.get(i), null, null, null, true, false);
                            recordDto.setLevel(level);
                            records.add(recordDto);

                            levels.put(headers.get(i), level);

                        }
                    }
                }

                currentType = entity.getType();
                // add entity values to output
//                Double sum = entity.getTrancheA() != null && entity.getTrancheB() != null ? entity.getTrancheA().doubleValue() + entity.getTrancheB().doubleValue() :
//                        entity.getTrancheA() != null ? entity.getTrancheA().doubleValue() : entity.getTrancheB() != null ? entity.getTrancheB().doubleValue() : 0.0;
                Double[] values = {entity.getTrancheA(), entity.getTrancheB(), entity.getTotal()};

                boolean isTotal = isNetSum(entity.getName());
                int totalLevel = -1;
                if(!isTotal){
                    PECashflowsType type = entity.getType();
                    while(type != null){
                        if(entity.getName().equalsIgnoreCase("Net " + type.getNameEn())){
                            isTotal = true;
                            totalLevel = levels.get(type.getNameEn());
                            break;
                        }
                        type = type.getParent();
                    }
                }

                ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, isTotal, isTotal);
                recordDto.setLevel(entity.getType() != null && levels.get(entity.getType().getNameEn()) != null ? levels.get(entity.getType().getNameEn()) + 1 : 0);
                if(totalLevel != -1){
                    recordDto.setLevel(totalLevel);
                }
                records.add(recordDto);
            }
            records.add(new ConsolidatedReportRecordDto("", null, null, null, false, false));
        }
        return records;
    }

    private PECashflowsType getCashflowTypeByName(String nameEn){
        return this.peCashflowsTypeRepository.findByNameEnIgnoreCase(nameEn);
    }
}
