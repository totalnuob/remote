package kz.nicnbk.service.impl.reporting.realestate;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.HFFinancialStatementTypeRepository;
import kz.nicnbk.repo.api.lookup.REChartOfAccountsTypeRepository;
import kz.nicnbk.repo.api.lookup.RETrancheTypeRepository;
import kz.nicnbk.repo.api.reporting.realestate.ReportingREGeneralLedgerBalanceRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import kz.nicnbk.repo.model.reporting.realestate.REChartOfAccountsType;
import kz.nicnbk.repo.model.reporting.realestate.RETrancheType;
import kz.nicnbk.repo.model.reporting.realestate.ReportingREGeneralLedgerBalance;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.realestate.REGeneralLedgerBalanceService;
import kz.nicnbk.service.converter.reporting.PeriodicReportConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.service.dto.reporting.realestate.TerraGeneralLedgerBalanceRecordDto;
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

    @Autowired
    private RETrancheTypeRepository reTrancheTypeRepository;

    @Autowired
    private REChartOfAccountsTypeRepository reChartAccountsTypeRepository;

    @Override
    public ReportingREGeneralLedgerBalance assemble(TerraGeneralLedgerBalanceRecordDto dto, Long reportId) {
        ReportingREGeneralLedgerBalance entity = new ReportingREGeneralLedgerBalance();

        if(dto.getAcronym() != null){
            RETrancheType trancheType = this.reTrancheTypeRepository.findByNameEnIgnoreCase(dto.getAcronym().trim());
            if(trancheType != null){
                entity.setTrancheType(trancheType);
            }
        }
        if(entity.getTrancheType() == null){
            logger.error("Error parsing 'Terra General Ledger Balance' file: tranche type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
            throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: tranche type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
        }

        entity.setBalanceDate(dto.getBalanceDate());

        if(dto.getFinancialStatementCategory() != null) {
            entity.setFinancialStatementCategory(this.lookupService.findByTypeAndCode(FinancialStatementCategory.class, dto.getFinancialStatementCategory()));
            if(entity.getFinancialStatementCategory() == null){
                logger.error("Error parsing 'Terra General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
                throw new ExcelFileParseException("Error parsing 'Terra General Ledger Balance' file: financial statement type could not be determined - '" + dto.getFinancialStatementCategory() + "'");
            }
        }

        entity.setGlSubclass(dto.getGlSubclass());
        entity.setPortfolioFund(dto.getPortfolioFund());
        entity.setAccountBalanceGP(dto.getAccountBalanceGP());
        entity.setAccountBalanceNICKMF(dto.getAccountBalanceNICKMF());
        entity.setAccountBalanceGrandTotal(dto.getAccountBalanceGrandTotal());

        // report
        entity.setReport(new PeriodicReport(reportId));

        return entity;
    }

    @Override
    public List<ReportingREGeneralLedgerBalance> assembleList(List<TerraGeneralLedgerBalanceRecordDto> dtoList, Long reportId) {
        List<ReportingREGeneralLedgerBalance> entities = new ArrayList<>();
        if(dtoList != null){
            for(TerraGeneralLedgerBalanceRecordDto dto: dtoList){
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

//    @Override
//    public ConsolidatedReportRecordHolderDto get(Long reportId) {
//        List<ReportingREGeneralLedgerBalance> entities = this.generalLedgerBalanceRepository.getEntitiesByReportId(reportId,
//                new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//
//        ConsolidatedReportRecordHolderDto result = new ConsolidatedReportRecordHolderDto();
//        List<SingularityGeneralLedgerBalanceRecordDto> records = disassembleList(entities);
//
//        result.setRealEstateGeneralLedgerBalanceList(records);
//
//        if(entities != null && !entities.isEmpty()) {
//            result.setReport(periodicReportConverter.disassemble(entities.get(0).getReport()));
//        }
//
//        return result;
//    }


    public List<TerraGeneralLedgerBalanceRecordDto> disassembleList(List<ReportingREGeneralLedgerBalance> entities){
        List<TerraGeneralLedgerBalanceRecordDto> records = new ArrayList<>();
        if(entities != null){
            for(ReportingREGeneralLedgerBalance entity: entities){
                TerraGeneralLedgerBalanceRecordDto dto = disassemble(entity);
                records.add(dto);
            }
        }
        return records;
    }

    public TerraGeneralLedgerBalanceRecordDto disassemble(ReportingREGeneralLedgerBalance entity){
        if(entity != null){
            TerraGeneralLedgerBalanceRecordDto dto = new TerraGeneralLedgerBalanceRecordDto();
            dto.setId(entity.getId());

            if(entity.getTrancheType() != null) {
                dto.setAcronym(entity.getTrancheType().getNameEn());
            }
            dto.setBalanceDate(entity.getBalanceDate());
            if(entity.getFinancialStatementCategory() != null) {
                dto.setFinancialStatementCategory(entity.getFinancialStatementCategory().getCode());
            }
//            if(entity.getGlSubclass() != null){
//                dto.setGlSubclass(entity.getGlSubclass().getNameEn());
//            }
            dto.setGlSubclass(entity.getGlSubclass());
            dto.setPortfolioFund(entity.getPortfolioFund());
            dto.setAccountBalanceGP(entity.getAccountBalanceGP());
            dto.setAccountBalanceNICKMF(entity.getAccountBalanceNICKMF());
            dto.setAccountBalanceGrandTotal(entity.getAccountBalanceGrandTotal());
            dto.setExcludeFromTerraCalculation(entity.getExcludeFromTerraCalculation());
            return dto;
        }
        return null;
    }

    @Override
    public boolean deleteByReportId(Long reportId){
        if(reportId == null){
            logger.error("Error deleting Terra General Ledger records by id: id is null");
            return false;
        }
        try {
            this.generalLedgerBalanceRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting Terra General Ledger records by id=" + reportId.longValue() + " (with exception)", ex);
            return false;
        }
    }

    @Override
    public List<TerraGeneralLedgerBalanceRecordDto> getRecordsByReportId(Long reportId){
        List<ReportingREGeneralLedgerBalance> entities = this.generalLedgerBalanceRepository.getEntitiesByReportId(reportId);
        if(entities != null){
            return disassembleList(entities);
        }
        return null;
    }

    @Override
    public boolean excludeRecord(Long recordId) {
        try{
            ReportingREGeneralLedgerBalance entity = this.generalLedgerBalanceRepository.findOne(recordId);
            entity.setExcludeFromTerraCalculation(true);
            this.generalLedgerBalanceRepository.save(entity);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    @Override
    public boolean excludeIncludeRecord(Long recordId) {
        try{
            ReportingREGeneralLedgerBalance entity = this.generalLedgerBalanceRepository.findOne(recordId);
            if(entity.getExcludeFromTerraCalculation() == null || !entity.getExcludeFromTerraCalculation().booleanValue()){
                entity.setExcludeFromTerraCalculation(true);
            }else{
                entity.setExcludeFromTerraCalculation(false);
            }
            this.generalLedgerBalanceRepository.save(entity);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    @Override
    public boolean includeRecord(Long recordId) {
        try{
            ReportingREGeneralLedgerBalance entity = this.generalLedgerBalanceRepository.findOne(recordId);
            entity.setExcludeFromTerraCalculation(false);
            this.generalLedgerBalanceRepository.save(entity);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

}
