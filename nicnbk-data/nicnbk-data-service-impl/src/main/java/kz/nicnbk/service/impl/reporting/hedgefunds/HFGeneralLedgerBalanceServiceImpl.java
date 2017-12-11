package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.HFChartOfAccountsTypeRepository;
import kz.nicnbk.repo.api.lookup.HFFinancialStatementTypeRepository;
import kz.nicnbk.repo.api.reporting.hedgefunds.ReportingHFGeneralLedgerBalanceRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFGeneralLedgerBalance;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.SingularityGeneralLedgerBalanceRecordDto;
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
        // TODO: boolean result, check for error?
        if(entities != null){
            this.generalLedgerBalanceRepository.save(entities);
        }
        return true;
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
            dto.setGLAccountBalance(entity.getGLAccountBalance());
            if(entity.getSegValCCY() != null) {
                dto.setSegValCCY(entity.getSegValCCY().getCode());
            }
            if(entity.getFundCCY() != null){
                dto.setFundCCY(entity.getFundCCY().getCode());
            }
            return dto;
        }
        return null;
    }

}
