package kz.nicnbk.service.impl.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.NumberUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.REBalanceTypeRepository;
import kz.nicnbk.repo.api.lookup.REProfitLossTypeRepository;
import kz.nicnbk.repo.api.reporting.NICReportingChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.hedgefunds.SingularityNICChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.realestate.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.reporting.TerraExcludeRecordTypeLookup;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.hedgefunds.SingularityNICChartOfAccounts;
import kz.nicnbk.repo.model.reporting.realestate.*;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.api.reporting.privateequity.ReserveCalculationService;
import kz.nicnbk.service.api.reporting.realestate.PeriodicReportREService;
import kz.nicnbk.service.api.reporting.realestate.REGeneralLedgerBalanceService;
import kz.nicnbk.service.converter.reporting.realestate.RealEstateGeneralLedgerFormDataConverter;
import kz.nicnbk.service.converter.reporting.realestate.ReportingREBalanceSheetConverter;
import kz.nicnbk.service.converter.reporting.realestate.ReportingREProfitLossConverter;
import kz.nicnbk.service.converter.reporting.realestate.ReportingRESecuritiesCostConverter;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.exception.ExcelFileParseException;
import kz.nicnbk.service.dto.reporting.realestate.*;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import kz.nicnbk.service.impl.reporting.lookup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by magzumov on 18.01.2018.
 */
@Service
public class PeriodicReportREServiceImpl implements PeriodicReportREService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportREServiceImpl.class);

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private REBalanceTypeRepository balanceTypeRepository;

    @Autowired
    private REProfitLossTypeRepository profitLossTypeRepository;

    @Autowired
    private ReportingREBalanceSheetRepository balanceSheetRepository;

    @Autowired
    private ReportingREProfitLossRepository profitLossRepository;

    @Autowired
    private ReportingRESecuritiesCostRepository securitiesCostRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ReportingREBalanceSheetConverter balanceSheetConverter;

    @Autowired
    private ReportingREProfitLossConverter profitLossConverter;

    @Autowired
    private ReportingRESecuritiesCostConverter securitiesCostConverter;

    @Autowired
    private TerraNICChartOfAccountsRepository terraNICChartOfAccountsRepository;

    @Autowired
    private ReserveCalculationService reserveCalculationService;

    @Autowired
    private REGeneralLedgerFormDataRepository reGeneralLedgerFormDataRepository;

    @Autowired
    private RealEstateGeneralLedgerFormDataConverter realEstateGeneralLedgerFormDataConverter;



    @Override
    public ReportingREBalanceSheet assembleBalanceSheet(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingREBalanceSheet entity = new ReportingREBalanceSheet();
        entity.setName(dto.getName());
        entity.setTotalSum(dto.isTotalSum());
        entity.setValueGP(dto.getValues()[0]);
        entity.setValueNICKMF(dto.getValues()[1]);
        entity.setGrandTotal(dto.getValues()[2]);
        entity.setCreationDate(new Date()); // does not edit, only create

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        //entity.setTranche(tranche);

        // balance type
        for (int i = 0; i < dto.getClassifications().length; i++) {
            String classification = dto.getClassifications()[i];
            if (classification != null) {
                REBalanceType balanceType = this.balanceTypeRepository.findByNameEnIgnoreCase(classification.trim());
                if (balanceType != null) {
                    entity.setType(balanceType);
                    //break;
                }
            }
        }

        if (entity.getType() == null) {
            REBalanceType balanceType = this.balanceTypeRepository.findByNameEnIgnoreCase(dto.getName().trim());
            if (balanceType != null) {
                entity.setType(balanceType);
                //break;
            }
        }

        if(entity.getType() == null && !entity.getName().equalsIgnoreCase("NET ASSET VALUE")){
            logger.error("Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.  Check for possible spaces in names.");
            throw new ExcelFileParseException("Balance record type could not be determined for record '" + entity.getName() + "'. Expected values are 'Assets', 'Liabilities', etc.  Check for possible spaces in names.");
        }

        return entity;
    }

    @Override
    public List<ReportingREBalanceSheet> assembleBalanceSheetList(List<ConsolidatedReportRecordDto> dtoList, Long reportId) {
        List<ReportingREBalanceSheet> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingREBalanceSheet entity = assembleBalanceSheet(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public ReportingREProfitLoss assembleProfitLoss(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingREProfitLoss entity = new ReportingREProfitLoss();
        entity.setName(dto.getName());
        entity.setTotalSum(dto.isTotalSum());
        entity.setValueGP(dto.getValues()[0]);
        entity.setValueNICKMF(dto.getValues()[1]);
        entity.setGrandTotal(dto.getValues()[2]);
        entity.setCreationDate(new Date());// does not edit, only create

        // report
        entity.setReport(new PeriodicReport(reportId));

        // tranche
        //entity.setTranche(tranche);

        // balance type

        for (int i = 0; i < dto.getClassifications().length; i++) {
            String classification = dto.getClassifications()[i];
            if (classification != null) {
                REProfitLossType balanceType = this.profitLossTypeRepository.findByNameEnIgnoreCase(classification.trim());
                if (balanceType != null) {
                    entity.setType(balanceType);
                    //break;
                }
            }
        }

        if (entity.getType() == null) {
            REProfitLossType type = this.profitLossTypeRepository.findByNameEnIgnoreCase(dto.getName().trim());
            if (type != null) {
                entity.setType(type);
                //break;
            }
        }

        if(entity.getType() == null && !entity.getName().equalsIgnoreCase("NET PROFIT/LOSS FOR THE PERIOD")){
            logger.error("Profit Loss record type could not be determined for record '" + entity.getName() + "'. Check for possible spaces in names.");
            throw new ExcelFileParseException("Profit Loss record type could not be determined for record '" + entity.getName() + "'. Check for possible spaces in names.");
        }

        return entity;
    }

    @Override
    public List<ReportingREProfitLoss> assembleProfitLossList(List<ConsolidatedReportRecordDto> dtoList, Long reportId) {
        List<ReportingREProfitLoss> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingREProfitLoss entity = assembleProfitLoss(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public ReportingRESecuritiesCost assembleSecuritiesCost(ConsolidatedReportRecordDto dto, Long reportId) {
        ReportingRESecuritiesCost entity = new ReportingRESecuritiesCost();
        entity.setName(dto.getName());
        entity.setTotalSum(dto.isTotalSum());
        entity.setTotalCostFCY(dto.getValues()[0]);
        entity.setCostLCYHistorical(dto.getValues()[1]);
        entity.setCostLCYCurrentFXRate(dto.getValues()[2]);
        entity.setUnrealizedGainFCY(dto.getValues()[3]);
        entity.setUnrealizedGainLCY(dto.getValues()[4]);
        entity.setFXGainLCY(dto.getValues()[5]);
        entity.setMarketValueFCY(dto.getValues()[6]);
        entity.setCreationDate(new Date());// does not edit, only create

        // report
        entity.setReport(new PeriodicReport(reportId));

        return entity;
    }

    @Override
    public List<ReportingRESecuritiesCost> assembleSecuritiesCostList(List<ConsolidatedReportRecordDto> dtoList, Long reportId) {
        List<ReportingRESecuritiesCost> entities = new ArrayList<>();
        if(dtoList != null){
            for(ConsolidatedReportRecordDto dto: dtoList){
                ReportingRESecuritiesCost entity = assembleSecuritiesCost(dto, reportId);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean saveBalanceSheet(List<ReportingREBalanceSheet> entities, String username) {
        try {
            if (entities != null) {
                EmployeeDto creator = this.employeeService.findByUsername(username);
                if(creator != null){
                    for(ReportingREBalanceSheet entity: entities){
                        entity.setCreator(new Employee(creator.getId()));
                    }
                }
                balanceSheetRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving Statement of Balance entities. ", ex);
            return false;
        }
    }

    @Override
    public boolean saveProfitLoss(List<ReportingREProfitLoss> entities, String username) {
        try {
            if (entities != null) {
                EmployeeDto creator = this.employeeService.findByUsername(username);
                if(creator != null){
                    for(ReportingREProfitLoss entity: entities){
                        entity.setCreator(new Employee(creator.getId()));
                    }
                }
                profitLossRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving Statement of Balance entities. ", ex);
            return false;
        }
    }

    @Override
    public boolean saveSecuritiesCost(List<ReportingRESecuritiesCost> entities, String username) {
        try {
            if (entities != null) {
                EmployeeDto creator = this.employeeService.findByUsername(username);
                if(creator != null){
                    for(ReportingRESecuritiesCost entity: entities){
                        entity.setCreator(new Employee(creator.getId()));
                    }
                }
                securitiesCostRepository.save(entities);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving Statement of Balance entities. ", ex);
            return false;
        }
    }

    @Override
    public boolean deleteByReportId(Long reportId) {
        try {
            this.balanceSheetRepository.deleteByReportId(reportId);
            this.profitLossRepository.deleteByReportId(reportId);
            this.securitiesCostRepository.deleteByReportId(reportId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting Terra parsed records with report id=" + reportId);
            return false;
        }
    }

    @Override
    public List<TerraBalanceSheetRecordDto> getBalanceSheetRecords(Long reportId) {
        List<ReportingREBalanceSheet> entities =
                this.balanceSheetRepository.getEntitiesByReportId(reportId, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
        List<TerraBalanceSheetRecordDto> dtoList = this.balanceSheetConverter.disassembleList(entities);
        return dtoList;
    }

    @Override
    public List<TerraProfitLossRecordDto> getProfitLossRecords(Long reportId) {
        List<ReportingREProfitLoss> entities =
                this.profitLossRepository.getEntitiesByReportId(reportId, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
        List<TerraProfitLossRecordDto> dtoList = this.profitLossConverter.disassembleList(entities);
        return dtoList;
    }

    @Override
    public List<TerraSecuritiesCostRecordDto> getSecuritiesCostRecords(Long reportId) {
        List<ReportingRESecuritiesCost> entities =
                this.securitiesCostRepository.getEntitiesByReportId(reportId, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
        List<TerraSecuritiesCostRecordDto> dtoList = this.securitiesCostConverter.disassembleList(entities);
        return dtoList;
    }

    @Override
    public ListResponseDto getTerraGeneratedForm(Long reportId){
        try {
            ListResponseDto responseDto = new ListResponseDto();

            PeriodicReportDto report = this.periodicReportService.getPeriodicReport(reportId);
            if(report == null){
                logger.error("Error getting Terra Generated GL Form: report is not found for report id " + reportId);
                responseDto.setErrorMessageEn("Error getting Terra Generated GL Form: report is not found for report id " + reportId);
                return responseDto;
            }

            List<TerraGeneratedGeneralLedgerFormDto> records = new ArrayList<>();

            // Balance sheet
            List<TerraBalanceSheetRecordDto> balanceRecords = getBalanceSheetRecords(reportId);
            records.addAll(processTerraBalanceSheet(balanceRecords));

            // Securities cost
            List<TerraSecuritiesCostRecordDto> securitiesCostRecords = getSecuritiesCostRecords(reportId);
            records.addAll(processTerraSecuritiesCost(securitiesCostRecords));

            // From capital calls

            List<ReserveCalculationDto> reserveCalculationRecords =
                    this.reserveCalculationService.getReserveCalculationsForMonth(ReserveCalculationsExpenseTypeLookup.ADD.getCode(), report.getReportDate(), true);
            if(reserveCalculationRecords != null){
                for(ReserveCalculationDto reserveCalculationDto: reserveCalculationRecords){
                    if(reserveCalculationDto.getRecipient() == null || !reserveCalculationDto.getRecipient().getCode().startsWith("TERR")){
                        continue;
                    }
                    TerraGeneratedGeneralLedgerFormDto recordDto = new TerraGeneratedGeneralLedgerFormDto();
                    recordDto.setId(reserveCalculationDto.getId());
                    String acronym = reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TERRA_A.getCode()) ? "TERRA A" :
                            reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TERRA_B.getCode()) ? "TERRA B" : "TERRA";
                    if(acronym.equalsIgnoreCase("TERRA")) {
                        logger.error("Error generating Terra GL Form: capital call recipient is specified as 'Terra', must be either 'Terra A' or 'Terra B'");
                        responseDto.setErrorMessageEn("Error generating Terra GL Form: capital call recipient is specified as 'Terra', must be either 'Terra A' or 'Terra B'");
                        return responseDto;
                    }
                    recordDto.setAcronym(acronym);
                    recordDto.setBalanceDate(report.getReportDate());

                    Double amount = reserveCalculationDto.getAmountToSPV() != null ?
                            reserveCalculationDto.getAmountToSPV() : reserveCalculationDto.getAmount();
                    recordDto.setGLAccountBalance(amount);
                    recordDto.setAdded(false);
                    recordDto.setEditable(false);

                    recordDto.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());
                    recordDto.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CASH_ADJ.getNameEn());
                    recordDto.setNbAccountNumber(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getCodeNBRK());
                    recordDto.setNicAccountName(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getNameRu());


                    recordDto.setExcludeFromTerraCalculation(reserveCalculationDto.getExcludeFromTerraCalculation() != null
                            && reserveCalculationDto.getExcludeFromTerraCalculation().booleanValue());

                    recordDto.setType(TerraExcludeRecordTypeLookup.CAPITAL_CALL.getCode());
                    records.add(recordDto);

                    TerraGeneratedGeneralLedgerFormDto recordDtoOpposite = new TerraGeneratedGeneralLedgerFormDto();
                    recordDtoOpposite.setAcronym(acronym);
                    recordDtoOpposite.setBalanceDate(report.getReportDate());
                    recordDtoOpposite.setGLAccountBalance(MathUtils.subtract(0.0, amount));
                    recordDtoOpposite.setAdded(false);
                    recordDtoOpposite.setEditable(false);

                    recordDtoOpposite.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.EQUITY.getCode());
                    recordDtoOpposite.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CAPITAL_ADJ.getNameEn());
                    recordDtoOpposite.setNbAccountNumber(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getCodeNBRK());
                    recordDtoOpposite.setNicAccountName(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getNameRu());

                    recordDtoOpposite.setExcludeFromTerraCalculation(reserveCalculationDto.getExcludeOppositeFromTerraCalculation() != null
                            && reserveCalculationDto.getExcludeOppositeFromTerraCalculation().booleanValue());
                    recordDtoOpposite.setId(reserveCalculationDto.getId());
                    recordDtoOpposite.setType(TerraExcludeRecordTypeLookup.CAPITAL_CALL.getCode());
                    records.add(recordDtoOpposite);
                }

            }


            // Added records
            List<RealEstateGeneralLedgerFormData> addedRecods =
                    this.reGeneralLedgerFormDataRepository.getEntitiesByReportId(reportId, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
            if (addedRecods != null) {
                for (RealEstateGeneralLedgerFormData entity : addedRecods) {
                    RealEstateGeneralLedgerFormDataDto addedRecordDto = this.realEstateGeneralLedgerFormDataConverter.disassemble(entity);
                    TerraGeneratedGeneralLedgerFormDto recordDto = new TerraGeneratedGeneralLedgerFormDto();
                    recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? "TERRA A" : addedRecordDto.getTranche() == 2 ? "TERRA B" : "TERRA");
                    if (addedRecordDto.getReport() != null) {
                        recordDto.setBalanceDate(addedRecordDto.getReport().getReportDate());
                    }
                    recordDto.setFinancialStatementCategory(addedRecordDto.getFinancialStatementCategory());
                    recordDto.setChartAccountsLongDescription(addedRecordDto.getTerraNICChartOfAccountsName());
                    recordDto.setNbAccountNumber(addedRecordDto.getNbAccountNumber());
                    String entityName = StringUtils.isNotEmpty(addedRecordDto.getEntityName()) ? " " + addedRecordDto.getEntityName() : "";
                    recordDto.setNicAccountName(addedRecordDto.getNicAccountName() + entityName);
                    recordDto.setSubscriptionRedemptionEntity(entityName);
                    recordDto.setGLAccountBalance(addedRecordDto.getGLAccountBalance());
                    recordDto.setAdded(true);
                    recordDto.setAddedRecordId(entity.getId());
                    records.add(recordDto);
                }
            }

            setTerraNICChartOfAccounts(records);
            Collections.sort(records);


            responseDto.setRecords(records);
            responseDto.setStatus(ResponseStatusType.SUCCESS);
            return responseDto;
        }catch (Exception ex){
            logger.error("Error loading Terra Generated form: report id " + reportId, ex);
            return null;
        }
    }

    @Override
    public ListResponseDto getTerraGeneratedFormWithoutExcluded(Long reportId) {
        ListResponseDto responseDto = getTerraGeneratedForm(reportId);
        if(responseDto.getStatus() == null || responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
            if(responseDto.getRecords() != null){
                List<TerraGeneratedGeneralLedgerFormDto> newRecords = new ArrayList<>();
                for(TerraGeneratedGeneralLedgerFormDto record: (List<TerraGeneratedGeneralLedgerFormDto>) responseDto.getRecords()){
                    if(record.getExcludeFromTerraCalculation() == null || !record.getExcludeFromTerraCalculation().booleanValue()){
                        newRecords.add(record);
                    }
                }
                responseDto.setRecords(newRecords);
            }
        }

        return responseDto;
    }

    private void setTerraNICChartOfAccounts(List<TerraGeneratedGeneralLedgerFormDto> records){
        if(records != null) {
            for(GeneratedGeneralLedgerFormDto record: records){
                if(record.getNicAccountName() == null) {
                    TerraNICChartOfAccounts nicChartOfAccounts =
                            this.terraNICChartOfAccountsRepository.findByTerraChartOfAccountsNameAndAddable(record.getChartAccountsLongDescription(), false);
                    if (nicChartOfAccounts != null) {
                        record.setNicAccountName(nicChartOfAccounts.getNicReportingChartOfAccounts().getNameRu());
                        if (nicChartOfAccounts.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                            record.setNbAccountNumber(nicChartOfAccounts.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                        }
                    }else{
                        // no match found
                        if(record.getChartAccountsLongDescription().equalsIgnoreCase("Investment in Portfolio Fund")){
                            record.setNicAccountName("Инвестиции в фонд недвижимости " + record.getSubscriptionRedemptionEntity());
                            record.setNbAccountNumber("2033.010");
                        }
                    }
                }
            }
        }
    }

    private boolean isExpenses(TerraBalanceSheetRecordDto dto){
        if(dto != null){
            if(dto.getName().contains("expense") || dto.getName().contains("fee") ||
                    dto.getName().contains("Expense") || dto.getName().contains("Fee")){
                return true;
            }
        }
        return false;
    }

    private boolean isIncome(TerraBalanceSheetRecordDto dto){
        if(dto != null){
            if(dto.getName().startsWith("Unrealised p/l")){
                return true;
            }
        }
        return false;
    }

    private boolean isLiabilities(TerraBalanceSheetRecordDto dto){
        if(dto != null){
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase("LIABLTY")){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private boolean isAssets(TerraBalanceSheetRecordDto dto){
        if(dto != null){
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase("ASSETS")){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private List<TerraGeneratedGeneralLedgerFormDto> processTerraBalanceSheet(List<TerraBalanceSheetRecordDto> balanceRecords){
        List<TerraGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(balanceRecords != null){
            for(TerraBalanceSheetRecordDto balanceRecord: balanceRecords){
                if(balanceRecord.getTotalSum() != null && balanceRecord.getTotalSum().booleanValue()){
                    // skip total sums
                    continue;
                }
                if(balanceRecord.getType() != null && balanceRecord.getType().getCode().equalsIgnoreCase("ASSETS")){
                    // skip Assets
                    continue;
                }
                TerraGeneratedGeneralLedgerFormDto record = new TerraGeneratedGeneralLedgerFormDto();
                record.setId(balanceRecord.getId());
                if(isLiabilities(balanceRecord)){
                    record.setFinancialStatementCategory("L");
                }else if(isIncome(balanceRecord)){
                    record.setFinancialStatementCategory("I");
                }else if(isExpenses(balanceRecord)){
                    record.setFinancialStatementCategory("X");
                }else{

                }

                record.setAcronym("TERRA");
                record.setBalanceDate(balanceRecord.getReport().getReportDate());
                record.setChartAccountsLongDescription(balanceRecord.getName());
                //record.setSegVal1("");
                //record.setNbAccountNumber("");
                //record.setNicAccountName("");
                Double accountBalance = balanceRecord.getValueNICKMF();
                record.setGLAccountBalance(accountBalance);
                record.setExcludeFromTerraCalculation(balanceRecord.getExcludeFromTerraCalculation());
                record.setType(TerraExcludeRecordTypeLookup.BALANCE_SHEET.getCode());
                //record.setFundCCY();
                //record.setSegValCCY();

                records.add(record);
            }
        }
        return records;
    }

    private List<TerraGeneratedGeneralLedgerFormDto> processTerraSecuritiesCost(List<TerraSecuritiesCostRecordDto> balanceRecords){
        List<TerraGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(balanceRecords != null){
            for(TerraSecuritiesCostRecordDto balanceRecord: balanceRecords){
                if(balanceRecord.getTotalSum() != null && balanceRecord.getTotalSum().booleanValue()){
                    // skip total sums
                    continue;
                }

                TerraGeneratedGeneralLedgerFormDto record = new TerraGeneratedGeneralLedgerFormDto();
                record.setId(balanceRecord.getId());
                record.setFinancialStatementCategory("A");

                record.setAcronym("TERRA");
                record.setBalanceDate(balanceRecord.getReport().getReportDate());
                record.setChartAccountsLongDescription("Investment in Portfolio Fund");
                record.setSubscriptionRedemptionEntity(balanceRecord.getName());
                record.setShortName(balanceRecord.getName());
                //record.setSegVal1("");
                //record.setNbAccountNumber("");
                //record.setNicAccountName("");
                Double accountBalance = MathUtils.multiply(balanceRecord.getMarketValueFCY(), 0.99);
                record.setGLAccountBalance(accountBalance);
                record.setExcludeFromTerraCalculation(balanceRecord.getExcludeFromTerraCalculation());
                record.setType(TerraExcludeRecordTypeLookup.SECURITIES_COST.getCode());
                //record.setFundCCY();
                //record.setSegValCCY();

                records.add(record);
            }
        }
        return records;
    }

    @Transactional // if DB operation fails, no record will be saved, i.e. no partial commits
    @Override
    public EntityListSaveResponseDto saveRealEstateGeneralLedgerFormData(RealEstateGeneralLedgerFormDataHolderDto dataHolderDto) {
        EntityListSaveResponseDto entityListSaveResponseDto = new EntityListSaveResponseDto();
        try {
            if(dataHolderDto != null && dataHolderDto.getRecords() != null){
                checkRealEstateGeneralLedgerFormData(dataHolderDto.getRecords());
                // check report status
                PeriodicReportDto periodicReport = this.periodicReportService.getPeriodicReport(dataHolderDto.getReport().getId());
                if(periodicReport == null || periodicReport.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                    Long reportId = periodicReport != null ? periodicReport.getId() : null;
                    logger.error("Cannot edit report with status 'SUBMITTED': report id " + reportId);
                    entityListSaveResponseDto.setErrorMessageEn("Cannot edit report with status 'SUBMITTED': report id ");
                    return entityListSaveResponseDto;
                }

                for(RealEstateGeneralLedgerFormDataDto dto: dataHolderDto.getRecords()){
                    RealEstateGeneralLedgerFormData entity = this.realEstateGeneralLedgerFormDataConverter.assemble(dto);
                    entity.setReport(new PeriodicReport(dataHolderDto.getReport().getId()));
                    this.reGeneralLedgerFormDataRepository.save(entity);
                }
                entityListSaveResponseDto.setSuccessMessageEn("Successfully saved records");
                return entityListSaveResponseDto;
            }
        }catch (IllegalArgumentException ex){
            entityListSaveResponseDto.setErrorMessageEn("Input validation failed. " + ex.getMessage());
        }catch (Exception ex){
            logger.error("Error saving Terra GL Form data", ex);
            entityListSaveResponseDto.setErrorMessageEn("Error saving Terra GL Form data");
        }
        return entityListSaveResponseDto;
    }

    private void checkRealEstateGeneralLedgerFormData(List<RealEstateGeneralLedgerFormDataDto> records){
        if(records != null){
//            double totalAssets = 0.0;
//            double totalOther = 0.0;
            for(RealEstateGeneralLedgerFormDataDto record: records){
//                if(record.getTranche() != 1 && record.getTranche() != 2){
//                    throw new IllegalArgumentException("Tranche value invalid : " + record.getTranche() + "; expected values 1, 2");
//                }
//                if(!isValidFinancialStatementCategory(record.getFinancialStatementCategory())){
//                    throw new IllegalArgumentException("Financial statement category value invalid : '" + record.getFinancialStatementCategory() + "'; expected values A, L, E, X, I");
//                }
                if(StringUtils.isEmpty(record.getTerraNICChartOfAccountsName())){
                    throw new IllegalArgumentException("Chart oof Accounts Name value missing");
                }else{
                    // check valid ?
                }
                if(record.getGLAccountBalance() == null){
                    throw new IllegalArgumentException("Account Balance value missing");
                }
//                if(record.getFinancialStatementCategory().equalsIgnoreCase("A")){
//                    totalAssets += record.getGLAccountBalance().doubleValue();
//                }else{
//                    totalOther += record.getGLAccountBalance().doubleValue();
//                }
            }

//            double difference = totalAssets - totalOther;
//            if(difference > 2 || difference < -2){
//                throw new IllegalArgumentException("Total Assets (" + totalAssets + ") not equal to Total Other (" + totalOther + "); difference can be between -1 and 1");
//            }
        }

    }

    @Override
    public boolean deleteRealEstateGeneralLedgerFormDataRecordById(Long recordId) {
        Long reportId = null;
        try {
            RealEstateGeneralLedgerFormData entity = this.reGeneralLedgerFormDataRepository.findOne(recordId);
            if(entity == null){
                logger.error("No record found to delete: record id " + recordId);
                return false;
            }
            reportId = entity.getReport().getId();
            if (entity.getReport().getStatus() != null) {
                if(entity.getReport().getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                    return false;
                }
                this.reGeneralLedgerFormDataRepository.delete(entity);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error deleting Real Estate General Ledger Record: report id " + reportId +  ", record id " + recordId, ex);
        }
        return false;
    }

    @Override
    public List<TerraGeneratedGeneralLedgerFormDto> getTerraGLAddedRecordsPreviousMonth(Long reportId) {
        try {
            List<TerraGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
            PeriodicReportDto currentReport = this.periodicReportService.getPeriodicReport(reportId);
            if (currentReport != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
                PeriodicReportDto previousReport = this.periodicReportService.findReportByReportDate(previousDate);
                List<RealEstateGeneralLedgerFormData> addedRecods =
                        this.reGeneralLedgerFormDataRepository.getEntitiesByReportId(previousReport.getId(), new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
                if (addedRecods != null) {
                    for (RealEstateGeneralLedgerFormData entity : addedRecods) {
                        RealEstateGeneralLedgerFormDataDto addedRecordDto = this.realEstateGeneralLedgerFormDataConverter.disassemble(entity);
                        TerraGeneratedGeneralLedgerFormDto recordDto = new TerraGeneratedGeneralLedgerFormDto();
                        recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? "TERRA A" : addedRecordDto.getTranche() == 2 ? "TERRA B" : "TERRA");
                        if (addedRecordDto.getReport() != null) {
                            recordDto.setBalanceDate(addedRecordDto.getReport().getReportDate());
                        }
                        recordDto.setFinancialStatementCategory(addedRecordDto.getFinancialStatementCategory());
                        recordDto.setChartAccountsLongDescription(addedRecordDto.getTerraNICChartOfAccountsName());
                        recordDto.setNbAccountNumber(addedRecordDto.getNbAccountNumber());
                        String entityName = addedRecordDto.getEntityName() != null ? addedRecordDto.getEntityName() : "";
                        recordDto.setNicAccountName(addedRecordDto.getNicAccountName() + entityName);
                        recordDto.setSubscriptionRedemptionEntity(entityName);
                        recordDto.setGLAccountBalance(addedRecordDto.getGLAccountBalance());
                        recordDto.setAdded(true);
                        recordDto.setAddedRecordId(entity.getId());
                        records.add(recordDto);
                    }
                }

                setTerraNICChartOfAccounts(records);
            }
            return records;
        }catch(Exception ex){
            logger.error("Error loading Tarragon Generated form: report id " + reportId, ex);
            return null;
        }
    }

    @Override
    public boolean excludeIncludeTerraRecord(ExcludeTerraRecordDto excludeTerraRecordDto) {
        try {
            if (excludeTerraRecordDto.getType() == null) {
                return false;
            }
            if (excludeTerraRecordDto.getType().equalsIgnoreCase(TerraExcludeRecordTypeLookup.CAPITAL_CALL.getCode())) {
                return this.reserveCalculationService.excludeIncludeRecord(excludeTerraRecordDto.getRecordId(), excludeTerraRecordDto.getName());
            } else if (excludeTerraRecordDto.getType().equalsIgnoreCase(TerraExcludeRecordTypeLookup.BALANCE_SHEET.getCode())) {
                ReportingREBalanceSheet entity = this.balanceSheetRepository.findOne(excludeTerraRecordDto.getRecordId());
                boolean currentValue = entity.getExcludeFromTerraCalculation() == null ? false : entity.getExcludeFromTerraCalculation().booleanValue();
                entity.setExcludeFromTerraCalculation(!currentValue);
                this.balanceSheetRepository.save(entity);
                return true;
            } else if (excludeTerraRecordDto.getType().equalsIgnoreCase(TerraExcludeRecordTypeLookup.SECURITIES_COST.getCode())) {
                ReportingRESecuritiesCost entity = this.securitiesCostRepository.findOne(excludeTerraRecordDto.getRecordId());
                boolean currentValue = entity.getExcludeFromTerraCalculation() == null ? false : entity.getExcludeFromTerraCalculation().booleanValue();
                entity.setExcludeFromTerraCalculation(!currentValue);
                this.securitiesCostRepository.save(entity);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error when including/excluding Terra record: id=" + excludeTerraRecordDto.getRecordId(), ex);
            return false;
        }
        return false;
    }

    @Override
    public TerraCombinedDataHolderDto getTerraCombinedParsedData(Long reportId) {

        List<TerraBalanceSheetRecordDto> balanceSheetRecords = getBalanceSheetRecords(reportId);
        List<TerraProfitLossRecordDto> profitLossRecords = getProfitLossRecords(reportId);
        List<TerraSecuritiesCostRecordDto> securitiesCostRecords = getSecuritiesCostRecords(reportId);

        TerraCombinedDataHolderDto holderDto = new TerraCombinedDataHolderDto();
        holderDto.setBalanceSheetRecords(balanceSheetRecords);
        holderDto.setProfitLossRecords(profitLossRecords);
        holderDto.setSecuritiesCostRecords(securitiesCostRecords);
        holderDto.setReport(this.periodicReportService.getPeriodicReport(reportId));
        return holderDto;
    }


}