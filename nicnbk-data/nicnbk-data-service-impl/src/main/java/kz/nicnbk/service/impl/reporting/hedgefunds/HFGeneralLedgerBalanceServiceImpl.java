package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.HFChartOfAccountsTypeRepository;
import kz.nicnbk.repo.api.lookup.HFFinancialStatementTypeRepository;
import kz.nicnbk.repo.api.reporting.hedgefunds.ReportingHFGeneralLedgerBalanceRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFGeneralLedgerBalance;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import kz.nicnbk.service.impl.reporting.lookup.GeneralLedgerFinancialStatementCategoryLookup;
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
public class HFGeneralLedgerBalanceServiceImpl implements HFGeneralLedgerBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(HFGeneralLedgerBalanceServiceImpl.class);

    @Autowired
    private ReportingHFGeneralLedgerBalanceRepository generalLedgerBalanceRepository;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private HFFinancialStatementTypeRepository financialStatementTypeRepository;

    @Autowired
    private HFChartOfAccountsTypeRepository chartOfAccountsTypeRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Autowired
    private PeriodicReportService periodicReportService;

    private String convertFinancialStatementCategory(String category){
        if(StringUtils.isEmpty(category)){
            return null;
        }
        if(category.equalsIgnoreCase(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getNameEn())){
            return "A";
        }else if(category.equalsIgnoreCase(GeneralLedgerFinancialStatementCategoryLookup.LIABILITY.getNameEn()) ||
                category.equalsIgnoreCase("Liabilities")){
            return "L";
        }else if(category.equalsIgnoreCase(GeneralLedgerFinancialStatementCategoryLookup.EQUITY.getNameEn())){
            return "E";
        }else if(category.equalsIgnoreCase("Investment Expense")){
            return "X";
        }else if(category.equalsIgnoreCase("Investment Income")){
            return "I";
        }
        return category;
    }

    @Override
    public ReportingHFGeneralLedgerBalance assemble(SingularityGeneralLedgerBalanceRecordDto dto, Long reportId) {
        ReportingHFGeneralLedgerBalance entity = new ReportingHFGeneralLedgerBalance();
        entity.setTranche(getTranche(dto.getAcronym()));
        entity.setBalanceDate(dto.getBalanceDate());

        if(dto.getFinancialStatementCategory() != null) {
            String financialStatementCategory = convertFinancialStatementCategory(dto.getFinancialStatementCategory());
            entity.setFinancialStatementCategory(this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, financialStatementCategory));
        }
        if(entity.getFinancialStatementCategory() == null){
            logger.error("Error parsing 'Singularity General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
            throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
        }

        if(dto.getFinancialStatementCategoryDescription() != null){
            entity.setChartAccountsType(this.chartOfAccountsTypeRepository.findByNameEnIgnoreCase(dto.getFinancialStatementCategoryDescription().trim()));
        }
        if(entity.getChartAccountsType() == null){
            String errorMessage = "Error parsing 'Singularity General Ledger Balance' file: chart of accounts type could not be determined - '" + dto.getFinancialStatementCategoryDescription() + "'";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }

        entity.setGLAccount(dto.getGLAccount());
        entity.setChartAccountsLongDescription(dto.getChartAccountsLongDescription());
        if(StringUtils.isEmpty(entity.getChartAccountsLongDescription())){
            String errorMessage = "Error parsing 'Singularity General Ledger Balance' file: chart of accounts Long description could not be determined - '" + dto.getChartAccountsLongDescription() + "'";
            logger.error(errorMessage);
            throw new ExcelFileParseException(errorMessage);
        }
        entity.setShortName(dto.getShortName());
        entity.setGLAccountBalance(dto.getGLAccountBalance());

        if(dto.getSegValCCY() != null){
            entity.setSegValCCY(lookupService.findByTypeAndCode(Currency.class, dto.getSegValCCY()));
            if(entity.getSegValCCY() == null){
                logger.error("Error parsing 'Singularity General Ledger Balance' file: seg val ccy could not be determined - '" + dto.getSegValCCY() + "'");
                throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: seg val ccy type could not be determined - '" + dto.getSegValCCY() + "'");
            }
        }
        if(dto.getFundCCY() != null){
            entity.setFundCCY(lookupService.findByTypeAndCode(Currency.class, dto.getFundCCY()));
            if(entity.getFundCCY() == null){
                logger.error("Error parsing 'Singularity General Ledger Balance' file: fund ccy could not be determined - '" + dto.getFundCCY() + "'");
                throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: fund type could not be determined - '" + dto.getFundCCY() + "'");
            }
        }

        // report
        entity.setReport(new PeriodicReport(reportId));

        return entity;
    }

    @Override
    public SingularityGeneralLedgerBalanceRecordDto getRecordById(Long recordId) {
        if(recordId != null) {
            ReportingHFGeneralLedgerBalance entity = this.generalLedgerBalanceRepository.findOne(recordId);
            if (entity != null) {
                return disassemble(entity);
            }
        }

        return null;
    }

    @Override
    public List<ReportingHFGeneralLedgerBalance> assembleList(List<SingularityGeneralLedgerBalanceRecordDto> dtoList, Long reportId) {
        List<ReportingHFGeneralLedgerBalance> entities = new ArrayList<>();
        if(dtoList != null){
            for(SingularityGeneralLedgerBalanceRecordDto dto: dtoList){
                ReportingHFGeneralLedgerBalance entity = assemble(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingHFGeneralLedgerBalance> entities) {
        try {
            if (entities != null) {
                this.generalLedgerBalanceRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving GL Singularity entities. ", ex);
            return false;
        }
    }

    @Override
    public boolean excludeIncludeSingularityRecord(Long recordId, String username) {
        try {
            ReportingHFGeneralLedgerBalance entity = this.generalLedgerBalanceRepository.findOne(recordId);
            if (entity != null) {
                boolean value = entity.getExcludeFromSingularityCalculation() != null ? entity.getExcludeFromSingularityCalculation().booleanValue() : false;
                entity.setExcludeFromSingularityCalculation(!value);
                this.generalLedgerBalanceRepository.save(entity);
                logger.info("Successfully included/excluded Singularity GL record with id: " + recordId + " [user]=" + username);
                return true;
            }
            logger.error("Error to include/exclude Singularity GL record with id: " + recordId + " - record not found [user]=" + username);
        }catch (Exception ex){
            logger.error("Error to include/exclude Singularity GL record with id: " + recordId + " [user]=" + username, ex);
        }
        return false;
    }


    private int getTranche(String acronym){
        if(StringUtils.isEmpty(acronym)){
            logger.error("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined - empty value");
            throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined - empty value");
        }else if(acronym.equalsIgnoreCase("SINGULAR")){
            return 1;
        }else if(acronym.equalsIgnoreCase("SINGULAR B")){
            return 2;
        }else{
            logger.error("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined - " + acronym);
            throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined - " + acronym);
        }
    }

    private String getAcronym(Integer tranche){
        if(tranche == null ){
            logger.error("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined - empty value");
            throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined - empty value");
        }else if(tranche.intValue() == 1){
            return PeriodicReportConstants.SINGULARITY_A_LOWER_CASE;
        }else if(tranche.intValue() == 2){
            return PeriodicReportConstants.SINGULARITY_B_LOWER_CASE;
        }else{
            logger.error("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined from tranche - " + tranche);
            throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined from tranche - " + tranche);
        }
    }

    @Override
    public ConsolidatedReportRecordHolderDto getWithExcludedRecords(Long reportId) {
        List<ReportingHFGeneralLedgerBalance> entities = this.generalLedgerBalanceRepository.getEntitiesByReportId(reportId,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<SingularityGeneralLedgerBalanceRecordDto> records = disassembleList(entities);

        result.setGeneralLedgerBalanceList(records);

        if(entities != null && !entities.isEmpty()) {
            result.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
        }

        return result;
    }

    @Override
    public ConsolidatedReportRecordHolderDto getWithoutExcludedRecords(Long reportId) {
        ConsolidatedReportRecordHolderDto holderDto = getWithExcludedRecords(reportId);
        List<SingularityGeneralLedgerBalanceRecordDto> records = new ArrayList<>();
        if(holderDto != null && holderDto.getGeneralLedgerBalanceList() != null){
            for(SingularityGeneralLedgerBalanceRecordDto record: holderDto.getGeneralLedgerBalanceList()){
                if(record.getExcludeFromSingularityCalculation() == null || !record.getExcludeFromSingularityCalculation().booleanValue()){
                    records.add(record);
                }
            }
        }

        ConsolidatedReportRecordHolderDto resultDto = new ConsolidatedReportRecordHolderDto();
        resultDto.setGeneralLedgerBalanceList(records);
        return resultDto;
    }

    @Override
    public boolean deleteByReportId(Long reportId) {
        try {
            this.generalLedgerBalanceRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting schedule of investments records with report id=" + reportId);
            return false;
        }
    }

    @Override
    public boolean saveAdjustments(SingularityAdjustmentsDto adjustmentsDto, String updater) {
        if(adjustmentsDto.getReportId() == null){
            logger.error("Error saving singularity adjustments: report id is null");
            return false;
        }
        PeriodicReportDto reportDto = periodicReportService.getPeriodicReport(adjustmentsDto.getReportId());
        if(reportDto == null){
            logger.error("Error saving singularity adjustments: report not found for report id '" + adjustmentsDto.getReportId() + "'");
            return false;
        }else if(reportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            logger.error("Failed to save Singularity Adjustments for report with status 'SUBMITTED' : report id " + reportDto.getId());
            return false;
        }

        if(adjustmentsDto.getAdjustedRedemptions() != null && !adjustmentsDto.getAdjustedRedemptions().isEmpty()){
            List<ReportingHFGeneralLedgerBalance> entityList = new ArrayList<>();
            for(SingularityFundAdjustmentDto adjustment: adjustmentsDto.getAdjustedRedemptions()){
                if(adjustment.getRecordId() != null){
                    ReportingHFGeneralLedgerBalance entity = this.generalLedgerBalanceRepository.findOne(adjustment.getRecordId());
                    if(entity != null){
                        entity.setAdjustedRedemption(adjustment.getAdjustedRedemption());
                        entity.setInterestRate(StringUtils.isNotEmpty(adjustment.getInterestRate()) ? adjustment.getInterestRate() : null);
                        entity.setComment(adjustment.getComment());
                        entityList.add(entity);
                    }else{
                        logger.error("Error saving Singularity Adjustments for report id " + reportDto.getId() + ": record not found with id " + adjustment.getRecordId());
                        return false;
                    }
                }
            }

            this.generalLedgerBalanceRepository.save(entityList);
        }
        logger.info("Successfully saved Singularity Adjustment for report id: " + adjustmentsDto.getReportId() + "'");
        return true;

//        EntitySaveResponseDto entitySaveResponseDto = this.periodicReportService.saveInterestRate(adjustmentsDto.getReportId(),
//                adjustmentsDto.getInterestRate(), updater);

//        if(entitySaveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
//            // FAILED
//            String errorMessage = entitySaveResponseDto.getMessage().getMessageText() != null ?
//                    entitySaveResponseDto.getMessage().getMessageText() :
//                    "Singularity adjustments: error saving interest rate for Singularity: report id '" + adjustmentsDto.getReportId() + "'";
//            logger.error(errorMessage);
//            return false;
//        }else{
//            if(adjustmentsDto.getAdjustedRedemptions() != null){
//                for(SingularityFundAdjustmentDto adjustment: adjustmentsDto.getAdjustedRedemptions()){
//                    ReportingHFGeneralLedgerBalance entity = this.generalLedgerBalanceRepository.findOne(adjustment.getRecordId());
//                    entity.setAdjustedRedemption(adjustment.getAdjustedRedemption());
//                    this.generalLedgerBalanceRepository.save(entity);
//                }
//            }
//            logger.info("Successfully saved Singularity Adjustment for report id: " + adjustmentsDto.getReportId() + "'");
//            return true;
//        }
    }

    @Override
    public List<SingularityGeneralLedgerBalanceRecordDto> getAdjustedRecords(Long reportId){

        List<ReportingHFGeneralLedgerBalance> entities = this.generalLedgerBalanceRepository.getAdjustedEntitiesByReportId(reportId,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        List<SingularityGeneralLedgerBalanceRecordDto> records = disassembleList(entities);
        return records;
    }


    public List<SingularityGeneralLedgerBalanceRecordDto> disassembleList(List<ReportingHFGeneralLedgerBalance> entities){
        List<SingularityGeneralLedgerBalanceRecordDto> records = new ArrayList<>();
        if(entities != null){
            for(ReportingHFGeneralLedgerBalance entity: entities){
                SingularityGeneralLedgerBalanceRecordDto dto = disassemble(entity);
                records.add(dto);
            }
        }
        return records;
    }


    public SingularityGeneralLedgerBalanceRecordDto disassemble(ReportingHFGeneralLedgerBalance entity){
        if(entity != null){
            SingularityGeneralLedgerBalanceRecordDto dto = new SingularityGeneralLedgerBalanceRecordDto();
            dto.setId(entity.getId());

            dto.setAcronym(getAcronym(entity.getTranche()));
            dto.setBalanceDate(entity.getBalanceDate());
            if(entity.getFinancialStatementCategory() != null) {
                dto.setFinancialStatementCategory(entity.getFinancialStatementCategory().getCode());
            }
            dto.setGLAccount(entity.getGLAccount());
            if(entity.getChartAccountsType() != null) {
                dto.setFinancialStatementCategoryDescription(entity.getChartAccountsType().getNameEn());
            }
            dto.setChartAccountsLongDescription(entity.getChartAccountsLongDescription());
            dto.setShortName(entity.getShortName());
            dto.setGLAccountBalance(entity.getGLAccountBalance());
            if(entity.getSegValCCY() != null) {
                dto.setSegValCCY(entity.getSegValCCY().getCode());
            }
            if(entity.getFundCCY() != null){
                dto.setFundCCY(entity.getFundCCY().getCode());
            }
            dto.setAdjustedRedemption(entity.getAdjustedRedemption());
            dto.setInterestRate(entity.getInterestRate());
            dto.setComment(entity.getComment());
            dto.setExcludeFromSingularityCalculation(entity.getExcludeFromSingularityCalculation());
            return dto;
        }
        return null;
    }

    @Override
    public boolean existEntityWithChartAccountsType(String code) {
        int count = this.generalLedgerBalanceRepository.getEntitiesCountByChartAccountsType(code);
        return count > 0;
    }
}
