package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.CurrencyRepository;
import kz.nicnbk.repo.api.lookup.PEInvestmentTypeRepository;
import kz.nicnbk.repo.api.lookup.PETrancheTypeRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.api.reporting.privateequity.ReportingPEScheduleInvestmentRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEInvestmentType;
import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import kz.nicnbk.service.api.reporting.privateequity.PEScheduleInvestmentService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.converter.reporting.ReportingPEScheduleInvestmentConverter;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.ScheduleInvestmentsDto;
import kz.nicnbk.service.dto.reporting.UpdateTarragonInvestmentDto;
import kz.nicnbk.common.service.exception.ExcelFileParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PETrancheTypeRepository trancheTypeRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ReportingPEScheduleInvestmentRepository peScheduleInvestmentRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Autowired
    private ReportingPEScheduleInvestmentConverter scheduleInvestmentConverter;


    @Override
    public ReportingPEScheduleInvestment assemble(ConsolidatedReportRecordDto dto, int tranche, Long reportId) {

        ReportingPEScheduleInvestment entity = new ReportingPEScheduleInvestment();
        entity.setName(dto.getName());
        entity.setCapitalCommitments(dto.getValues()[0]);
        entity.setNetCost(dto.getValues()[1]);
        entity.setFairValue(dto.getValues()[2]);
        entity.setTotalSum(dto.isTotalSum());

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        entity.setTranche(tranche);

        // investment type
        for(String classification: dto.getClassifications()){
            if(classification != null) {
                PEInvestmentType investmentType = this.peInvestmentTypeRepository.findByNameEnIgnoreCase(classification.trim());
                if(investmentType != null){
                    entity.setType(investmentType);
                    break;
                }
            }
        }
        if(entity.getType() == null && !isTotalTypeSum(entity.getName())){
            String trancheName = tranche == 1 ? "[Tranche A]" : tranche == 2 ? "[Tranche B]" : "";
            logger.error(trancheName + " Schedule of investment record type could not be determined for '" + entity.getName() +
                    "'. Expected values are 'Fund Investments', 'Co-Investments', etc.  Check for possible spaces in names.");
            throw new ExcelFileParseException(trancheName + " Schedule of investment record type could not be determined for '" + entity.getName() +
                    "'. Expected values are 'Fund Investments', 'Co-Investments', etc.  Check for possible spaces in names.");
        }

        // strategy
//        String currencyName = null;
        if(entity.getType() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS) &&
                !isTotalTypeSum(entity.getName())) {
            for(String classification: dto.getClassifications()){
                if(classification != null && !classification.equalsIgnoreCase("Fund Investments and Co-Investments")
                        && !classification.equalsIgnoreCase(entity.getType().getNameEn())){
                    // strategy name cut off CURRENCY
//                    if(classification.trim().endsWith(" - USD") || classification.trim().endsWith(" - GBP")){
//                        classification = classification.substring(0, classification.length() - 6);
//                        currencyName = "USD";
//                    }else if(classification.trim().endsWith(" - GBP")){
//                        classification = classification.substring(0, classification.length() - 6);
//                        currencyName = "GBP";
//                    }else if(classification.trim().endsWith(" - Euro")){
//                        classification = classification.substring(0, classification.length() - 7);
//                        currencyName = "EUR";
//                    }

                    Strategy strategy = this.strategyRepository.findByNameEnAndGroupType(classification.trim(), Strategy.TYPE_PRIVATE_EQUITY);
                    if(strategy != null){
                        entity.setStrategy(strategy);
                    }
                }
            }
        }
        if(entity.getType() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS)
                && entity.getStrategy() == null && !isTotalTypeSum(entity.getName())){
            logger.error("Schedule of investment record strategy could not be determined for record '" + entity.getName() + "'");
            throw new ExcelFileParseException("Schedule of investment record strategy could not be determined for record '" + entity.getName() + "'");
        }


        // description
        if(entity.getType() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_COINVESTMENTS)) {
            for(String classification: dto.getClassifications()){
                if(classification != null && !classification.equalsIgnoreCase("Fund Investments and Co-Investments") &&
                        !classification.equalsIgnoreCase(entity.getType().getNameEn())){
                    entity.setDescription(dto.getName());
                    entity.setName(classification.trim());
                }
            }

        }


        // currency
        if(dto.getCurrency() != null) {
            Currency currency = this.currencyRepository.findByCode(dto.getCurrency());
            entity.setCurrency(currency);
        }
//        else if(currencyName != null){
//            Currency currency = this.currencyRepository.findByCode(currencyName);
//            entity.setCurrency(currency);
//        }

        return entity;
    }

    @Override
    public ReportingPEScheduleInvestment assemble(ScheduleInvestmentsDto dto, Long reportId) {

        ReportingPEScheduleInvestment entity = new ReportingPEScheduleInvestment();
        entity.setName(dto.getName());
        entity.setCapitalCommitments(dto.getCapitalCommitments());
        entity.setNetCost(dto.getNetCost());
        entity.setFairValue(dto.getFairValue());

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        if(dto.getTrancheType() != null){
            PETrancheType trancheType = this.trancheTypeRepository.findByNameEnIgnoreCase(dto.getTrancheType().getNameEn().trim());
            if(trancheType != null){
                entity.setTrancheType(trancheType);
            }
        }

        // investment type
        if(dto.getType() != null) {
            PEInvestmentType investmentType = this.peInvestmentTypeRepository.findByNameEnIgnoreCase(dto.getType().getNameEn().trim());
            if(investmentType != null){
                entity.setType(investmentType);
            }
        }

        if(entity.getType() == null){
            String errorMessage = " SOI Report record type could not be determined for '" + entity.getInvestment() +
                    "'. Expected values are 'Fund Investments', 'Co-Investments', etc.  Check for possible spaces in names.";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }

        // strategy
        if(entity.getStrategy() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS)) {
            Strategy strategy = this.strategyRepository.findByNameEnAndGroupType(dto.getStrategy().getNameEn().trim(), Strategy.TYPE_PRIVATE_EQUITY);
            if(strategy != null){
                entity.setStrategy(strategy);
            }
        }
        if(entity.getType() != null && entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS)
                && entity.getStrategy() == null && !isTotalTypeSum(entity.getName())){
            logger.error("Schedule of investment record strategy could not be determined for record '" + entity.getName() + "'");
            throw new ExcelFileParseException("Schedule of investment record strategy could not be determined for record '" + entity.getName() + "'");
        }


        // currency
        if(dto.getCurrency() != null && dto.getCurrency().getCode() != null) {
            Currency currency = this.currencyRepository.findByCode(dto.getCurrency().getCode());
            if(currency != null){
                entity.setCurrency(currency);
            }
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
    public List<ReportingPEScheduleInvestment> assembleList(List<ScheduleInvestmentsDto> dtoList, Long reportId) {
        List<ReportingPEScheduleInvestment> entities = new ArrayList<>();
        if(dtoList != null){
            for(ScheduleInvestmentsDto dto: dtoList){
                ReportingPEScheduleInvestment entity = this.scheduleInvestmentConverter.assemble(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ScheduleInvestmentsDto> dtoList, Long reportId){
        try {
            List<ReportingPEScheduleInvestment> entities = new ArrayList<>();
            if(dtoList != null){
                for(ScheduleInvestmentsDto dto: dtoList){
                    ReportingPEScheduleInvestment entity = this.scheduleInvestmentConverter.assemble(dto, reportId);
                    entities.add(entity);
                }
                if (entities != null) {
                    peScheduleInvestmentRepository.save(entities);
                }
            }
            return true;
        }catch (ExcelFileParseException ex){
            logger.error("Error saving SOI Report entities with ExcelFileParseException", ex);
            throw ex;
        }catch (Exception ex){
            logger.error("Error saving SOI Report entities", ex);
            return false;
        }
    }

    //@Transactional
    @Override
    public boolean save(List<ReportingPEScheduleInvestment> entities) {
        try {
            if (entities != null) {
                peScheduleInvestmentRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving Schedule of Investments entities", ex);
            return false;
        }
    }

    @Override
    public boolean save(ScheduleInvestmentsDto dto) {
        ReportingPEScheduleInvestment entity = this.scheduleInvestmentConverter.assemble(dto);
        if(entity != null){
            this.peScheduleInvestmentRepository.save(entity);
            return true;
        }
        return false;
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
            result.setReport(periodicReportConverter.disassemble(entitiesTrancheA.get(0).getReport()));
        }else if(entitiesTrancheB != null && !entitiesTrancheB.isEmpty()){
            result.setReport(periodicReportConverter.disassemble(entitiesTrancheB.get(0).getReport()));
        }

        return result;
    }

    private List<ConsolidatedReportRecordDto> disassembleList(List<ReportingPEScheduleInvestment> entities){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        if(entities != null){
            String investmentType = null;
            Strategy strategy = null;
            String name = null;
            for(ReportingPEScheduleInvestment entity: entities){
                boolean isPrivateEquityPartnerships = entity.getType() != null &&
                        entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS);

                boolean isCoinvestments = entity.getType() != null &&
                        entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_COINVESTMENTS);

                boolean strategySwitch = strategy != null && entity.getStrategy() != null && isPrivateEquityPartnerships &&
                        !entity.getStrategy().getNameEn().equalsIgnoreCase(strategy.getNameEn()) &&
                        // skip parent strategy header, e.g. Corporate Finance Buyout for Total Corporate Finance Buyout
                        (strategy.getParent() == null || !entity.getStrategy().getNameEn().equalsIgnoreCase(strategy.getParent().getNameEn()));

                boolean nameSwitch = name != null && entity.getName() != null && isCoinvestments
                        && !entity.getName().equalsIgnoreCase(name) && !entity.getName().equalsIgnoreCase("Total Co-Investments");

                boolean investmentTypeSwitch = investmentType != null && entity.getType() != null
                        &&!entity.getType().getNameEn().equalsIgnoreCase(investmentType);


                if(investmentType == null || investmentTypeSwitch){
                    investmentType = entity.getType() != null ? entity.getType().getNameEn() : null;
                    records.add(new ConsolidatedReportRecordDto(investmentType, null, null, null, true, false));
                }

                if(isPrivateEquityPartnerships && (strategy == null || strategySwitch)){
                    strategy = entity.getStrategy();
                    records.add(new ConsolidatedReportRecordDto(strategy != null ? strategy.getNameEn() : null, null, null, null, true, false));
                }

                if(isCoinvestments && (name == null || nameSwitch)){
                    name = entity.getName();
                    records.add(new ConsolidatedReportRecordDto(name, null, null, null, true, false));
                }

                records.add(disassemble(entity));
            }
        }
        return records;
    }

    private ConsolidatedReportRecordDto disassemble(ReportingPEScheduleInvestment entity){
        boolean isPrivateEquityPartnerships = entity.getType() != null &&
                entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS);

        boolean isCoinvestments = entity.getType() != null &&
                entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_COINVESTMENTS);

        // values
        Double[] values = new Double[]{entity.getCapitalCommitments() != null ? entity.getCapitalCommitments().doubleValue() : null,
                entity.getNetCost() != null ? entity.getNetCost().doubleValue() : null,
                entity.getFairValue() != null ? entity.getFairValue().doubleValue() : null};

        boolean isTotalStrategySum = isTotalTypeSum(entity.getName()) ||
                entity.getStrategy() != null && entity.getName().equalsIgnoreCase("Total " + entity.getStrategy().getNameEn());

        boolean isTotalDescriptionSum = isTotalTypeSum(entity.getName()) || (entity.getDescription() != null && entity.getDescription().equalsIgnoreCase("Total " + entity.getName()));

        String name = isPrivateEquityPartnerships ? entity.getName() : isCoinvestments ? (StringUtils.isNotEmpty(entity.getDescription()) ? entity.getDescription() : entity.getName() ): entity.getName();

        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(name, null, values, null, isTotalStrategySum || isTotalDescriptionSum, isTotalStrategySum || isTotalDescriptionSum);

        return recordDto;
    }


    @Override
    public List<ScheduleInvestmentsDto> getScheduleInvestments(Long reportId){
        List<ScheduleInvestmentsDto> records = new ArrayList<>();
//        List<ReportingPEScheduleInvestment> entitiesTrancheA =
//                this.peScheduleInvestmentRepository.getEntitiesByReportIdAndTranche(reportId, 1,new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//        if(entitiesTrancheA != null){
//            for(ReportingPEScheduleInvestment entity: entitiesTrancheA){
//                records.add(disassembleDto(entity));
//            }
//            //return records;
//        }
//        List<ReportingPEScheduleInvestment> entitiesTrancheB =
//                this.peScheduleInvestmentRepository.getEntitiesByReportIdAndTranche(reportId, 2, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//        if(entitiesTrancheB != null){
//            for(ReportingPEScheduleInvestment entity: entitiesTrancheB){
//                records.add(disassembleDto(entity));
//            }
//        }

        List<ReportingPEScheduleInvestment> entities = this.peScheduleInvestmentRepository.getEntitiesByReportId(reportId);
        for(ReportingPEScheduleInvestment entity: entities){
            records.add(disassembleDto(entity));
        }

        return records;
    }

    @Override
    public ConsolidatedReportRecordHolderDto getSOIReport(Long reportId){
        ConsolidatedReportRecordHolderDto recordHolderDto = new ConsolidatedReportRecordHolderDto();
        List<ScheduleInvestmentsDto> records = new ArrayList<>();
        List<ReportingPEScheduleInvestment> entities =
                this.peScheduleInvestmentRepository.findByReportId(reportId, new Sort(Sort.Direction.ASC, "id", "trancheType.id", "strategy"));
        if(entities != null && !entities.isEmpty()){
            for(ReportingPEScheduleInvestment entity: entities){
                records.add(this.scheduleInvestmentConverter.disassemble(entity));

            }
            recordHolderDto.setReport(this.periodicReportConverter.disassemble(entities.get(0).getReport()));

            recordHolderDto.setScheduleInvestments(records);

        }
        return recordHolderDto;
    }

    @Override
    public EntitySaveResponseDto updateScheduleInvestments(UpdateTarragonInvestmentDto updateDto) {
        EntitySaveResponseDto entitySaveResponseDto = new EntitySaveResponseDto();
        try {
            ReportingPEScheduleInvestment entity = this.peScheduleInvestmentRepository.getEntities(updateDto.getReportId(), updateDto.getTrancheTypeNameEn(), updateDto.getFundName());
            if (entity != null) {
                if (entity.getReport().getStatus().getCode().equalsIgnoreCase("SUBMITTED")) {
                    Long reportId = entity.getReport() != null ? entity.getReport().getId() : null;
                    logger.error("Cannot edit report with status 'SUBMITTED': report id " + reportId);
                    entitySaveResponseDto.setErrorMessageEn("Cannot edit report with status 'SUBMITTED': report id ");
                    return entitySaveResponseDto;
                }
                entity.setEditedFairValue(updateDto.getAccountBalance());
                this.peScheduleInvestmentRepository.save(entity);
                entitySaveResponseDto.setSuccessMessageEn("Successfully saved schedule of investment record");
                return entitySaveResponseDto;
            }else{
                logger.error("Failed to update schedule of investment record: No record found to update: fund name = " + updateDto.getFundName());
                entitySaveResponseDto.setErrorMessageEn("Failed to update schedule of investment record - no record found to update: fund name = " + updateDto.getFundName());
                return entitySaveResponseDto;
            }
        }catch (Exception ex){
            logger.error("Error updating schedule of investment record: fund name = " + updateDto.getFundName(), ex);
            entitySaveResponseDto.setErrorMessageEn("Error updating schedule of investment record: fund name = " + updateDto.getFundName());
            return entitySaveResponseDto;
        }
    }

    @Override
    public ScheduleInvestmentsDto getScheduleInvestments(Long reportId, String fundName, int tranche) {
        ReportingPEScheduleInvestment entity = this.peScheduleInvestmentRepository.getEntities(reportId, tranche, fundName);
        if(entity != null){
            return disassembleDto(entity);
        }
        return null;
    }

    @Transactional
    @Override
    public boolean deleteByReportId(Long reportId) {
        try {
            this.peScheduleInvestmentRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting schedule of investments records with report id=" + reportId);
            return false;
        }
    }

    @Override
    public boolean excludeIncludeTarragonRecord(Long recordId) {
        try {
            ReportingPEScheduleInvestment entity = peScheduleInvestmentRepository.findOne(recordId);
            boolean value = entity.getExcludeFromTarragonCalculation() != null ? entity.getExcludeFromTarragonCalculation().booleanValue() : false;
            entity.setExcludeFromTarragonCalculation(!value);
            peScheduleInvestmentRepository.save(entity);
            return true;
        }catch (Exception ex){
            logger.error("Error saving ReportingPEScheduleInvestment with id=" + recordId, ex);
            return false;
        }
    }

    private ScheduleInvestmentsDto disassembleDto(ReportingPEScheduleInvestment entity){
        if(entity != null){
            ScheduleInvestmentsDto dto = new ScheduleInvestmentsDto();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setCapitalCommitments(entity.getCapitalCommitments());
            dto.setFairValue(entity.getFairValue());
            dto.setNetCost(entity.getNetCost());
            dto.setTranche(entity.getTranche());
            dto.setDescription(entity.getDescription());
            dto.setTotalSum(entity.getTotalSum());
            dto.setEditedFairValue(entity.getEditedFairValue());

            if(entity.getCurrency() != null) {
                BaseDictionaryDto currency = new BaseDictionaryDto(entity.getCurrency().getCode(), entity.getCurrency().getNameEn(),
                        entity.getCurrency().getNameRu(), entity.getCurrency().getNameKz());
                dto.setCurrency(currency);
            }
            if(entity.getStrategy() != null) {
                BaseDictionaryDto strategy = new BaseDictionaryDto(entity.getStrategy().getCode(), entity.getStrategy().getNameEn(),
                        entity.getStrategy().getNameRu(), entity.getStrategy().getNameKz());
                dto.setStrategy(strategy);
            }
            if(entity.getType() != null){
                BaseDictionaryDto type = new BaseDictionaryDto(entity.getType().getCode(), entity.getType().getNameEn(),
                        entity.getType().getNameRu(), entity.getType().getNameKz());
                dto.setType(type);
            }
            if(entity.getTrancheType() != null){
                BaseDictionaryDto trancheType = new BaseDictionaryDto(entity.getTrancheType().getCode(), entity.getTrancheType().getNameEn(),
                        entity.getTrancheType().getNameRu(), entity.getTrancheType().getNameKz());
                dto.setTrancheType(trancheType);
            }

            dto.setReport(this.periodicReportConverter.disassemble(entity.getReport()));
            return dto;
        }
        return null;
    }

    private ReportingPEScheduleInvestment assembleDto(ScheduleInvestmentsDto dto){
        if(dto != null){
            ReportingPEScheduleInvestment entity = this.scheduleInvestmentConverter.assemble(dto);

            if(dto.getCurrency() != null && dto.getCurrency().getCode() != null) {
                Currency currency = this.currencyRepository.findByCode(dto.getCurrency().getCode());
                entity.setCurrency(currency);
            }
            if(dto.getStrategy() != null && dto.getStrategy().getNameEn() != null) {
                Strategy strategy = this.strategyRepository.findByNameEnAndGroupType(dto.getStrategy().getNameEn(), Strategy.TYPE_PRIVATE_EQUITY);
                entity.setStrategy(strategy);
            }
            if(dto.getType() != null && dto.getType().getNameEn() != null){
                PEInvestmentType type = this.peInvestmentTypeRepository.findByNameEnIgnoreCase(dto.getType().getNameEn());
                entity.setType(type);
            }
            if(dto.getTrancheType() != null && dto.getTrancheType().getNameEn() != null){
                PETrancheType trancheType = this.trancheTypeRepository.findByNameEnIgnoreCase(dto.getTrancheType().getNameEn());
                entity.setTrancheType(trancheType);
            }

            dto.setReport(this.periodicReportConverter.disassemble(entity.getReport()));
            return entity;
        }
        return null;
    }

    // TODO: refactor
//    private List<ConsolidatedReportRecordDto> _disassembleList(List<ReportingPEScheduleInvestment> entities){
//        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
//        if(entities != null){
//            String investmentType = null;
//            String strategy = null;
//            String description = null;
//
//            Double[] totalSums = new Double[]{0.0, 0.0, 0.0};
//            Double[] totalSumsByInvestmentType = new Double[]{0.0, 0.0, 0.0};
//            Double[] totalSumsByStrategy = new Double[]{0.0, 0.0, 0.0};
//            Double[] totalSumsByDescription = new Double[]{0.0, 0.0, 0.0};
//
//            int totalInvestmentsNumber = 0;
//
//            for(ReportingPEScheduleInvestment entity: entities){
//
//                boolean isPrivateEquityPartnerships = entity.getType() != null &&
//                        entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_FUND_INVESTMENTS);
//
//                boolean isCoinvestments = entity.getType() != null &&
//                        entity.getType().getNameEn().equalsIgnoreCase(ReportingPEScheduleInvestment.TYPE_COINVESTMENTS);
//
//                boolean strategySwitch = strategy != null && entity.getStrategy() != null && isPrivateEquityPartnerships
//                        && !entity.getStrategy().getNameEn().equalsIgnoreCase(strategy);
//
//                boolean descriptionSwitch = description != null && entity.getDescription() != null && isCoinvestments
//                        && !entity.getDescription().equalsIgnoreCase(description);
//
//                boolean investmentTypeSwitch = investmentType != null && entity.getType() != null
//                        &&!entity.getType().getNameEn().equalsIgnoreCase(investmentType);
//
//                if((isPrivateEquityPartnerships && strategySwitch) || (investmentTypeSwitch && strategy != null)){
//                    // add total by strategy
//                    records.add(buildTotalRecord(strategy, totalSumsByStrategy));
//
//                    strategy = entity.getStrategy() != null ? entity.getStrategy().getNameEn(): null;
//                    totalSumsByStrategy = new Double[]{0.0, 0.0, 0.0};
//                }
//
//                if((isCoinvestments && descriptionSwitch) || (investmentTypeSwitch && description != null)){
//                    // add total by description
//                    records.add(buildTotalRecord(description, totalSumsByDescription));
//
//                    description = entity.getDescription();
//                    totalSumsByDescription= new Double[]{0.0, 0.0, 0.0};
//                }
//
//                if(investmentTypeSwitch){
//                    // investment type switch, add total by investment type
//                    totalSumsByInvestmentType[0] = null;
//                    records.add(buildTotalRecord(investmentType, totalSumsByInvestmentType));
//
//
//                    totalSumsByInvestmentType = new Double[]{0.0, 0.0, 0.0};
//                    investmentType = entity.getType().getNameEn();
//                }
//
//                if(investmentType == null || investmentTypeSwitch){
//                    investmentType = entity.getType() != null ? entity.getType().getNameEn() : null;
//
//                    records.add(new ConsolidatedReportRecordDto(investmentType, null, null, null, true, false));
//
//                    totalInvestmentsNumber ++;
//                }
//
//                if(isPrivateEquityPartnerships && (strategy == null || strategySwitch)){
//                    strategy = entity.getStrategy() != null ? entity.getStrategy().getNameEn() : null;
//                    records.add(new ConsolidatedReportRecordDto(strategy, null, null, null, true, false));
//                }
//
//                if(isCoinvestments && (description == null || descriptionSwitch)){
//                    description = entity.getDescription();
//                    records.add(new ConsolidatedReportRecordDto(description, null, null, null, true, false));
//                }
//
//
//                // values
//                Double[] values = new Double[]{entity.getCapitalCommitments() != null ? entity.getCapitalCommitments().doubleValue() : null,
//                        entity.getNetCost() != null ? entity.getNetCost().doubleValue() : null,
//                        entity.getFairValue() != null ? entity.getFairValue().doubleValue() : null};
//                ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(entity.getName(), null, values, null, false, false);
//                records.add(recordDto);
//
//                // update total sums
//                updateTotalSums(totalSums, recordDto.getValues());
//
//                updateTotalSums(totalSumsByInvestmentType, recordDto.getValues());
//
//                if(entity.getType() != null && isPrivateEquityPartnerships) {
//                    updateTotalSums(totalSumsByStrategy, recordDto.getValues());
//                }
//
//                if(entity.getType() != null && isCoinvestments) {
//                    updateTotalSums(totalSumsByDescription, recordDto.getValues());
//                }
//
//            }
//            if(strategy != null){
//                records.add(buildTotalRecord(strategy, totalSumsByStrategy));
//            }
//            if(description != null){
//                records.add(buildTotalRecord(description, totalSumsByDescription));
//            }
//            if(investmentType != null){
//                totalSumsByInvestmentType[0] = null;
//                records.add(buildTotalRecord(investmentType, totalSumsByInvestmentType));
//
//            }
//            if(totalInvestmentsNumber > 1){
//                totalSums[0] = null;
//                records.add(buildTotalRecord("Fund Investments and Co-Investments", totalSums));
//            }
//        }
//        return records;
//    }
//
//    private ConsolidatedReportRecordDto buildTotalRecord(String name, Double[] totalSums){
//        ConsolidatedReportRecordDto recordDtoTotal = new ConsolidatedReportRecordDto();
//        recordDtoTotal.setName("Total " + name);
//        recordDtoTotal.setValues(totalSums);
//        recordDtoTotal.setHeader(true);
//        recordDtoTotal.setTotalSum(true);
//        return recordDtoTotal;
//    }
//
//    private void updateTotalSums(Double[] totalSums, Double[] values){
//        totalSums[0] += values[0] != null ? values[0] : 0.0;
//        totalSums[1] += values[1] != null ? values[1] : 0.0;
//        totalSums[2] += values[2] != null ? values[2] : 0.0;
//    }

    private boolean isTotalTypeSum(String name){
        return StringUtils.isNotEmpty(name) && (name.equalsIgnoreCase("Total Fund Investments") ||
                name.equalsIgnoreCase("Total Co-Investments") ||
                name.equalsIgnoreCase("Total Fund Investments and Co-Investments"));
    }
}
