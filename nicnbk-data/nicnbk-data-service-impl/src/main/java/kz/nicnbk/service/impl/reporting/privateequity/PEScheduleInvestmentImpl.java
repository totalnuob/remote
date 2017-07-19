package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.repo.api.lookup.CurrencyRepository;
import kz.nicnbk.repo.api.lookup.PEInvestmentTypeRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEScheduleInvestmentRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEInvestmentType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import kz.nicnbk.service.api.reporting.privateequity.PEScheduleInvestmentService;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
@Service
public class PEScheduleInvestmentImpl implements PEScheduleInvestmentService {

    private static final Logger logger = LoggerFactory.getLogger(PEScheduleInvestmentImpl.class);

    @Autowired
    private PEInvestmentTypeRepository peInvestmentTypeRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ReportingPEScheduleInvestmentRepository peScheduleInvestmentRepository;

    @Autowired
    private PeriodReportConverter periodReportConverter;


    @Override
    public ReportingPEScheduleInvestment assemble(ConsolidatedReportRecordDto dto, int tranche, Long reportId) {

        ReportingPEScheduleInvestment entity = new ReportingPEScheduleInvestment();
        entity.setName(dto.getName());
        entity.setCapitalCommitments(dto.getValues()[0]);
        entity.setNetCost(dto.getValues()[1]);
        entity.setFairValue(dto.getValues()[2]);

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        entity.setTranche(tranche);

        // investment type
        for(String classification: dto.getClassifications()){
            if(!classification.equalsIgnoreCase("Private Equity Partnerships and Co-Investments")){
                if(classification != null) {
                    PEInvestmentType investmentType = this.peInvestmentTypeRepository.findByNameEn(classification.trim());
                    if(investmentType != null){
                        entity.setType(investmentType);
                        break;
                    }
                }
            }
        }
        if(entity.getType() == null){
            logger.error("Schedule of investment record type could not be determined for record '" + entity.getName() +
                    "'. Expected values are 'Private Equity Partnerships', 'CoInvestments', etc.");
            throw new ExcelFileParseException("Schedule of investment record type could not be determined for record '" + entity.getName() +
                    "'. Expected values are 'Private Equity Partnerships', 'CoInvestments', etc.");
        }

        // strategy
        if(entity.getType() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_PE_PARTNERSHIPS)) {
            for(String classification: dto.getClassifications()){
                if(classification != null && !classification.equalsIgnoreCase("Private Equity Partnerships and Co-Investments")
                        && !classification.equalsIgnoreCase(entity.getType().getNameEn())){
                    Strategy strategy = this.strategyRepository.findByNameEnAndGroupType(classification.trim(), Strategy.TYPE_PRIVATE_EQUITY);
                    if(strategy != null){
                        entity.setStrategy(strategy);
                    }
                }
            }
        }
        if(entity.getType().getCode().equalsIgnoreCase("PE_PARTN") && entity.getStrategy() == null){
            logger.error("Schedule of investment record strategy could not be determined for record '" + entity.getName() + "'");
            throw new ExcelFileParseException("Schedule of investment record strategy could not be determined for record '" + entity.getName() + "'");
        }


        // description
        if(entity.getType() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_COINVESTMENTS)) {
            for(String classification: dto.getClassifications()){
                if(classification != null && !classification.equalsIgnoreCase("Private Equity Partnerships and Co-Investments") &&
                        !classification.equalsIgnoreCase(entity.getType().getNameEn())){
                    entity.setDescription(classification.trim());
                }
            }

        }


        // currency
        if(dto.getCurrency() != null) {
            Currency currency = this.currencyRepository.findByCode(dto.getCurrency());
            entity.setCurrency(currency);
        }

        return entity;
    }

    @Override
    public List<ReportingPEScheduleInvestment> assembleList(List<ConsolidatedReportRecordDto> dtoList, int tranche, Long reportId) {
        List<ReportingPEScheduleInvestment> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingPEScheduleInvestment entity = assemble(dto, tranche, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingPEScheduleInvestment> entities) {
        // TODO: boolean result, check for error?
        if(entities != null){
            peScheduleInvestmentRepository.save(entities);
        }
        return true;
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {

        List<ReportingPEScheduleInvestment> entitiesTrancheA = this.peScheduleInvestmentRepository.getEntitiesByReportIdAndTranche(reportId, 1,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        List<ReportingPEScheduleInvestment> entitiesTrancheB = this.peScheduleInvestmentRepository.getEntitiesByReportIdAndTranche(reportId, 2,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<ConsolidatedReportRecordDto> trancheA = disassembleList(entitiesTrancheA);
        List<ConsolidatedReportRecordDto> trancheB = disassembleList(entitiesTrancheB);

        result.setTrancheA(trancheA);
        result.setTrancheB(trancheB);
        if(entitiesTrancheA != null && !entitiesTrancheA.isEmpty()) {
            result.setReport(periodReportConverter.disassemble(entitiesTrancheA.get(0).getReport()));
        }else if(entitiesTrancheB != null && !entitiesTrancheB.isEmpty()){
            result.setReport(periodReportConverter.disassemble(entitiesTrancheB.get(0).getReport()));
        }

        return result;
    }


    // TODO: refactor
    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEScheduleInvestment> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null){
            String investmentType = null;
            String strategy = null;
            String description = null;

            Double[] totalSums = new Double[]{0.0, 0.0, 0.0};
            Double[] totalSumsByInvestmentType = new Double[]{0.0, 0.0, 0.0};
            Double[] totalSumsByStrategy = new Double[]{0.0, 0.0, 0.0};
            Double[] totalSumsByDescription = new Double[]{0.0, 0.0, 0.0};

            int totalInvestmentsNumber = 0;

            for(ReportingPEScheduleInvestment entity: entities){

                boolean isPrivateEquityPartnerships = entity.getType() != null &&
                        entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_PE_PARTNERSHIPS);

                boolean isCoinvestments = entity.getType() != null &&
                        entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_COINVESTMENTS);

                boolean strategySwitch = strategy != null && entity.getStrategy() != null && isPrivateEquityPartnerships
                        && !entity.getStrategy().getNameEn().equalsIgnoreCase(strategy);

                boolean descriptionSwitch = description != null && entity.getDescription() != null && isCoinvestments
                        && !entity.getDescription().equalsIgnoreCase(description);

                boolean investmentTypeSwitch = investmentType != null && entity.getType() != null
                        &&!entity.getType().getNameEn().equalsIgnoreCase(investmentType);

                if((isPrivateEquityPartnerships && strategySwitch) || (investmentTypeSwitch && strategy != null)){
                    // add total by strategy
                    records.add(buildTotalRecord(strategy, totalSumsByStrategy));

                    strategy = entity.getStrategy() != null ? entity.getStrategy().getNameEn(): null;
                    totalSumsByStrategy = new Double[]{0.0, 0.0, 0.0};
                }

                if((isCoinvestments && descriptionSwitch) || (investmentTypeSwitch && description != null)){
                    // add total by description
                    records.add(buildTotalRecord(description, totalSumsByDescription));

                    description = entity.getDescription();
                    totalSumsByDescription= new Double[]{0.0, 0.0, 0.0};
                }

                if(investmentTypeSwitch){
                    // investment type switch, add total by investment type
                    totalSumsByInvestmentType[0] = null;
                    records.add(buildTotalRecord(investmentType, totalSumsByInvestmentType));


                    totalSumsByInvestmentType = new Double[]{0.0, 0.0, 0.0};
                    investmentType = entity.getType().getNameEn();
                }

                if(investmentType == null || investmentTypeSwitch){
                    investmentType = entity.getType() != null ? entity.getType().getNameEn() : null;

                    records.add(new ConsolidatedReportRecordDto(investmentType, null, null, null, true, false));

                    totalInvestmentsNumber ++;
                }

                if(isPrivateEquityPartnerships && (strategy == null || strategySwitch)){
                    strategy = entity.getStrategy() != null ? entity.getStrategy().getNameEn() : null;
                    records.add(new ConsolidatedReportRecordDto(strategy, null, null, null, true, false));
                }

                if(isCoinvestments && (description == null || descriptionSwitch)){
                    description = entity.getDescription();
                    records.add(new ConsolidatedReportRecordDto(description, null, null, null, true, false));
                }


                // values
                Double[] values = new Double[]{entity.getCapitalCommitments() != null ? entity.getCapitalCommitments().doubleValue() : null,
                        entity.getNetCost() != null ? entity.getNetCost().doubleValue() : null,
                        entity.getFairValue() != null ? entity.getFairValue().doubleValue() : null};
                ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false);
                records.add(recordDto);

                // update total sums
                updateTotalSums(totalSums, recordDto.getValues());

                updateTotalSums(totalSumsByInvestmentType, recordDto.getValues());

                if(entity.getType() != null && isPrivateEquityPartnerships) {
                    updateTotalSums(totalSumsByStrategy, recordDto.getValues());
                }

                if(entity.getType() != null && isCoinvestments) {
                    updateTotalSums(totalSumsByDescription, recordDto.getValues());
                }

            }
            if(strategy != null){
                records.add(buildTotalRecord(strategy, totalSumsByStrategy));
            }
            if(description != null){
                records.add(buildTotalRecord(description, totalSumsByDescription));
            }
            if(investmentType != null){
                totalSumsByInvestmentType[0] = null;
                records.add(buildTotalRecord(investmentType, totalSumsByInvestmentType));

            }
            if(totalInvestmentsNumber > 1){
                totalSums[0] = null;
                records.add(buildTotalRecord("Private Equity Partnerships and Co-Investments", totalSums));
            }
        }
        return records;
    }

    private ConsolidatedReportRecordDto buildTotalRecord(String name, Double[] totalSums){
        ConsolidatedReportRecordDto recordDtoTotal = new ConsolidatedReportRecordDto();
        recordDtoTotal.setName("Total " + name);
        recordDtoTotal.setValues(totalSums);
        recordDtoTotal.setHeader(true);
        recordDtoTotal.setTotalSum(true);
        return recordDtoTotal;
    }

    private void updateTotalSums(Double[] totalSums, Double[] values){
        totalSums[0] += values[0] != null ? values[0] : 0.0;
        totalSums[1] += values[1] != null ? values[1] : 0.0;
        totalSums[2] += values[2] != null ? values[2] : 0.0;
    }
}
