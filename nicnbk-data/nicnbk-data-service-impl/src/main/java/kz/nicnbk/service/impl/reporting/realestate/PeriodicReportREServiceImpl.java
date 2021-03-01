package kz.nicnbk.service.impl.reporting.realestate;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.lookup.REBalanceTypeRepository;
import kz.nicnbk.repo.api.lookup.REProfitLossTypeRepository;
import kz.nicnbk.repo.api.reporting.realestate.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.reporting.*;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.realestate.*;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
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

    @Autowired
    private REGeneralLedgerBalanceService reGeneralLedgerBalanceService;


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
    public boolean deleteGeneralLedgerByReportId(Long reportId) {
        try {
            return reGeneralLedgerBalanceService.deleteByReportId(reportId);
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
    public ListResponseDto getTerraGeneralLedgerFormDataWithoutExcluded(Long reportId){
        ListResponseDto responseDto = new ListResponseDto();
        List<TerraGeneratedGeneralLedgerFormDto> recordsWithoutExcluded = new ArrayList<>();
        ListResponseDto allGLRecordsResponse = getTerraGeneralLedgerFormData(reportId);
        if(allGLRecordsResponse != null && allGLRecordsResponse.getStatus() == ResponseStatusType.SUCCESS){
            List<TerraGeneratedGeneralLedgerFormDto> records = allGLRecordsResponse.getRecords();
            for(TerraGeneratedGeneralLedgerFormDto record: records){
                if(record.getExcludeFromTerraCalculation() == null || !record.getExcludeFromTerraCalculation().booleanValue()){
                    recordsWithoutExcluded.add(record);
                }
            }
            responseDto.setStatus(ResponseStatusType.SUCCESS);
            responseDto.setRecords(recordsWithoutExcluded);
        }else if(allGLRecordsResponse.getStatus() == ResponseStatusType.FAIL){
            return allGLRecordsResponse;
        }else{
            responseDto.setStatus(ResponseStatusType.FAIL);
        }

        return responseDto;
    }

    @Override
    public ListResponseDto getTerraGeneralLedgerFormData(Long reportId){
        try {
            ListResponseDto responseDto = new ListResponseDto();

            PeriodicReportDto report = this.periodicReportService.getPeriodicReport(reportId);
            if(report == null){
                logger.error("Error getting Terra General Ledger Form: report is not found for report id " + reportId);
                responseDto.setErrorMessageEn("Error getting Terra General Ledger Form: report is not found for report id " + reportId);
                return responseDto;
            }

            List<TerraGeneratedGeneralLedgerFormDto> records = new ArrayList<>();

            // Balance sheet
            TerraGeneralLedgerDataHolderDto generalLedgerDataHolderDto = getTerraGeneralLedgerData(reportId);
            records.addAll(processTerraGeneralLedger(generalLedgerDataHolderDto));

            // From capital calls
            if(!DateUtils.isDecember(report.getReportDate())) {
                // December report has December data
                List<ReserveCalculationDto> reserveCalculationRecords =
                        this.reserveCalculationService.getReserveCalculationsForMonth(ReserveCalculationsExpenseTypeLookup.ADD.getCode(), report.getReportDate(), true);

                // Distributions
                List<ReserveCalculationDto> reserveCalculationRecordsDistributions =
                        this.reserveCalculationService.getReserveCalculationsForMonth(ReserveCalculationsExpenseTypeLookup.RETURN.getCode(), report.getReportDate(), true);
                if (reserveCalculationRecordsDistributions != null) {
                    for (ReserveCalculationDto reserveCalculationDto : reserveCalculationRecordsDistributions) {
                        if (reserveCalculationDto.getSource() != null && reserveCalculationDto.getSource().getCode().startsWith("TERR") &&
                                reserveCalculationDto.getRecipient() != null && reserveCalculationDto.getRecipient().getCode().startsWith("NICK")) {
                            // Distribution from Terra to NICK MF
                            reserveCalculationRecords.add(reserveCalculationDto);
                        }
                    }
                }
                if (reserveCalculationRecords != null) {
                    for (ReserveCalculationDto reserveCalculationDto : reserveCalculationRecords) {
                        if (reserveCalculationDto.getRecipient() == null || !reserveCalculationDto.getRecipient().getCode().startsWith("TERR")) {
                            if (reserveCalculationDto.getSource() == null || !reserveCalculationDto.getSource().getCode().startsWith("TERR") ||
                                    reserveCalculationDto.getRecipient() == null || !reserveCalculationDto.getRecipient().getCode().startsWith("NICK")) {
                                continue;
                            }
                        }
                        TerraGeneratedGeneralLedgerFormDto recordDto = new TerraGeneratedGeneralLedgerFormDto();
                        recordDto.setId(reserveCalculationDto.getId());
                        String acronym = reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TERRA_A.getCode()) ? "Terra A" :
                                reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TERRA_B.getCode()) ? "Terra B" : "Terra";
//                    if(acronym.equalsIgnoreCase("TERRA")) {
//                        logger.error("Error generating Terra GL Form: capital call recipient is specified as 'Terra', must be either 'Terra A' or 'Terra B'");
//                        responseDto.setErrorMessageEn("Error generating Terra GL Form: capital call recipient is specified as 'Terra', must be either 'Terra A' or 'Terra B'");
//                        return responseDto;
//                    }
                        recordDto.setAcronym(acronym);
                        recordDto.setBalanceDate(report.getReportDate());

                        Double amount = reserveCalculationDto.getAmountToSPV() != null ?
                                reserveCalculationDto.getAmountToSPV() : reserveCalculationDto.getAmount();
                        if (reserveCalculationDto.getExpenseType() != null && reserveCalculationDto.getExpenseType().getCode().equalsIgnoreCase(ReserveCalculationsExpenseTypeLookup.RETURN.getCode())) {
                            // Subtract Distributions
                            amount = MathUtils.subtract(0.0, amount);
                        }
                        recordDto.setGLAccountBalance(amount);
                        recordDto.setAdded(false);
                        recordDto.setEditable(false);

                        recordDto.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());
                        recordDto.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CASH_ADJ.getNameEn());
                        recordDto.setNbAccountNumber(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getCodeNBRK());
                        recordDto.setNicAccountName(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getNameRu());


                        recordDto.setExcludeFromTerraCalculation(reserveCalculationDto.getExcludeFromTerraCalculation() != null
                                && reserveCalculationDto.getExcludeFromTerraCalculation().booleanValue());

                        recordDto.setType(TerraRecordTypeLookup.CAPITAL_CALL.getCode());
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
                        recordDtoOpposite.setType(TerraRecordTypeLookup.CAPITAL_CALL.getCode());
                        records.add(recordDtoOpposite);
                    }

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

            //setTerraNICChartOfAccounts(records);
            Collections.sort(records);


            responseDto.setRecords(records);
            responseDto.setStatus(ResponseStatusType.SUCCESS);
            return responseDto;
        }catch (Exception ex){
            logger.error("Error loading Terra Generated form: report id " + reportId, ex);
            return null;
        }
    }

    private List<TerraGeneratedGeneralLedgerFormDto>  processTerraGeneralLedger(TerraGeneralLedgerDataHolderDto generalLedgerDataHolderDto){
        List<TerraGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(generalLedgerDataHolderDto != null && generalLedgerDataHolderDto.getRecords() != null){
            Map<String, Double> unrealizedGainMap = new HashMap<>();
            for(TerraGeneralLedgerBalanceRecordDto recordDto: generalLedgerDataHolderDto.getRecords()){
                TerraGeneratedGeneralLedgerFormDto record = new TerraGeneratedGeneralLedgerFormDto(recordDto);
                if(record.getGLAccountBalance() == null || record.getGLAccountBalance().doubleValue() == 0){
                    // skip zero balance records
                    continue;
                }
                record.setType(TerraRecordTypeLookup.GENERAL_LEDGER.getCode());
                record.setExcludeFromTerraCalculation(recordDto.getExcludeFromTerraCalculation());
                if(recordDto.getGlSubclass() != null && recordDto.getGlSubclass().equalsIgnoreCase(TerraNICChartAccountsLookup.UNREALIZED_GAIN.getNameEn()) &&
                        recordDto.getPortfolioFund() != null && recordDto.getAccountBalanceNICKMF() != null){
                    if(unrealizedGainMap.get(recordDto.getPortfolioFund()) == null){
                        unrealizedGainMap.put(recordDto.getPortfolioFund(), recordDto.getAccountBalanceNICKMF());
                    }else {
                        Double value = unrealizedGainMap.get(recordDto.getPortfolioFund());
                        unrealizedGainMap.put(recordDto.getPortfolioFund(), MathUtils.add(value, recordDto.getAccountBalanceNICKMF()));
                    }
                }
                record.setId(recordDto.getId());
                List<TerraNICChartOfAccounts> chartOfAccounts = this.terraNICChartOfAccountsRepository.findByTerraChartOfAccountsName(recordDto.getGlSubclass());
                boolean exclude = false;
                if(chartOfAccounts != null && !chartOfAccounts.isEmpty()){
                    for(TerraNICChartOfAccounts entity: chartOfAccounts) {
                        if(entity.getNicReportingChartOfAccounts() != null &&
                                entity.getNicReportingChartOfAccounts().getCode().equalsIgnoreCase(NICChartAccountsTypeLookup.NOMATCH.getCode())){
                            // no match, exclude
                            exclude = true;
                            break;
                        }
                        if (record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0) {
                            if (entity.getChartAccountsType() != null &&
                                    (entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode()) ||
                                            entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.ALL.getCode()))) {
                                if(hasOtherEntityName(entity)) {
                                    record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + " " + record.getShortName());
                                }else{
                                    record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                }
                                if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                    record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                }
                                break;
                            }
                        } else if (record.getGLAccountBalance() != null && record.getGLAccountBalance() >= 0) {
                            if (entity.getChartAccountsType() != null &&
                                    (entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode()) ||
                                            entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.ALL.getCode()))) {
                                if(hasOtherEntityName(entity)) {
                                    record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + " " + record.getShortName());
                                }else{
                                    record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                }
                                if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                    record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                }
                                break;
                            }
                        }
                    }
                }
                if(!exclude) {
                    records.add(record);
                }
            }
            for(TerraGeneratedGeneralLedgerFormDto record: records){
                if(record.getChartAccountsLongDescription() != null &&
                        record.getChartAccountsLongDescription().equalsIgnoreCase(TerraNICChartAccountsLookup.SECURITY.getNameEn())){
                    if(unrealizedGainMap.get(record.getShortName()) != null){
                        record.setGLAccountBalance(MathUtils.add(record.getGLAccountBalance(), unrealizedGainMap.get(record.getShortName())));
                    }
                }
            }
        }
        return records;
    }

//    @Override
//    public ListResponseDto getTerraGeneratedForm(Long reportId){
//        try {
//            ListResponseDto responseDto = new ListResponseDto();
//
//            PeriodicReportDto report = this.periodicReportService.getPeriodicReport(reportId);
//            if(report == null){
//                logger.error("Error getting Terra Generated GL Form: report is not found for report id " + reportId);
//                responseDto.setErrorMessageEn("Error getting Terra Generated GL Form: report is not found for report id " + reportId);
//                return responseDto;
//            }
//
//            List<TerraGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
//
//            // Balance sheet
//            List<TerraBalanceSheetRecordDto> balanceRecords = getBalanceSheetRecords(reportId);
//            records.addAll(processTerraBalanceSheet(balanceRecords));
//
//            // Securities cost
//            List<TerraSecuritiesCostRecordDto> securitiesCostRecords = getSecuritiesCostRecords(reportId);
//            records.addAll(processTerraSecuritiesCost(securitiesCostRecords));
//
//            // From capital calls
//
//            List<ReserveCalculationDto> reserveCalculationRecords =
//                    this.reserveCalculationService.getReserveCalculationsForMonth(ReserveCalculationsExpenseTypeLookup.ADD.getCode(), report.getReportDate(), true);
//            if(reserveCalculationRecords != null){
//                for(ReserveCalculationDto reserveCalculationDto: reserveCalculationRecords){
//                    if(reserveCalculationDto.getRecipient() == null || !reserveCalculationDto.getRecipient().getCode().startsWith("TERR")){
//                        continue;
//                    }
//                    TerraGeneratedGeneralLedgerFormDto recordDto = new TerraGeneratedGeneralLedgerFormDto();
//                    recordDto.setId(reserveCalculationDto.getId());
//                    String acronym = reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TERRA_A.getCode()) ? "TERRA A" :
//                            reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TERRA_B.getCode()) ? "TERRA B" : "TERRA";
//                    if(acronym.equalsIgnoreCase("TERRA")) {
//                        logger.error("Error generating Terra GL Form: capital call recipient is specified as 'Terra', must be either 'Terra A' or 'Terra B'");
//                        responseDto.setErrorMessageEn("Error generating Terra GL Form: capital call recipient is specified as 'Terra', must be either 'Terra A' or 'Terra B'");
//                        return responseDto;
//                    }
//                    recordDto.setAcronym(acronym);
//                    recordDto.setBalanceDate(report.getReportDate());
//
//                    Double amount = reserveCalculationDto.getAmountToSPV() != null ?
//                            reserveCalculationDto.getAmountToSPV() : reserveCalculationDto.getAmount();
//                    recordDto.setGLAccountBalance(amount);
//                    recordDto.setAdded(false);
//                    recordDto.setEditable(false);
//
//                    recordDto.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());
//                    recordDto.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CASH_ADJ.getNameEn());
//                    recordDto.setNbAccountNumber(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getCodeNBRK());
//                    recordDto.setNicAccountName(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getNameRu());
//
//
//                    recordDto.setExcludeFromTerraCalculation(reserveCalculationDto.getExcludeFromTerraCalculation() != null
//                            && reserveCalculationDto.getExcludeFromTerraCalculation().booleanValue());
//
//                    recordDto.setType(TerraRecordTypeLookup.CAPITAL_CALL.getCode());
//                    records.add(recordDto);
//
//                    TerraGeneratedGeneralLedgerFormDto recordDtoOpposite = new TerraGeneratedGeneralLedgerFormDto();
//                    recordDtoOpposite.setAcronym(acronym);
//                    recordDtoOpposite.setBalanceDate(report.getReportDate());
//                    recordDtoOpposite.setGLAccountBalance(MathUtils.subtract(0.0, amount));
//                    recordDtoOpposite.setAdded(false);
//                    recordDtoOpposite.setEditable(false);
//
//                    recordDtoOpposite.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.EQUITY.getCode());
//                    recordDtoOpposite.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CAPITAL_ADJ.getNameEn());
//                    recordDtoOpposite.setNbAccountNumber(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getCodeNBRK());
//                    recordDtoOpposite.setNicAccountName(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getNameRu());
//
//                    recordDtoOpposite.setExcludeFromTerraCalculation(reserveCalculationDto.getExcludeOppositeFromTerraCalculation() != null
//                            && reserveCalculationDto.getExcludeOppositeFromTerraCalculation().booleanValue());
//                    recordDtoOpposite.setId(reserveCalculationDto.getId());
//                    recordDtoOpposite.setType(TerraRecordTypeLookup.CAPITAL_CALL.getCode());
//                    records.add(recordDtoOpposite);
//                }
//
//            }
//
//
//            // Added records
//            List<RealEstateGeneralLedgerFormData> addedRecods =
//                    this.reGeneralLedgerFormDataRepository.getEntitiesByReportId(reportId, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
//            if (addedRecods != null) {
//                for (RealEstateGeneralLedgerFormData entity : addedRecods) {
//                    RealEstateGeneralLedgerFormDataDto addedRecordDto = this.realEstateGeneralLedgerFormDataConverter.disassemble(entity);
//                    TerraGeneratedGeneralLedgerFormDto recordDto = new TerraGeneratedGeneralLedgerFormDto();
//                    recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? "TERRA A" : addedRecordDto.getTranche() == 2 ? "TERRA B" : "TERRA");
//                    if (addedRecordDto.getReport() != null) {
//                        recordDto.setBalanceDate(addedRecordDto.getReport().getReportDate());
//                    }
//                    recordDto.setFinancialStatementCategory(addedRecordDto.getFinancialStatementCategory());
//                    recordDto.setChartAccountsLongDescription(addedRecordDto.getTerraNICChartOfAccountsName());
//                    recordDto.setNbAccountNumber(addedRecordDto.getNbAccountNumber());
//                    String entityName = StringUtils.isNotEmpty(addedRecordDto.getEntityName()) ? " " + addedRecordDto.getEntityName() : "";
//                    recordDto.setNicAccountName(addedRecordDto.getNicAccountName() + entityName);
//                    recordDto.setSubscriptionRedemptionEntity(entityName);
//                    recordDto.setGLAccountBalance(addedRecordDto.getGLAccountBalance());
//                    recordDto.setAdded(true);
//                    recordDto.setAddedRecordId(entity.getId());
//                    records.add(recordDto);
//                }
//            }
//
//            setTerraNICChartOfAccounts(records);
//            Collections.sort(records);
//
//
//            responseDto.setRecords(records);
//            responseDto.setStatus(ResponseStatusType.SUCCESS);
//            return responseDto;
//        }catch (Exception ex){
//            logger.error("Error loading Terra Generated form: report id " + reportId, ex);
//            return null;
//        }
//    }

//    @Override
//    public ListResponseDto getTerraGeneratedFormWithoutExcluded(Long reportId) {
//        ListResponseDto responseDto = getTerraGeneratedForm(reportId);
//        if(responseDto.getStatus() == null || responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
//            if(responseDto.getRecords() != null){
//                List<TerraGeneratedGeneralLedgerFormDto> newRecords = new ArrayList<>();
//                for(TerraGeneratedGeneralLedgerFormDto record: (List<TerraGeneratedGeneralLedgerFormDto>) responseDto.getRecords()){
//                    if(record.getExcludeFromTerraCalculation() == null || !record.getExcludeFromTerraCalculation().booleanValue()){
//                        newRecords.add(record);
//                    }
//                }
//                responseDto.setRecords(newRecords);
//            }
//        }
//
//        return responseDto;
//    }

    private boolean hasOtherEntityName(TerraNICChartOfAccounts accountDto){
        return accountDto != null && accountDto.getNicReportingChartOfAccounts() != null && accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null &&
                accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode() != null &&
                (accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)
                || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1183_040)
                || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)
                || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010)
                || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010)
                || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010)
                || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010));
    }

    private void setTerraNICChartOfAccounts(List<TerraGeneratedGeneralLedgerFormDto> records){
        if(records != null) {
            for(GeneratedGeneralLedgerFormDto record: records){
                if(record.getNicAccountName() == null) {
                    List<TerraNICChartOfAccounts> nicChartOfAccounts =
                            this.terraNICChartOfAccountsRepository.findByTerraChartOfAccountsNameAndAddable(record.getChartAccountsLongDescription(), false);
                    if (nicChartOfAccounts != null && !nicChartOfAccounts.isEmpty()) {
                        if(nicChartOfAccounts.size() == 1){
                            TerraNICChartOfAccounts entity = nicChartOfAccounts.get(0);
                            if(hasOtherEntityName(entity)){
                                record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + " " + record.getSubscriptionRedemptionEntity());
                            }else {
                                record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                            }
                            if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                            }
                        }else if(nicChartOfAccounts.size() == 2){
                            boolean found = false;
                            for(TerraNICChartOfAccounts entity: nicChartOfAccounts) {
                                if (record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0) {
                                    if (entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode())) {
                                        if(hasOtherEntityName(entity)){
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + " " + record.getSubscriptionRedemptionEntity());
                                        }else {
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                        }
                                        //record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                        }
                                        found = true;
                                        break;
                                    }
                                } else if (record.getGLAccountBalance() != null && record.getGLAccountBalance() >= 0) {
                                    if (entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode())) {
                                        if(hasOtherEntityName(entity)){
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + " " + record.getSubscriptionRedemptionEntity());
                                        }else {
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                        }
                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                        }
                                    }
                                    found = true;
                                    break;
                                }
                            }
                            if(!found){
                                logger.error("Error setting Terra NIC Chart of accounts for '" + record.getChartAccountsLongDescription() + "' " +
                                        " : positiveOnly/negativeOnly flags not set properly");
                            }
                        }else{
                            logger.error("Error setting Terra NIC Chart of accounts for '" + record.getChartAccountsLongDescription() + "' " +
                                    " : more than 2 mappings found.");
                        }

//                        record.setNicAccountName(nicChartOfAccounts.getNicReportingChartOfAccounts().getNameRu());
//                        if (nicChartOfAccounts.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
//                            record.setNbAccountNumber(nicChartOfAccounts.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
//                        }
                    }else{
                        // no match found
                        if(record.getChartAccountsLongDescription().equalsIgnoreCase("Investment in Portfolio Fund")){
                            record.setNicAccountName(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT + " " + record.getSubscriptionRedemptionEntity());
                            record.setNbAccountNumber(PeriodicReportConstants.ACC_NUM_1123_010);
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

    private boolean isEquity(TerraBalanceSheetRecordDto dto){
        if(dto != null){
            if(dto.getName().equalsIgnoreCase("Partnership capital - book value")){
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
                TerraGeneratedGeneralLedgerFormDto record = new TerraGeneratedGeneralLedgerFormDto();

                // TODO: decide what to do with 'Bank accounts'? Skipp ASSETS?
                if(balanceRecord.getName().equalsIgnoreCase("Bank accounts")){
                    record.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());
                }else if(balanceRecord.getType() != null && balanceRecord.getType().getCode().equalsIgnoreCase("ASSETS")){
                    // skip Assets
                    continue;
                }
                record.setId(balanceRecord.getId());
                if(isLiabilities(balanceRecord)){
                    record.setFinancialStatementCategory("L");
                }else if(isIncome(balanceRecord)){
                    record.setFinancialStatementCategory("I");
                }else if(isExpenses(balanceRecord)){
                    record.setFinancialStatementCategory("X");
                }else if(isEquity(balanceRecord)){
                    record.setFinancialStatementCategory("E");
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
                record.setType(TerraRecordTypeLookup.BALANCE_SHEET.getCode());
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
                record.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());

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
                record.setType(TerraRecordTypeLookup.SECURITIES_COST.getCode());
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
                if(previousReport != null) {
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
    public boolean excludeIncludeTerraRecord(ExcludeTerraRecordDto excludeTerraRecordDto, String username) {
        try {
            if (excludeTerraRecordDto.getType() == null) {
                return false;
            }
            boolean result = false;
            if (excludeTerraRecordDto.getType().equalsIgnoreCase(TerraRecordTypeLookup.CAPITAL_CALL.getCode())) {
                result = this.reserveCalculationService.excludeIncludeRecord(excludeTerraRecordDto.getRecordId(), excludeTerraRecordDto.getName(), ExcludeRecordTypeLookup.TERRA);
            }else if (excludeTerraRecordDto.getType().equalsIgnoreCase(TerraRecordTypeLookup.GENERAL_LEDGER.getCode())) {
                result= this.reGeneralLedgerBalanceService.excludeIncludeRecord(excludeTerraRecordDto.getRecordId());
                //result = true;
            }
//            else if (excludeTerraRecordDto.getType().equalsIgnoreCase(TerraExcludeRecordTypeLookup.BALANCE_SHEET.getCode())) {
//                ReportingREBalanceSheet entity = this.balanceSheetRepository.findOne(excludeTerraRecordDto.getRecordId());
//                boolean currentValue = entity.getExcludeFromTerraCalculation() == null ? false : entity.getExcludeFromTerraCalculation().booleanValue();
//                entity.setExcludeFromTerraCalculation(!currentValue);
//                this.balanceSheetRepository.save(entity);
//                result = true;
//            } else if (excludeTerraRecordDto.getType().equalsIgnoreCase(TerraExcludeRecordTypeLookup.SECURITIES_COST.getCode())) {
//                ReportingRESecuritiesCost entity = this.securitiesCostRepository.findOne(excludeTerraRecordDto.getRecordId());
//                boolean currentValue = entity.getExcludeFromTerraCalculation() == null ? false : entity.getExcludeFromTerraCalculation().booleanValue();
//                entity.setExcludeFromTerraCalculation(!currentValue);
//                this.securitiesCostRepository.save(entity);
//                result = true;
//            }
            if(result){
                logger.info("Successfully excluded/included Terra record: id=" + excludeTerraRecordDto.getRecordId() +
                        ", type=" + excludeTerraRecordDto.getType() + ", name='" + excludeTerraRecordDto.getName() + "' [user " + username + "]");
            }else{
                logger.error("Error excluding/including Terra record: id=" + excludeTerraRecordDto.getRecordId() +
                        ", type=" + excludeTerraRecordDto.getType() + ", name='"  + excludeTerraRecordDto.getName() + "' [user " + username + "]");
            }
            return result;
        }catch (Exception ex){
            logger.error("Error when including/excluding Terra record: id=" + excludeTerraRecordDto.getRecordId()
                    + ", type=" + excludeTerraRecordDto.getType(), ex);
            return false;
        }
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

    @Override
    public TerraGeneralLedgerDataHolderDto getTerraGeneralLedgerData(Long reportId){
        List<TerraGeneralLedgerBalanceRecordDto> records = this.reGeneralLedgerBalanceService.getRecordsByReportId(reportId);

        TerraGeneralLedgerDataHolderDto holderDto = new TerraGeneralLedgerDataHolderDto();
        holderDto.setRecords(records);
        holderDto.setReport(this.periodicReportService.getPeriodicReport(reportId));
        return holderDto;
    }

    @Override
    public TerraGeneralLedgerDataHolderDto getTerraGeneralLedgerDataWithoutExcluded(Long reportId){
        TerraGeneralLedgerDataHolderDto holderDto = new TerraGeneralLedgerDataHolderDto();

        TerraGeneralLedgerDataHolderDto terraGLAll = getTerraGeneralLedgerData(reportId);
        if(terraGLAll != null && terraGLAll.getRecords() != null) {
            List<TerraGeneralLedgerBalanceRecordDto> records = new ArrayList<>();
            for(TerraGeneralLedgerBalanceRecordDto recordDto: terraGLAll.getRecords()){
                if(recordDto.getExcludeFromTerraCalculation() == null || !recordDto.getExcludeFromTerraCalculation().booleanValue()){
                    records.add(recordDto);
                }
            }
            holderDto.setRecords(records);
            holderDto.setReport(this.periodicReportService.getPeriodicReport(reportId));
        }

        return holderDto;
    }

    @Override
    public boolean existBalanceEntityWithType(String code) {
        int count = this.balanceSheetRepository.getEntitiesCountByType(code);
        return count > 0;
    }

    @Override
    public boolean existProfitLossEntityWithType(String code) {
        int count = this.profitLossRepository.getEntitiesCountByType(code);
        return count > 0;
    }


}
