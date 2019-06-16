package kz.nicnbk.service.impl.reporting.realestate;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.HFFinancialStatementTypeRepository;
import kz.nicnbk.repo.api.lookup.REChartOfAccountsTypeRepository;
import kz.nicnbk.repo.api.reporting.realestate.ReportingREGeneralLedgerBalanceRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.realestate.ReportingREGeneralLedgerBalance;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.realestate.REGeneralLedgerBalanceService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.common.service.exception.ExcelFileParseException;
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
public class REGeneralLedgerBalanceServiceImpl implements REGeneralLedgerBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(REGeneralLedgerBalanceServiceImpl.class);

    @Autowired
    private ReportingREGeneralLedgerBalanceRepository generalLedgerBalanceRepository;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private HFFinancialStatementTypeRepository financialStatementTypeRepository;

    @Autowired
    private REChartOfAccountsTypeRepository chartOfAccountsTypeRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Override
    public ReportingREGeneralLedgerBalance assemble(SingularityGeneralLedgerBalanceRecordDto dto, Long reportId) {
        ReportingREGeneralLedgerBalance entity = new ReportingREGeneralLedgerBalance();
        entity.setTranche(getTranche(dto.getAcronym()));
        entity.setBalanceDate(dto.getBalanceDate());

        if(dto.getFinancialStatementCategory() != null) {
            entity.setFinancialStatementCategory(this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, dto.getFinancialStatementCategory()));
            if(entity.getFinancialStatementCategory() == null){
                logger.error("Error parsing 'Terra General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
                throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
            }
        }
        if(dto.getFinancialStatementCategoryDescription() != null){
            entity.setChartAccountsType(this.chartOfAccountsTypeRepository.findByNameEnIgnoreCase(dto.getFinancialStatementCategoryDescription().trim()));
            if(entity.getChartAccountsType() == null){
                logger.error("Error parsing 'Terra General Ledger Balance' file: chart of accounts type could not be determined - '" + dto.getFinancialStatementCategoryDescription() + "'");
                throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: chart of accounts type could not be determined - '" + dto.getFinancialStatementCategoryDescription() + "'");
            }
        }

        entity.setGLAccount(dto.getGLAccount());
        entity.setChartAccountsLongDescription(dto.getChartAccountsLongDescription());
        entity.setShortName(dto.getShortName());
        entity.setGLAccountBalance(dto.getGLAccountBalance());

        if(dto.getSegValCCY() != null){
            entity.setSegValCCY(lookupService.findByTypeAndCode(Currency.class, dto.getSegValCCY()));
            if(entity.getSegValCCY() == null){
                logger.error("Error parsing 'Terra General Ledger Balance' file: seg val ccy could not be determined - '" + dto.getSegValCCY() + "'");
                throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: seg val ccy type could not be determined - '" + dto.getSegValCCY() + "'");
            }
        }
        if(dto.getFundCCY() != null){
            entity.setFundCCY(lookupService.findByTypeAndCode(Currency.class, dto.getFundCCY()));
            if(entity.getFundCCY() == null){
                logger.error("Error parsing 'Terra General Ledger Balance' file: fund ccy could not be determined - '" + dto.getFundCCY() + "'");
                throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: fund type could not be determined - '" + dto.getFundCCY() + "'");
            }
        }

        // report
        entity.setReport(new PeriodicReport(reportId));

        return entity;
    }

    @Override
    public List<ReportingREGeneralLedgerBalance> assembleList(List<SingularityGeneralLedgerBalanceRecordDto> dtoList, Long reportId) {
        List<ReportingREGeneralLedgerBalance> entities = new ArrayList<>();
        if(dtoList != null){
            for(SingularityGeneralLedgerBalanceRecordDto dto: dtoList){
                ReportingREGeneralLedgerBalance entity = assemble(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean save(List<ReportingREGeneralLedgerBalance> entities) {
        try {
            if (entities != null) {
                this.generalLedgerBalanceRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving GL Terra entities. ", ex);
            return false;
        }
    }

    @Override
    public ConsolidatedReportRecordHolderDto get(Long reportId) {
        List<ReportingREGeneralLedgerBalance> entities = this.generalLedgerBalanceRepository.getEntitiesByReportId(reportId,
                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));

        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
        List<SingularityGeneralLedgerBalanceRecordDto> records = disassembleList(entities);

        result.setRealEstateGeneralLedgerBalanceList(records);

        if(entities != null && !entities.isEmpty()) {
            result.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
        }

        return result;
    }


    private int getTranche(String acronym){
        if(StringUtils.isEmpty(acronym)){
            logger.error("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined - empty value");
            throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined - empty value");
        }else if(acronym.equalsIgnoreCase("TERRA")){
            return 1;
        }else if(acronym.equalsIgnoreCase("TERRA B")){
            return 2;
        }else{
            logger.error("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined - " + acronym);
            throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined - " + acronym);
        }
    }

    private String getAcronym(Integer tranche){
        if(tranche == null ){
            logger.error("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined - empty value");
            throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined - empty value");
        }else if(tranche.intValue() == 1){
            return "TERRA";
        }else if(tranche.intValue() == 2){
            return "TERRA B";
        }else{
            logger.error("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined from tranche - " + tranche);
            throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: tranche could not be determined from tranche - " + tranche);
        }
    }

    public List<SingularityGeneralLedgerBalanceRecordDto> disassembleList(List<ReportingREGeneralLedgerBalance> entities){
        List<SingularityGeneralLedgerBalanceRecordDto> records = new ArrayList<>();
        if(entities != null){
            for(ReportingREGeneralLedgerBalance entity: entities){
                SingularityGeneralLedgerBalanceRecordDto dto = disassemble(entity);
                records.add(dto);
            }
        }
        return records;
    }


    public SingularityGeneralLedgerBalanceRecordDto disassemble(ReportingREGeneralLedgerBalance entity){
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
