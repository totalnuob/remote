package kz.nicnbk.service.impl.reporting.hedgefunds;

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
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.reporting.*;
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

    @Override
    public ReportingHFGeneralLedgerBalance assemble(SingularityGeneralLedgerBalanceRecordDto dto, Long reportId) {
        ReportingHFGeneralLedgerBalance entity = new ReportingHFGeneralLedgerBalance();
        entity.setTranche(getTranche(dto.getAcronym()));
        entity.setBalanceDate(dto.getBalanceDate());

        if(dto.getFinancialStatementCategory() != null) {
            entity.setFinancialStatementCategory(this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, dto.getFinancialStatementCategory()));
            if(entity.getFinancialStatementCategory() == null){
                logger.error("Error parsing 'Singularity General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
                throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
            }
        }
        if(dto.getFinancialStatementCategoryDescription() != null){
            entity.setChartAccountsType(this.chartOfAccountsTypeRepository.findByNameEnIgnoreCase(dto.getFinancialStatementCategoryDescription().trim()));
            if(entity.getChartAccountsType() == null){
                logger.error("Error parsing 'Singularity General Ledger Balance' file: chart of accounts type could not be determined - '" + dto.getFinancialStatementCategoryDescription() + "'");
                throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: chart of accounts type could not be determined - '" + dto.getFinancialStatementCategoryDescription() + "'");
            }
        }

        entity.setGLAccount(dto.getGLAccount());
        entity.setChartAccountsLongDescription(dto.getChartAccountsLongDescription());
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
            return "SINGULAR";
        }else if(tranche.intValue() == 2){
            return "SINGULAR B";
        }else{
            logger.error("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined from tranche - " + tranche);
            throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: tranche could not be determined from tranche - " + tranche);
        }
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
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
            return dto;
        }
        return null;
    }

}
