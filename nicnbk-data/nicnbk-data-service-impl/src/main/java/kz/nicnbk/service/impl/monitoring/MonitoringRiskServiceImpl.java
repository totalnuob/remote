package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.risk.AllocationBySubStrategyRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.PortfolioVarLookup;
import kz.nicnbk.repo.model.risk.AllocationBySubStrategy;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.monitoring.MonitoringHedgeFundService;
import kz.nicnbk.service.api.monitoring.MonitoringRiskService;
import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFITDService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFPortfolioRiskService;
import kz.nicnbk.service.api.risk.RiskStressTestsService;
import kz.nicnbk.service.converter.risk.AllocationBySubStrategyEntityConverter;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.*;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.risk.PortfolioVarValueDto;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class MonitoringRiskServiceImpl implements MonitoringRiskService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringRiskServiceImpl.class);
    public static final Date SINGULAR_PORTFOLIO_START_DATE = DateUtils.getDate("31.08.2015");

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private HFGeneralLedgerBalanceService hfGeneralLedgerBalanceService;

    @Autowired
    private NicPortfolioService nicPortfolioService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private RiskStressTestsService riskStressTestsService;

    @Autowired
    private HFITDService hfitdService;

    @Autowired
    private MonitoringHedgeFundService hfService;

    @Autowired
    HFPortfolioRiskService hfPortfolioRiskService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AllocationBySubStrategyRepository repository;

    @Autowired
    private FileService fileService;

    @Autowired
    private AllocationBySubStrategyEntityConverter converter;


    @Override
    public MonitoringRiskHedgeFundReportDto getMonthlyHedgeFundReport(MonitoringRiskReportSearchParamsDto searchParamsDto) {

        MonitoringRiskHedgeFundReportDto reportDto = new MonitoringRiskHedgeFundReportDto();//getDummyMonthlyHedgeFundReport();

        NicPortfolioResultDto nicPortfolioResultDto = this.nicPortfolioService.get();

        // Top fund allocations
        ListResponseDto topFundAllocationsResponse = getHedgeFundsTopAllocations(searchParamsDto.getDate());
        if(topFundAllocationsResponse != null) {
            reportDto.setTopFundAllocationsWarning(topFundAllocationsResponse.getWarningMessageEn());

            if(!topFundAllocationsResponse.isStatusOK()){
                reportDto.setTopFundAllocationsError(topFundAllocationsResponse.getErrorMessageEn());
            }
            List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = topFundAllocationsResponse.getRecords();
            reportDto.setTopFundAllocations(topFundAllocations);
        }

        //Sub-strategy allocations
        ListResponseDto subStrategyAllocationsResponse = getAllocationBySubStrategy(searchParamsDto.getDate());
        if (subStrategyAllocationsResponse != null) {
            reportDto.setSubStrategyAllocationsWarning(topFundAllocationsResponse.getWarningMessageEn());

            if (!subStrategyAllocationsResponse.isStatusOK()) {
                reportDto.setSubStrategyAllocationsError(subStrategyAllocationsResponse.getErrorMessageEn());
            }
            List<MonitoringRiskHedgeFundAllocationSubStrategyDto> subStrategyAllocations = subStrategyAllocationsResponse.getRecords();
            reportDto.setSubStrategyAllocations(subStrategyAllocations);
        }

        // Factor Betas
        ListResponseDto factorBetasResponse = getHedgeFundsFactorBetas(searchParamsDto.getDate(),nicPortfolioResultDto);
        if(!factorBetasResponse.isStatusOK()){
            reportDto.setFactorBetasError(factorBetasResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundBetaFactorDto> factorBetas = factorBetasResponse.getRecords();
        reportDto.setFactorBetas(factorBetas);


        // Performance summary
        Date dateTo = DateUtils.getLastDayOfCurrentMonth(searchParamsDto.getDate());
        Date dateFrom12M = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(searchParamsDto.getDate(), -11));
        ListResponseDto performance12MResponsePortfolioA = null;
        ListResponseDto performance12MResponsePortfolioB = null;
        ListResponseDto performance12MResponseHfriFoF = null;
        ListResponseDto performance12MResponseHfriAWC = null;
        if(nicPortfolioResultDto != null && nicPortfolioResultDto.getNicPortfolioDtoList() != null){
            List<DateDoubleValue> valuesA = new ArrayList<>();
            List<DateDoubleValue> valuesB = new ArrayList<>();
            for(NicPortfolioDto dto: nicPortfolioResultDto.getNicPortfolioDtoList()){
                DateDoubleValue valueA = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getHedgeFundsClassAMtd());
                DateDoubleValue valueB = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getHedgeFundsClassBMtd());
                valuesA.add(valueA);
                valuesB.add(valueB);
            }

            performance12MResponsePortfolioA = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesA, false);
            performance12MResponsePortfolioB = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesB, false);
        }


        String[] benchmarkCodesHFRIFOF = {BenchmarkLookup.HFRIFOF.getCode()};
        String[] benchmarkCodesHFRIAWC = {BenchmarkLookup.HFRIAWC.getCode()};
        List<BenchmarkValueDto> hfrifof12M = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom12M, dateTo, 10, benchmarkCodesHFRIFOF);
        List<BenchmarkValueDto> hfriawc12M = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom12M, dateTo, 10, benchmarkCodesHFRIAWC);
        if(hfrifof12M != null && !hfrifof12M.isEmpty()){
            List<DateDoubleValue> valuesHfriFoF = new ArrayList<>();
            for(BenchmarkValueDto dto: hfrifof12M){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getReturnValue());
                valuesHfriFoF.add(value);
            }

            performance12MResponseHfriFoF = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesHfriFoF, true);
        }
        if (hfriawc12M != null && !hfriawc12M.isEmpty()){
            List<DateDoubleValue> valuesHfriAWC = new ArrayList<>();
            for (BenchmarkValueDto dto : hfriawc12M) {
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getReturnValue());
                valuesHfriAWC.add(value);
            }
            performance12MResponseHfriAWC = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesHfriAWC, true);
        }

        ListResponseDto mergedPortfolioPerformance12MResponse = mergePortfolioPerformanceResponses(performance12MResponsePortfolioA, performance12MResponsePortfolioB);
        ListResponseDto mergedBenchmarkPerformance12mResponse = mergeBenchmarkPerformanceResponses(performance12MResponseHfriFoF, performance12MResponseHfriAWC);
        ListResponseDto mergedPerformance12MResponse = mergePerformanceResponses(mergedPortfolioPerformance12MResponse, mergedBenchmarkPerformance12mResponse);
        if(!mergedPerformance12MResponse.isStatusOK()){
            reportDto.setPerformanceError(mergedPerformance12MResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundPerformanceRecordDto> performance12M = mergedPerformance12MResponse.getRecords();
        reportDto.setPerformance12M(performance12M);


        // Performance since inception
        Date dateFromSI = DateUtils.getLastDayOfCurrentMonth(SINGULAR_PORTFOLIO_START_DATE);
        ListResponseDto performanceSIResponsePortfolioA = null;
        ListResponseDto performanceSIResponsePortfolioB = null;
        ListResponseDto performanceSIResponseHfriFoF = null;
        ListResponseDto performanceSIResponseHfriAWC = null;
        if(nicPortfolioResultDto != null && nicPortfolioResultDto.getNicPortfolioDtoList() != null){
            List<DateDoubleValue> valuesA = new ArrayList<>();
            List<DateDoubleValue> valuesB = new ArrayList<>();
            for(NicPortfolioDto dto: nicPortfolioResultDto.getNicPortfolioDtoList()){
                DateDoubleValue valueA = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getHedgeFundsClassAMtd());
                DateDoubleValue valueB = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getHedgeFundsClassBMtd());
                valuesA.add(valueA);
                valuesB.add(valueB);
            }

            performanceSIResponsePortfolioA = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesA, false);
            performanceSIResponsePortfolioB = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesB, false);
        }

        List<BenchmarkValueDto> hfriFoFSI = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSI, dateTo, 10, benchmarkCodesHFRIFOF);
        List<BenchmarkValueDto> hfriAWCSI = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSI, dateTo, 10, benchmarkCodesHFRIAWC);
        if(hfriFoFSI != null && !hfriFoFSI.isEmpty()){
            List<DateDoubleValue> valuesHfriFoF = new ArrayList<>();
            for(BenchmarkValueDto dto: hfriFoFSI){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getReturnValue());
                valuesHfriFoF.add(value);
            }

            performanceSIResponseHfriFoF = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesHfriFoF, true);
        }
        if (hfriAWCSI != null && !hfriAWCSI.isEmpty()) {
            List<DateDoubleValue> valuesHfriAWC = new ArrayList<>();
            for (BenchmarkValueDto dto: hfriAWCSI){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getReturnValue());
            }
            performanceSIResponseHfriAWC = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesHfriAWC, true);
        }

        ListResponseDto mergedPortfolioPerformanceSIResponse = mergePortfolioPerformanceResponses(performanceSIResponsePortfolioA, performanceSIResponsePortfolioB);
        ListResponseDto mergedBenchmarkPerformanceSIResponse = mergeBenchmarkPerformanceResponses(performanceSIResponseHfriFoF, performanceSIResponseHfriAWC);
        ListResponseDto mergedPerformanceSIResponse = mergePerformanceResponses(mergedPortfolioPerformanceSIResponse, mergedBenchmarkPerformanceSIResponse);
        if(!mergedPerformanceSIResponse.isStatusOK()){
            String performanceError = reportDto.getPerformanceError();
            reportDto.setPerformanceError((StringUtils.isNotEmpty(performanceError) ? performanceError : "") + mergedPerformanceSIResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundPerformanceRecordDto> performanceSI = mergedPerformanceSIResponse.getRecords();
        reportDto.setPerformanceSinceInception(performanceSI);

        // Market Sensitivity
        // MSCI
        ListResponseDto marketSensitivityMSCIResponse = getMarketSensitivity(BenchmarkLookup.MSCI_ACWI_IMI.getCode(), dateFromSI, dateTo, nicPortfolioResultDto);
        if(!marketSensitivityMSCIResponse.isStatusOK()){
            reportDto.setMarketSensitivityError(marketSensitivityMSCIResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesMSCIRecords = marketSensitivityMSCIResponse.getRecords();
        reportDto.setMarketSensitivitesMSCI(marketSensitivitesMSCIRecords);

        // Barclays Global Agg
        ListResponseDto marketSensitivityBarclaysGlobalResponse = getMarketSensitivity(BenchmarkLookup.GLOBAL_FI.getCode(), dateFromSI, dateTo, nicPortfolioResultDto);
        if(!marketSensitivityBarclaysGlobalResponse.isStatusOK()){
            reportDto.setMarketSensitivityError(marketSensitivityBarclaysGlobalResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesBarclaysRecords = marketSensitivityBarclaysGlobalResponse.getRecords();
        reportDto.setMarketSensitivitesBarclays(marketSensitivitesBarclaysRecords);

        // VARS
        ListResponseDto portfolioVarResponse = getHedgeFundPortfolioVars(searchParamsDto.getDate(), PortfolioVarLookup.VAR95.getCode(), PortfolioVarLookup.VAR99.getCode());
        if(!portfolioVarResponse.isStatusOK()){
            reportDto.setPortfolioVarsError(marketSensitivityBarclaysGlobalResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundPortfolioVarDto> portfolioVars = portfolioVarResponse.getRecords();
        reportDto.setPortfolioVars(portfolioVars);

        // Stress Tests
        List<RiskStressTestsDto> stressTests = riskStressTestsService.getAllStressTests();
        if(stressTests != null && !stressTests.isEmpty()){
            reportDto.setStressTests(stressTests);
        }else{
            reportDto.setStressTestsError("Missing stress tests data for selected period. ");
        }

        if(reportDto.getStatus() == null){
            reportDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return reportDto;
    }

    @Override
    public List<Date> getDateList() {
        Date mostRecentDate = this.nicPortfolioService.getMostRecentDate();
        if(mostRecentDate != null){
            List<Date> dates = new ArrayList<>();
            Date date = SINGULAR_PORTFOLIO_START_DATE;
            while(date.compareTo(mostRecentDate) <= 0){
                dates.add(DateUtils.getLastDayOfCurrentMonth(date));
                date = DateUtils.getLastDayOfNextMonth(date);
            }
            Collections.reverse(dates);
            return dates;
        }
        return null;
    }

    private ListResponseDto mergePortfolioPerformanceResponses(ListResponseDto portfolioAResponse, ListResponseDto portfolioBResponse) {
        if (portfolioAResponse == null) {
            return portfolioBResponse;
        } else if (portfolioBResponse == null) {
            return portfolioAResponse;
        } else {
            ListResponseDto mergedPortfolioResponse = new ListResponseDto();
            mergedPortfolioResponse.setStatus(ResponseStatusType.SUCCESS);
            //Error messages
            if (portfolioAResponse.getErrorMessageEn() != null) {
                mergedPortfolioResponse.appendErrorMessageEn(portfolioAResponse.getErrorMessageEn());
            }
            if (portfolioBResponse.getErrorMessageEn() != null) {
                mergedPortfolioResponse.appendErrorMessageEn(portfolioBResponse.getErrorMessageEn());
            }
            //Records
            if(portfolioAResponse.getRecords() != null && !portfolioAResponse.getRecords().isEmpty()) {
                mergedPortfolioResponse.setRecords(portfolioAResponse.getRecords());
            }
            if (portfolioBResponse.getRecords() != null && !portfolioBResponse.getRecords().isEmpty()) {
                if(mergedPortfolioResponse.getRecords() == null || mergedPortfolioResponse.getRecords().isEmpty()) {
                    mergedPortfolioResponse.setRecords(portfolioBResponse.getRecords());
                } else {
                    //Merge portfolio values
                    for (MonitoringRiskHedgeFundPerformanceRecordDto portfolioDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) mergedPortfolioResponse.getRecords()) {
                        for (MonitoringRiskHedgeFundPerformanceRecordDto portfolioBDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) portfolioBResponse.getRecords()) {
                            if (portfolioDto.getName().equalsIgnoreCase(portfolioBDto.getName())) {
                                portfolioDto.setPortfolioBValue(portfolioBDto.getPortfolioValue());
                                portfolioDto.setPortfolioBValueTxt(portfolioBDto.getPortfolioValueTxt());
                                break;
                            }
                        }
                    }
                }
            }
            return mergedPortfolioResponse;
        }
    }

    private ListResponseDto mergeBenchmarkPerformanceResponses(ListResponseDto hfriFofResponse, ListResponseDto hfriAwcResponse) {
        if (hfriFofResponse == null) {
            return hfriAwcResponse;
        } else if (hfriAwcResponse == null) {
            return hfriFofResponse;
        } else {
            ListResponseDto mergedBenchmarkResponse = new ListResponseDto();
            mergedBenchmarkResponse.setStatus(ResponseStatusType.SUCCESS);
            //Error messages
            if (hfriFofResponse.getErrorMessageEn() != null) {
                mergedBenchmarkResponse.setErrorMessageEn(hfriFofResponse.getErrorMessageEn());
            }
            if (hfriAwcResponse.getErrorMessageEn() != null) {
                mergedBenchmarkResponse.setErrorMessageEn(hfriAwcResponse.getErrorMessageEn());
            }
            //Records
            if (hfriFofResponse.getRecords() != null && !hfriFofResponse.getRecords().isEmpty()) {
                mergedBenchmarkResponse.setRecords(hfriFofResponse.getRecords());
            }
            if (hfriAwcResponse.getRecords() != null && !hfriAwcResponse.getRecords().isEmpty()) {
                if (mergedBenchmarkResponse.getRecords() == null && mergedBenchmarkResponse.getRecords().isEmpty()) {
                    mergedBenchmarkResponse.setRecords(hfriAwcResponse.getRecords());
                } else {
                    //Merge benchmark values
                    for (MonitoringRiskHedgeFundPerformanceRecordDto benchmarkDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) mergedBenchmarkResponse.getRecords()) {
                        for (MonitoringRiskHedgeFundPerformanceRecordDto hfriAwcDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) hfriAwcResponse.getRecords()) {
                            if (benchmarkDto.getName().equalsIgnoreCase(hfriAwcDto.getName())) {
                                benchmarkDto.setBenchmarkAwcValue(benchmarkDto.getBenchmarkValue());
                                benchmarkDto.setBenchmarkAwcValueTxt(benchmarkDto.getBenchmarkValueTxt());
                                break;
                            }
                        }
                    }
                }
            }
            return mergedBenchmarkResponse;
        }
    }

    private ListResponseDto mergePerformanceResponses(ListResponseDto portfolioResponse, ListResponseDto benchmarkResponse){
        if(portfolioResponse == null){
            return benchmarkResponse;
        }else if(benchmarkResponse == null){
            return portfolioResponse;
        }else{
            ListResponseDto mergedResponse = new ListResponseDto();
            mergedResponse.setStatus(ResponseStatusType.SUCCESS);
            // Error messages
            if(portfolioResponse.getErrorMessageEn() != null){
                mergedResponse.appendErrorMessageEn(portfolioResponse.getErrorMessageEn());
            }
            if(benchmarkResponse.getErrorMessageEn() != null){
                mergedResponse.appendErrorMessageEn(benchmarkResponse.getErrorMessageEn());
            }
            // Records
            if(portfolioResponse.getRecords() != null && !portfolioResponse.getRecords().isEmpty()) {
                mergedResponse.setRecords(portfolioResponse.getRecords());
            }
            if(benchmarkResponse.getRecords() != null && !benchmarkResponse.getRecords().isEmpty()){
                if(mergedResponse.getRecords() == null || mergedResponse.getRecords().isEmpty()){
                    mergedResponse.setRecords(benchmarkResponse.getRecords());
                }else{
                    // Add benchmark values to portfolio
                    for(MonitoringRiskHedgeFundPerformanceRecordDto portfolioDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) mergedResponse.getRecords()){
                        for(MonitoringRiskHedgeFundPerformanceRecordDto benchmarkDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) benchmarkResponse.getRecords()){
                            if(portfolioDto.getName().equalsIgnoreCase(benchmarkDto.getName())){
                                portfolioDto.setBenchmarkValue(benchmarkDto.getBenchmarkValue());
                                portfolioDto.setBenchmarkValueTxt(benchmarkDto.getBenchmarkValueTxt());
                                break;
                            }
                        }
                    }
                }
            }
            return mergedResponse;
        }
    }

    private ListResponseDto getHedgeFundPortfolioVars(Date date, String portfolioVarCode, String portfolioVarCode2){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundPortfolioVarDto> records = new ArrayList<>();

        // Actual
        MonitoringRiskHedgeFundPortfolioVarDto recordPortfolio = new MonitoringRiskHedgeFundPortfolioVarDto();
        PortfolioVarValueDto dto1 = hfPortfolioRiskService.getPortfolioVarEndOfMonthForDate(date, portfolioVarCode);
        try {
            recordPortfolio.setName(dto1.getPortfolioVar().getCode());
            recordPortfolio.setValue(dto1.getValue());
            records.add(recordPortfolio);
            responseDto.setRecords(records);
        } catch (NullPointerException ex) {
            String errorMessage = "No recorded value for " + portfolioVarCode + " for the date " + date;
            logger.error(errorMessage);
            responseDto.appendErrorMessageEn(errorMessage);
        }
        // Simulation
        MonitoringRiskHedgeFundPortfolioVarDto recordSimulation = new MonitoringRiskHedgeFundPortfolioVarDto();
        PortfolioVarValueDto dto2 = hfPortfolioRiskService.getPortfolioVarEndOfMonthForDate(date, portfolioVarCode2);
        try {
            recordSimulation.setName(dto2.getPortfolioVar().getCode());
            recordSimulation.setValue(dto2.getValue());
            records.add(recordSimulation);
            responseDto.setRecords(records);
        } catch (NullPointerException ex) {
            String errorMessage = "No recorded value for " + portfolioVarCode2 + " for the date " + date;
            logger.error(errorMessage);
            responseDto.appendErrorMessageEn(errorMessage);
        }
        if(responseDto.getStatus() == null){
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return responseDto;
    }
    /* MARKET SENSITIVITY *********************************************************************************************/
    private ListResponseDto getMarketSensitivity(String benchmarkCode, Date dateFrom, Date dateTo, NicPortfolioResultDto nicPortfolioResultDto){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> records = new ArrayList<>();
        String[] benchmarkCodes = {benchmarkCode};
        List<BenchmarkValueDto> benchmarks = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom, dateTo, 10, benchmarkCodes);
        if(benchmarks == null || benchmarks.isEmpty()){
            String errorMessage = "Failed to calculate market sensitivity: '" + benchmarkCode + "' benchmarks missing. ";
            logger.error(errorMessage);
            responseDto.appendErrorMessageEn(errorMessage);
        }else{
            int monthDiff = DateUtils.getMonthsChanged(dateFrom, dateTo);
            if(benchmarks.size() != monthDiff){
                String errorMessage = "Failed to calculate market sensitivity: '" + benchmarkCode + "' benchmarks for period [" +
                        DateUtils.getDateFormatted(dateFrom) + ", " + DateUtils.getDateFormatted(dateTo) + "] missing - " +
                        "expected " + monthDiff + ", found " + benchmarks.size() + ". ";
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }
            List<NicPortfolioDto> periodReturns = new ArrayList<>();
            if(nicPortfolioResultDto != null && nicPortfolioResultDto.getNicPortfolioDtoList() != null) {
                for (NicPortfolioDto dto : nicPortfolioResultDto.getNicPortfolioDtoList()) {
                    Date portfolioReturnDateEndMonth = DateUtils.getLastDayOfCurrentMonth(dto.getDate());
                    if (portfolioReturnDateEndMonth.compareTo(dateFrom) >= 0 && portfolioReturnDateEndMonth.compareTo(dateTo) <= 0) {
                        periodReturns.add(dto);
                    }
                }
            }
            if(periodReturns.size() != monthDiff){
                String errorMessage = "Failed to calculate market sensitivity: missing portfolio returns for period [" +
                        DateUtils.getDateFormatted(dateFrom) + ", " + DateUtils.getDateFormatted(dateTo) + "], expected " + monthDiff + ", found " + periodReturns.size() + ". ";
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }

            Double[] totalPortfolio = new Double[monthDiff];
            Map<Date, NicPortfolioDto> portfolioMap = new HashMap<>();
            int i = 0;
            for(NicPortfolioDto dto: periodReturns){
                if(dto.getDate() == null || dto.getHedgeFundsMtd() == null){
                    String errorMessage = "Failed to calculate market sensitivity: missing portfolio return for date '" + DateUtils.getDateFormatted(dto.getDate()) + "'. ";
                    logger.error(errorMessage);
                    responseDto.appendErrorMessageEn(errorMessage);
                    return responseDto;
                }
                portfolioMap.put(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto);
                totalPortfolio[i] = dto.getHedgeFundsMtd();
                i++;
            }
            Double[] totalBenchmarks = new Double[monthDiff];
            List<Double> positivePortfolio = new ArrayList<>();
            List<Double> negativePortfolio = new ArrayList<>();
            List<Double> positiveBenchmark = new ArrayList<>();
            List<Double> negativeBenchmark = new ArrayList<>();
            i = 0;
            Collections.reverse(benchmarks);
            for(BenchmarkValueDto benchmark: benchmarks){
                if(benchmark.getReturnValue() == null){
                    String errorMessage = "Failed to calculate market sensitivity: missing benchmark return for date '" + DateUtils.getDateFormatted(benchmark.getDate()) + "'. ";
                    logger.error(errorMessage);
                    responseDto.appendErrorMessageEn(errorMessage);
                    return responseDto;
                }
                Date benchmarkDate = DateUtils.getLastDayOfCurrentMonth(benchmark.getDate());
                if(benchmark.getReturnValue().doubleValue() >= 0){
                    positiveBenchmark.add(benchmark.getReturnValue());
                    if(portfolioMap.get(benchmarkDate) != null) {
                        positivePortfolio.add(portfolioMap.get(benchmarkDate).getHedgeFundsMtd());
                    }else{
                        String errorMessage = "Failed to calculate market sensitivity: missing portfolio return for date '" + DateUtils.getDateFormatted(benchmarkDate) + "'. ";
                        logger.error(errorMessage);
                        responseDto.appendErrorMessageEn(errorMessage);
                        return responseDto;
                    }
                }else{
                    negativeBenchmark.add(benchmark.getReturnValue());
                    if(portfolioMap.get(benchmarkDate) != null) {
                        negativePortfolio.add(portfolioMap.get(benchmarkDate).getHedgeFundsMtd());
                    }else{
                        String errorMessage = "Failed to calculate market sensitivity: missing portfolio return for date '" + DateUtils.getDateFormatted(benchmarkDate) + "'. ";
                        logger.error(errorMessage);
                        responseDto.appendErrorMessageEn(errorMessage);
                        return responseDto;
                    }
                }
                totalBenchmarks[i] = benchmark.getReturnValue();
                i++;
            }
            Double[] upPortfolio = new Double[positivePortfolio.size()];
            upPortfolio = positivePortfolio.toArray(upPortfolio);
            Double[] downPortfolio = new Double[negativePortfolio.size()];
            downPortfolio = negativePortfolio.toArray(downPortfolio);
            Double[] upBenchmark = new Double[positiveBenchmark.size()];
            upBenchmark = positiveBenchmark.toArray(upBenchmark);
            Double[] downBenchmark = new Double[negativeBenchmark.size()];
            downBenchmark = negativeBenchmark.toArray(downBenchmark);

            // UP
            Double upCumulativePortfolio = MathUtils.getCumulativeReturn(18, upPortfolio);
            Double upCumulativeBenchmark = MathUtils.getCumulativeReturn(18, upBenchmark);
            MonitoringRiskHedgeFundMarketSensitivityRecordDto upRecord = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
            upRecord.setName("Up " + upPortfolio.length+ " Months");
            upRecord.setPortfolioValue(upCumulativePortfolio);
            upRecord.setBenchmarkValue(upCumulativeBenchmark);
            records.add(upRecord);
            // DOWN
            Double downCumulativePortfolio = MathUtils.getCumulativeReturn(18, downPortfolio);
            Double downCumulativeBenchmark = MathUtils.getCumulativeReturn(18, downBenchmark);
            MonitoringRiskHedgeFundMarketSensitivityRecordDto downRecord = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
            downRecord.setName("Down " + downPortfolio.length+ " Months");
            downRecord.setPortfolioValue(downCumulativePortfolio);
            downRecord.setBenchmarkValue(downCumulativeBenchmark);
            records.add(downRecord);
            // TOTAL
            Double totalCumulativePortfolio = MathUtils.getCumulativeReturn(18, totalPortfolio);
            Double totalCumulativeBenchmark = MathUtils.getCumulativeReturn(18, totalBenchmarks);
            MonitoringRiskHedgeFundMarketSensitivityRecordDto totalRecord = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
            totalRecord.setName("All " + totalPortfolio.length+ " Months");
            totalRecord.setPortfolioValue(totalCumulativePortfolio);
            totalRecord.setBenchmarkValue(totalCumulativeBenchmark);
            records.add(totalRecord);

            responseDto.setRecords(records);
            if(responseDto.getStatus() == null){
                responseDto.setStatus(ResponseStatusType.SUCCESS);
            }
        }

        return responseDto;
    }
    /* ****************************************************************************************************************/

    private Double calculateMTD(int scale, Map<Date, SingularityITDHistoricalRecordDto> map, Date date){
        if(map != null && date != null) {
            Date prevMonth = DateUtils.getLastDayOfPreviousMonth(date);
            SingularityITDHistoricalRecordDto prevDto = map.get(prevMonth);
            SingularityITDHistoricalRecordDto currentDto = map.get(date);
            if (prevDto != null && currentDto != null) {
                if( prevDto.getEndingBalance() != null && currentDto.getEndingBalance() != null) {
                    Double prevNav = prevDto.getEndingBalance();
                    Double currentNav = MathUtils.subtract(scale, currentDto.getEndingBalance(), currentDto.getNetContribution());
                    Double mtd = MathUtils.subtract(scale, MathUtils.divide(scale, currentNav, prevNav), 1.0);
                    return mtd;
                }
            }
        }
        return null;
    }

    private Double calculateQTD(int scale, Map<Date, SingularityITDHistoricalRecordDto> map, Date date){
        if(map != null && date != null) {
            SingularityITDHistoricalRecordDto currentDto = map.get(date);
            if (currentDto != null) {
                int month = DateUtils.getMonth(date) + 1;
                int year = DateUtils.getYear(date);
                int quarter = (month % 3) == 0 ? (month / 3) : (month / 3 + 1);
                Date quarterDateMonthFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.getDate("01." + (quarter < 4 ? "0" : "") +
                        ((quarter - 1)* 3) + "." + year));
                Date currDate = quarterDateMonthFrom;
                Double qtd = 1.0;
                while(currDate.compareTo(date) <= 0){
                    SingularityITDHistoricalRecordDto dto = map.get(currDate);
                    if(dto != null){
                        Double mtd = calculateMTD(scale, map, currDate);
                        qtd = MathUtils.multiply(scale, qtd, MathUtils.add(scale, 1.0, mtd));
                        currDate = DateUtils.getLastDayOfNextMonth(currDate);
                    }
                }

                return MathUtils.subtract(scale, qtd, 1.0);
            }
        }
        return null;
    }
    private Double calculatePeriodToDate(int scale, Map<Date, SingularityITDHistoricalRecordDto> map, Date datefrom, Date dateTo){
        if(map != null && datefrom != null && dateTo != null) {
            Date currDate = datefrom;
            Double value = null;
            while(currDate.compareTo(dateTo) <= 0){
                SingularityITDHistoricalRecordDto currDto = map.get(currDate);
                Date prevMonth = DateUtils.getLastDayOfPreviousMonth(currDate);
                SingularityITDHistoricalRecordDto prevDto = map.get(prevMonth);
                if(currDto != null){
                    // TODO: when missing intermediary data
                    if(prevDto != null && prevDto.getEndingBalance() != null && prevDto.getEndingBalance().doubleValue() != 0.0) {
                        Double currentNav = MathUtils.subtract(scale, currDto.getEndingBalance(), currDto.getNetContribution());
                        Double mtd = MathUtils.subtract(scale, MathUtils.divide(scale, currentNav, prevDto.getEndingBalance()), 1.0);
                        value = value == null ? 1.0: value;
                        value = MathUtils.multiply(scale, value, MathUtils.add(scale, 1.0, mtd));
                    }else if(currDto.getNetContribution() != null){
                        Double mtd = MathUtils.subtract(scale, MathUtils.divide(scale, currDto.getEndingBalance(), currDto.getNetContribution()), 1.0);
                        value = value == null ? 1.0: value;
                        value = MathUtils.multiply(scale, value, MathUtils.add(scale, 1.0, mtd));
                    }
                }

                currDate = DateUtils.getLastDayOfNextMonth(currDate);
            }

            return MathUtils.subtract(scale, value, 1.0);
        }
        return null;
    }

    /* TOP HEDGE FUNDS ALLOCATIONS ************************************************************************************/
    private ListResponseDto getHedgeFundsTopAllocations(Date date){
        ListResponseDto responseDto = new ListResponseDto();
        SingularityITDHistoricalRecordHolderDto itdHRSRecordsHolder = this.hfitdService.getHistoricalData(date);
        Map<String, Map<Date, SingularityITDHistoricalRecordDto>> itdHRSMap = new HashMap<>();
        if(itdHRSRecordsHolder != null && !itdHRSRecordsHolder.isEmpty()){
            boolean sameDate = itdHRSRecordsHolder.getReportDate() != null && itdHRSRecordsHolder.getReportDate().compareTo(date) == 0;
            if(!sameDate){
                String warningMessage = "Funds NaV for selected period (" + DateUtils.getDateFormatted(date) +
                        ") calculated using final/updated data ('ITD Investment data' file as of " + DateUtils.getDateFormatted(itdHRSRecordsHolder.getReportDate()) + "). ";
                responseDto.appendWarningMessageEn(warningMessage);
                logger.info("Monitoring HF Risk Report (monthly): " + warningMessage);
            }
            for(SingularityITDHistoricalRecordDto dto: itdHRSRecordsHolder.getRecords()){
                if(itdHRSMap != null) {
                    if (itdHRSMap.get(dto.getFundName()) == null) {
                        itdHRSMap.put(dto.getFundName(), new HashMap<Date, SingularityITDHistoricalRecordDto>());
                    }
                    String fundName = dto.getFundName();
                    Map<Date, SingularityITDHistoricalRecordDto> itdHRSFundMap = itdHRSMap.get(fundName);
                    if (itdHRSFundMap.get(dto.getDate()) == null) {
                        itdHRSFundMap.put(dto.getDate(), dto);
                    }else{
                        SingularityITDHistoricalRecordDto existingDto = itdHRSFundMap.get(dto.getDate());

                        // TODO: check portfolio ?
                        existingDto.setPortfolio(existingDto.getPortfolio() + "," + dto.getPortfolio());

                        existingDto.setNetContribution(MathUtils.add(18, existingDto.getNetContribution(), dto.getNetContribution()));
                        existingDto.setEndingBalance(MathUtils.add(18, existingDto.getEndingBalance(), dto.getEndingBalance()));
                    }
                }
            }
        }
        if(itdHRSMap.isEmpty()){
            String errorMessage = "Missing Hedge Funds NAV data (ITD Investment Data file). ";
            logger.error(errorMessage);
            responseDto.appendErrorMessageEn(errorMessage);
            return responseDto;
        }
        List<MonitoringRiskHedgeFundFundAllocationDto> valueList = new ArrayList<>();
        Double totalNav = getHedgeFundTotalNav(date);
        if(totalNav == null){
            // missing data
            String errorMessage = "Missing data for total Singularity NAV calculation - 'General Ledger' file for selected period. ";
            logger.error(errorMessage);
            responseDto.appendErrorMessageEn(errorMessage);
        }
        int month = DateUtils.getMonth(date) + 1;
        int year = DateUtils.getYear(date);
        int quarter = (month % 3) == 0 ? (month / 3) : (month / 3 + 1);
        Date qtdDate = DateUtils.getLastDayOfCurrentMonth(DateUtils.getDate("01." + (quarter < 4 ? "0" : "") +
                ((quarter - 1)* 3 + 1) + "." + year));
        Date ytdDate = DateUtils.getDate("31.01." + year);
        if(!itdHRSMap.isEmpty()){
            for(String fundName: itdHRSMap.keySet()){
                Map<Date, SingularityITDHistoricalRecordDto> fundMap = itdHRSMap.get(fundName);
                MonitoringRiskHedgeFundFundAllocationDto value = new MonitoringRiskHedgeFundFundAllocationDto();
                value.setFundName(fundName);
                //value.setClassName();
                SingularityITDHistoricalRecordDto itdHRS = fundMap.get(date);
                if(itdHRS != null){
                    value.setClassName(itdHRS.getPortfolio().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_CAPITAL_CASE) ?
                            "A" : itdHRS.getPortfolio().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE) ?
                            "B": itdHRS.getPortfolio().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_CAPITAL_CASE + "," +
                            PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE) ? "A, B" : null);
                    value.setNav(itdHRS.getEndingBalance());
                    Double mtd = calculatePeriodToDate(18, fundMap, date, date);
                    value.setMtd(mtd);
                    Double qtd = calculatePeriodToDate(18, fundMap, qtdDate, date);
                    value.setQtd(qtd);
                    Double ytd = calculatePeriodToDate(18, fundMap, ytdDate, date);
                    value.setYtd(ytd);
                    valueList.add(value);
                    //totalNav = MathUtils.add(18, totalNav, value.getNav());
                }
            }
        }

        Collections.sort(valueList);
        Double top10NAV = 0.0;
        Double top10MTD = 0.0;
        Double top10QTD = 0.0;
        Double top10YTD = 0.0;
        Double top20NAV = 0.0;
        Double top20MTD = 0.0;
        Double top20QTD = 0.0;
        Double top20YTD = 0.0;
        Double totalMTD = 0.0;
        Double totalQTD = 0.0;
        Double totalYTD = 0.0;
        int i = 1;
        for(MonitoringRiskHedgeFundFundAllocationDto fund: valueList){
            fund.setNavPercent(MathUtils.divide(4, fund.getNav(), totalNav));
            if(i <= 10){
                top10NAV = MathUtils.add(18, top10NAV, fund.getNav());
                top20NAV = MathUtils.add(18, top20NAV, fund.getNav());
                top10MTD = MathUtils.add(18, top10MTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getMtd()));
                top20MTD = MathUtils.add(18, top20MTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getMtd()));
                top10QTD = MathUtils.add(18, top10QTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getQtd()));
                top20QTD = MathUtils.add(18, top20QTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getQtd()));
                top10YTD = MathUtils.add(18, top10YTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getYtd()));
                top20YTD = MathUtils.add(18, top20YTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getYtd()));
            }else if(i <= 20){
                top20NAV = MathUtils.add(18, top20NAV, fund.getNav());

                top20MTD = MathUtils.add(18, top20MTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getMtd()));
                top20QTD = MathUtils.add(18, top20QTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getQtd()));
                top20YTD = MathUtils.add(18, top20YTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getYtd()));
            }
            totalMTD = MathUtils.add(18, totalMTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getMtd()));
            totalQTD = MathUtils.add(18, totalQTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getQtd()));
            totalYTD = MathUtils.add(18, totalYTD, MathUtils.multiply(18, fund.getNavPercent(), fund.getYtd()));
            i++;
        }

        List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = new ArrayList<>();
        topFundAllocations.addAll(valueList.subList(0, 10));

        MonitoringRiskHedgeFundFundAllocationDto top10TotalRecord = new MonitoringRiskHedgeFundFundAllocationDto();
        top10TotalRecord.setFundName("TOP 10");
        top10TotalRecord.setNavPercent(MathUtils.divide(4, top10NAV, totalNav));
        top10TotalRecord.setNav(top10NAV);
        top10TotalRecord.setMtd(top10MTD != null ? MathUtils.add(4, 0.0, top10MTD) : null);
        top10TotalRecord.setQtd(top10QTD != null ? MathUtils.add(4, 0.0, top10QTD) : null);
        top10TotalRecord.setYtd(top10YTD != null ? MathUtils.add(4, 0.0, top10YTD) : null);
        topFundAllocations.add(top10TotalRecord);

        MonitoringRiskHedgeFundFundAllocationDto top20TotalRecord = new MonitoringRiskHedgeFundFundAllocationDto();
        top20TotalRecord.setFundName("TOP 20");
        top20TotalRecord.setNavPercent(MathUtils.divide(4, top20NAV, totalNav));
        top20TotalRecord.setNav(top20NAV);
        top20TotalRecord.setMtd(top20MTD != null ? MathUtils.add(4, 0.0, top20MTD) : null);
        top20TotalRecord.setQtd(top20QTD != null ? MathUtils.add(4, 0.0, top20QTD) : null);
        top20TotalRecord.setYtd(top20YTD != null ? MathUtils.add(4, 0.0, top20YTD) : null);
        topFundAllocations.add(top20TotalRecord);

        MonitoringRiskHedgeFundFundAllocationDto totalRecord = new MonitoringRiskHedgeFundFundAllocationDto();
        totalRecord.setFundName("TOTAL");
        totalRecord.setNavPercent(1.0);
        totalRecord.setNav(totalNav);
        totalRecord.setMtd(totalMTD != null ? totalMTD : null);
        totalRecord.setQtd(totalQTD != null ? MathUtils.add(4, 0.0, totalQTD) : null);
        totalRecord.setYtd(totalYTD != null ? MathUtils.add(4, 0.0, totalYTD) : null);
        topFundAllocations.add(totalRecord);

        responseDto.setRecords(topFundAllocations);
        if(responseDto.getStatus() == null) {
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return responseDto;

    }

    private Double getHedgeFundTotalNav(Date date){
        PeriodicReportDto report = this.periodicReportService.findReportByReportDate(date);
        if (report != null) {
            Double totalNAV = 0.0;
            ConsolidatedReportRecordHolderDto recordHolderDto = this.hfGeneralLedgerBalanceService.getWithoutExcludedRecords(report.getId());
            if (recordHolderDto != null && recordHolderDto.getGeneralLedgerBalanceList() != null && !recordHolderDto.getGeneralLedgerBalanceList().isEmpty()) {
                for (SingularityGeneralLedgerBalanceRecordDto recordGL : recordHolderDto.getGeneralLedgerBalanceList()) {
                    if(recordGL.getFinancialStatementCategory() != null &&
                            (recordGL.getFinancialStatementCategory().equalsIgnoreCase("E") ||
                                    recordGL.getFinancialStatementCategory().equalsIgnoreCase("I") ||
                                    recordGL.getFinancialStatementCategory().equalsIgnoreCase("X"))){
                        // add up
                        totalNAV = MathUtils.add(18, totalNAV, recordGL.getGLAccountBalance());
                    }
                }
                return MathUtils.multiply(18, -1.0, totalNAV);
            }
        }
        return null;
    }
    /* ****************************************************************************************************************/

    private ListResponseDto getAllocationBySubStrategy(Date date) {
        ListResponseDto responseDto = new ListResponseDto();
        try {
            List<MonitoringRiskHedgeFundAllocationSubStrategyDto> allocationSubStrategyDtos = this.converter.disassembleList(this.repository.findByFirstDate(date));
            responseDto.setRecords(allocationSubStrategyDtos);
        } catch (Exception ex) {
            logger.error("Error finding Sub-strategies");
        }
        return responseDto;
    }

    /* HEDGE FUNDS FACTOR BETAS ***************************************************************************************/
    private ListResponseDto getHedgeFundsFactorBetas(Date date, NicPortfolioResultDto nicPortfolioResultDto){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundBetaFactorDto> records = new ArrayList<>();

        // Dates
        Date dateTo = DateUtils.getLastDayOfCurrentMonth(date);
        Date dateFrom12M = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(date, -11));
        Date dateFromSinceInception = SINGULAR_PORTFOLIO_START_DATE;
        // Benchmarks map
        Map<String, List<BenchmarkValueDto>> benchmarksMap = getBenchmarksMap(dateFromSinceInception, dateFrom12M, dateTo);

        // HFRI 12M
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "HFRI FoF", BenchmarkLookup.HFRI.getCode(), benchmarksMap.get(BenchmarkLookup.HFRI.getCode()), benchmarksMap.get(BenchmarkLookup.HFRI.getCode() + "_SI"), nicPortfolioResultDto);
        // US High Yields 12M
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "US High Yield", BenchmarkLookup.US_HIGH_YIELDS.getCode(), benchmarksMap.get(BenchmarkLookup.US_HIGH_YIELDS.getCode()), benchmarksMap.get(BenchmarkLookup.US_HIGH_YIELDS.getCode() + "_SI"), nicPortfolioResultDto);

        // MSCI World
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "MSCI World", BenchmarkLookup.MSCI_WORLD.getCode(), benchmarksMap.get(BenchmarkLookup.MSCI_WORLD.getCode()), benchmarksMap.get(BenchmarkLookup.MSCI_WORLD.getCode() + "_SI"), nicPortfolioResultDto);

        // MSCI ACWI IMI
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "MSCI ACWIIMI", BenchmarkLookup.MSCI_ACWI_IMI.getCode(), benchmarksMap.get(BenchmarkLookup.MSCI_ACWI_IMI.getCode()), benchmarksMap.get(BenchmarkLookup.MSCI_ACWI_IMI.getCode() + "_SI"), nicPortfolioResultDto);

        // S&P 12M
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "S&P 500", BenchmarkLookup.SNP_500_SPX.getCode(), benchmarksMap.get(BenchmarkLookup.SNP_500_SPX.getCode()), benchmarksMap.get(BenchmarkLookup.SNP_500_SPX.getCode() + "_SI"), nicPortfolioResultDto);

        // US IG Credit
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "US IG Credit", BenchmarkLookup.US_IG_CREDIT.getCode(), benchmarksMap.get(BenchmarkLookup.US_IG_CREDIT.getCode()), benchmarksMap.get(BenchmarkLookup.US_IG_CREDIT.getCode() + "_SI"), nicPortfolioResultDto);

        // MSCI EM
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "MSCI EM", BenchmarkLookup.MSCI_EM.getCode(), benchmarksMap.get(BenchmarkLookup.MSCI_EM.getCode()), benchmarksMap.get(BenchmarkLookup.MSCI_EM.getCode() + "_SI"), nicPortfolioResultDto);

        // OIL
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "Oil", BenchmarkLookup.OIL.getCode(), benchmarksMap.get(BenchmarkLookup.OIL.getCode()), benchmarksMap.get(BenchmarkLookup.OIL.getCode() + "_SI"), nicPortfolioResultDto);

        // Dollar
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "Dollar", BenchmarkLookup.DOLLAR.getCode(), benchmarksMap.get(BenchmarkLookup.DOLLAR.getCode()), benchmarksMap.get(BenchmarkLookup.DOLLAR.getCode() + "_SI"), nicPortfolioResultDto);

        // Gold
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "Gold", BenchmarkLookup.GOLD.getCode(), benchmarksMap.get(BenchmarkLookup.GOLD.getCode()), benchmarksMap.get(BenchmarkLookup.GOLD.getCode() + "_SI"), nicPortfolioResultDto);

        // Global FI
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "Global FI", BenchmarkLookup.GLOBAL_FI.getCode(), benchmarksMap.get(BenchmarkLookup.GLOBAL_FI.getCode()), benchmarksMap.get(BenchmarkLookup.GLOBAL_FI.getCode() + "_SI"), nicPortfolioResultDto);

        responseDto.setRecords(records);
        if(responseDto.getStatus() == null){
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return  responseDto;
    }

    private Double getHedgeFundsFactorBeta(Date dateFrom, Date dateTo, String benchmarkName, List<BenchmarkValueDto> tbills,
                                           List<BenchmarkValueDto> benchmarks, NicPortfolioResultDto nicPortfolioResultDto){
        int monthDiff = DateUtils.getMonthsChanged(dateFrom, dateTo);
        if (tbills == null || tbills.size() != monthDiff) {
            String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing T-bills records for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + monthDiff + ", found " + tbills.size() + ". ";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        if (benchmarks == null || benchmarks.size() != monthDiff) {
            String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing " + benchmarkName + " records for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + monthDiff + ", found " + benchmarks.size() + ". ";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        if (nicPortfolioResultDto == null || nicPortfolioResultDto.getNicPortfolioDtoList() == null || nicPortfolioResultDto.getNicPortfolioDtoList().isEmpty()) {
            String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing Singularity returns for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]. ";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }else{
            List<NicPortfolioDto> records12M = new ArrayList<>();
            for(NicPortfolioDto nicPortfolioDto: nicPortfolioResultDto.getNicPortfolioDtoList()){
                Date nicPortfolioDateLastOfMonth = DateUtils.getLastDayOfCurrentMonth(nicPortfolioDto.getDate());
                if(nicPortfolioDto.getHedgeFundsMtd() != null &&
                        nicPortfolioDateLastOfMonth.compareTo(dateFrom) >= 0 && nicPortfolioDateLastOfMonth.compareTo(dateTo) <= 0){
                    records12M.add(nicPortfolioDto);
                }
            }
            if(records12M.size() != monthDiff){
                String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing Singularity returns for period [" + DateUtils.getDateFormatted(dateFrom) +
                        ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + monthDiff + ", found " + records12M.size() + ". ";
                logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            double[][] data = new double[monthDiff][];
            Collections.reverse(records12M);
            for(int i = 0; i < monthDiff; i++){
                data[i] = new double[2];
                data[i][0] = MathUtils.subtract(18, benchmarks.get(i).getReturnValue(), tbills.get(i).getReturnValue());
                data[i][1] = MathUtils.subtract(18, records12M.get(i).getHedgeFundsMtd(), tbills.get(i).getReturnValue());
            }
            double value = MathUtils.calculateSlope(data, false);
            return MathUtils.add(0.0, value);
        }
    }

    private void setFactorBeta(ListResponseDto responseDto, List<MonitoringRiskHedgeFundBetaFactorDto> records,
                               Date dateFromSinceInception, Date dateFrom12M, Date dateTo,
                               List<BenchmarkValueDto> tbills,  List<BenchmarkValueDto> tbillsSI,
                               String factorName, String factorCode, List<BenchmarkValueDto> factor,  List<BenchmarkValueDto> factorSI,
                               NicPortfolioResultDto nicPortfolioResultDto){
        MonitoringRiskHedgeFundBetaFactorDto factorHFRI = new MonitoringRiskHedgeFundBetaFactorDto(factorName, null, null);
        try{
            Double value12M = getHedgeFundsFactorBeta(dateFrom12M, dateTo, factorCode, tbills, factor, nicPortfolioResultDto);
            factorHFRI.setValue12M(value12M);

            Double valueSinceInception = getHedgeFundsFactorBeta(dateFromSinceInception, dateTo, factorCode, tbillsSI, factorSI, nicPortfolioResultDto);
            factorHFRI.setValueSinceInception(valueSinceInception);
        }catch (IllegalArgumentException ex){
            responseDto.appendErrorMessageEn(ex.getMessage());
        }
        records.add(factorHFRI);
    }

    private Map<String, List<BenchmarkValueDto>> getBenchmarksMap(Date dateFromSinceInception, Date dateFrom12M, Date dateTo){
        Map<String, List<BenchmarkValueDto>> map = new HashMap<>();
        for(BenchmarkLookup lookup: BenchmarkLookup.values()){
            String[] benchmarks = {lookup.getCode()};
            List<BenchmarkValueDto> benchmark12M = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom12M, dateTo, 10, benchmarks);
            List<BenchmarkValueDto> benchmarkSI = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSinceInception, dateTo, 10, benchmarks);
            map.put(lookup.getCode(), benchmark12M);
            map.put(lookup.getCode() + "_SI", benchmarkSI);
        }
        return map;
    }
    /* ****************************************************************************************************************/

    /* HEDGE FUNDS PERFORMANCE SUMMARY 12M ****************************************************************************/
    private ListResponseDto getHedgeFundsPerformanceSummary(Date dateFrom, Date dateTo, List<DateDoubleValue> returnValues, boolean isBenchmark){
        int period = DateUtils.getMonthsChanged(dateFrom, dateTo);
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundPerformanceRecordDto> performance = new ArrayList<>();
        if(returnValues != null && !returnValues.isEmpty()){
            List<DateDoubleValue> recordsForPeriod = new ArrayList<>();
            List<DateDoubleValue> recordsSinceInception = new ArrayList<>();
            for(DateDoubleValue value: returnValues){
                if(value.getValue() != null){
                    if(value.getDate().compareTo(dateFrom) >= 0 && value.getDate().compareTo(dateTo) <= 0){
                        recordsForPeriod.add(value);
                    }
                    recordsSinceInception.add(value);
                }
            }

            if(recordsForPeriod.size() != period){
                String errorMessage = "Missing " + (isBenchmark ? "HFRI returns " : "Hedge Funds MTD records") + " for period [" + DateUtils.getDateFormatted(dateFrom) +
                ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found " + recordsForPeriod.size() + ". ";
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }

            Collections.sort(recordsForPeriod);
            double[] returns = new double[period];
            for(int i = 0; i < recordsForPeriod.size(); i++){
                returns[i] = recordsForPeriod.get(i).getValue();
            }
            double[] returnsSI = new double[recordsSinceInception.size()];
            for(int i = 0; i < recordsSinceInception.size(); i++){
                returnsSI[i] = recordsSinceInception.get(i).getValue();
            }
            String[] benchmarkCodes = {BenchmarkLookup.T_BILLS.getCode()};
            List<BenchmarkValueDto> tbills = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom, dateTo, 10, benchmarkCodes);
            if(tbills == null || tbills.size() != period){
                String errorMessage = "Missing T-bills records for period [" + DateUtils.getDateFormatted(dateFrom) +
                        ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found " + tbills.size() + ". ";
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }
            double[] tbillsReturns = new double[tbills.size()];
            for(int i = tbillsReturns.length - 1; i >= 0; i--){
                if(tbills.get(i).getReturnValue() == null){
                    String errorMessage = "Missing T-bills return values for date " +
                            DateUtils.getDateFormatted(tbills.get(i).getDate());
                    logger.error(errorMessage);
                    return null;
                }
                tbillsReturns[i] = tbills.get(i).getReturnValue();
            }
            // AnRoR
            Double annRoR = MathUtils.getAnnualizedReturn(returns, 18);
            Double annRoR1 = isBenchmark ? null : annRoR;
            Double annRoR2 = !isBenchmark ? null : annRoR;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Annualized Return", annRoR1, annRoR2,
                    annRoR1 != null ? (MathUtils.multiply(4, annRoR1, 100.0) + "%") : null,
                    annRoR2 != null ? (MathUtils.multiply(4, annRoR2, 100.0) + "%") : null));

            // STD
            Double std = MathUtils.getStandardDeviation(returns, true);
            if(std != null) {
                Double annStd = MathUtils.multiply(18, std, Math.sqrt(12));
                Double annStd1 = isBenchmark ? null : annStd;
                Double annStd2 = !isBenchmark ? null : annStd;
                performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Ann Standard Deviation", annStd1, annStd2,
                        annStd1 != null ? (MathUtils.multiply(4, annStd1, 100.0) + "%") : null,
                        annStd2 != null ? (MathUtils.multiply(4, annStd2, 100.0) + "%") : null));
            }

            // Downside Deviation
            Double downsideDeviation = MathUtils.getAnnualizedDownsideDeviation(18, returns);
            Double downsideDeviation1 = isBenchmark ? null : downsideDeviation;
            Double downsideDeviation2 = !isBenchmark ? null : downsideDeviation;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Downside Deviation", downsideDeviation1, downsideDeviation2,
                    downsideDeviation1 != null ? (MathUtils.multiply(4, downsideDeviation1, 100.0) + "%") : null,
                    downsideDeviation2 != null ? (MathUtils.multiply(4, downsideDeviation2, 100.0) + "%") : null));

            // Sharpe Ratio
            Double sharpeRatio = MathUtils.getSharpeRatioAvg12MReturns(18, returns, tbillsReturns, true);
            Double sharpeRatio1 = isBenchmark ? null : sharpeRatio;
            Double sharpeRatio2 = !isBenchmark ? null : sharpeRatio;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Sharpe Ratio", sharpeRatio1, sharpeRatio2,
                    sharpeRatio1 != null ? MathUtils.add(sharpeRatio1, 0.0).toString() : null,
                    sharpeRatio2 != null ? MathUtils.add(sharpeRatio2, 0.0).toString() : null));

            // Sortino
            Double annRoRTbills = MathUtils.getAnnualizedReturn(tbillsReturns, 18);
            Double sortino = MathUtils.getSortinoRatioAvgReturns(annRoRTbills, returns, 18);
            Double sortino1 = isBenchmark ? null : sortino;
            Double sortino2 = !isBenchmark ? null : sortino;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Sortino Ratio", sortino1, sortino2,
                    sortino1 != null ? MathUtils.add(sortino1, 0.0).toString() : null,
                    sortino2 != null ? MathUtils.add(sortino2, 0.0).toString() : null));

            // Worst Drawdown
            double[] cumulativeReturnsSI = MathUtils.getCumulativeReturnsFromInitial(18, returnsSI, 1.0);
            double[] cumulativeReturnsPeriod = new double[returns.length];
            int j = 0;
            for(int i = 0; i < recordsSinceInception.size(); i++){
                if(recordsSinceInception.get(i).getDate().compareTo(dateFrom) >= 0 && recordsSinceInception.get(i).getDate().compareTo(dateTo) <= 0){
                    if(j < cumulativeReturnsPeriod.length) {
                        cumulativeReturnsPeriod[j] = cumulativeReturnsSI[i];
                        j++;
                    }else{
                        // TODO: error?
                    }
                }
            }
            if(j != period){
                String errorMessage = "Missing calculated value added monthly return for period [" + DateUtils.getDateFormatted(dateFrom) +
                        ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found " + tbills.size() + ". ";
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }
            int monthsCountSinceInception = DateUtils.getMonthsChanged(dateFrom, dateTo);
            if(cumulativeReturnsPeriod != null && cumulativeReturnsSI != null && cumulativeReturnsPeriod.length == monthsCountSinceInception){
                double[] arrPrepended = new double[cumulativeReturnsPeriod.length + 1];
                for(int i = 0; i < arrPrepended.length; i++){
                    if(i == 0) {
                        arrPrepended[i] = 1.0;
                    }else {
                        arrPrepended[i] = cumulativeReturnsPeriod[i - 1];
                    }
                }
                cumulativeReturnsPeriod = arrPrepended;
            }
            WorstDrawdownDto worstDDDto = MathUtils.getWorstDrawdown(18, cumulativeReturnsPeriod);
            WorstDrawdownDto worstDDDto1 = isBenchmark ? null : worstDDDto;
            WorstDrawdownDto worstDDDto2 = !isBenchmark ? null : worstDDDto;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Worst DD",
                    worstDDDto1 != null && worstDDDto1.getWorstDDValue() != null ? worstDDDto1.getWorstDDValue() : null,
                    worstDDDto2 != null && worstDDDto2.getWorstDDValue() != null ? worstDDDto2.getWorstDDValue() : null,
                    worstDDDto1 != null && worstDDDto1.getWorstDDValue() != null ? (MathUtils.multiply(4, worstDDDto1.getWorstDDValue(), 100.0) + "%") : null,
                    worstDDDto2 != null && worstDDDto2.getWorstDDValue() != null ? (MathUtils.multiply(4, worstDDDto2.getWorstDDValue(), 100.0) + "%") : null));

            // Worst DD Period
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Worst DD Duration (months)",
                    worstDDDto1 != null && worstDDDto1.getWorstDDPeriod() != null ? MathUtils.add(worstDDDto1.getWorstDDPeriod(), 0.0) : null,
                    worstDDDto2 != null && worstDDDto2.getWorstDDPeriod() != null ? MathUtils.add(worstDDDto2.getWorstDDPeriod(), 0.0) : null,
                    worstDDDto1 != null && worstDDDto1.getWorstDDPeriod() != null ? worstDDDto1.getWorstDDPeriod().toString() : null,
                    worstDDDto2 != null && worstDDDto2.getWorstDDPeriod() != null ? worstDDDto2.getWorstDDPeriod().toString() : null));

            // Recovery Months
            Integer recoveryMonths = MathUtils.getRecoveryMonths(18, returns);
            Integer recoveryMonths1 = isBenchmark ? null : recoveryMonths;
            Integer recoveryMonths2 = !isBenchmark ? null : recoveryMonths;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Recovery (months)",
                    recoveryMonths1 != null ? MathUtils.add(recoveryMonths1, 0.0) : null,
                    recoveryMonths2 != null ? MathUtils.add(recoveryMonths2, 0.0) : null,
                    recoveryMonths1 != null ? recoveryMonths1.toString() : null,
                    recoveryMonths2 != null ? recoveryMonths2.toString() : null));

            // Positive & Negative Months
            int positives = 0;
            int negatives = 0;
            Double negativeSum = 0.0;
            Double positiveSum = 0.0;
            for(int i = 0; i < returns.length; i++){
                if(returns[i] >= 0){
                    positives += 1;
                    positiveSum = MathUtils.add(18, positiveSum, returns[i]);
                }else{
                    negatives += 1;
                    negativeSum = MathUtils.add(18, negativeSum, returns[i]);
                }
            }
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Positive Months",
                    !isBenchmark ? MathUtils.divide(positives + 0.0, returns.length + 0.0) : null,
                    isBenchmark ? MathUtils.divide(positives + 0.0, returns.length + 0.0) : null,
                    !isBenchmark ? MathUtils.multiply(2, 100.0, MathUtils.divide(positives + 0.0, returns.length + 0.0)) + "%" : null,
                    isBenchmark ? MathUtils.multiply(2, 100.0, MathUtils.divide(positives + 0.0, returns.length + 0.0)) + "%" : null));

            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Negative Months",
                    !isBenchmark ? MathUtils.divide(negatives + 0.0, returns.length + 0.0) : null,
                    isBenchmark ? MathUtils.divide(negatives + 0.0, returns.length + 0.0) : null,
                    !isBenchmark ? MathUtils.multiply(2, 100.0, MathUtils.divide(negatives + 0.0, returns.length + 0.0)) + "%" : null,
                    isBenchmark ? MathUtils.multiply(2, 100.0, MathUtils.divide(negatives + 0.0, returns.length + 0.0)) + "%" : null));

            // Gain Loss ratio
            Double gainLossRatio = null;
            Double gainLossRatio1 = null;
            Double gainLossRatio2 = null;
            if(negatives > 0 && negativeSum != 0.0) {
                Double positiveAvg = MathUtils.divide(18, positiveSum, positives + 0.0);
                Double negativeAvg = MathUtils.divide(18, negativeSum, negatives + 0.0);

                gainLossRatio = MathUtils.abs(MathUtils.divide(18, positiveAvg, negativeAvg));
                gainLossRatio1 = !isBenchmark ? gainLossRatio : null;
                gainLossRatio2 = isBenchmark ? gainLossRatio : null;

            }
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Gain/Loss Ratio",gainLossRatio1, gainLossRatio2,
                    !isBenchmark ? MathUtils.add(0.0, gainLossRatio).toString() : null,
                    isBenchmark ? MathUtils.add(0.0, gainLossRatio).toString() : null));
        }
        responseDto.setRecords(performance);
        if(responseDto.getStatus() == null){
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return responseDto;
    }
    /* ****************************************************************************************************************/

//    private EntitySaveResponseDto saveHFPortfolioVar(MonitoringRiskHedgeFundPortfolioVarDto dto) {
//        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
//
//    }
//
//    private EntitySaveResponseDto saveStressTest(MonitoringRiskHedgeFundStressTestDto dto) {
//
//    }

    @Override
    public MonitoringRiskHedgeFundAllocationSubStrategyResultDto uploadStrategy(Set<FilesDto> filesDtoSet, String updater) {

        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to update Sub-strategy data: the user is not found in the database!");
                return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: the user is not found in the database!", "");
            }

            FilesDto filesDto;
            Iterator<Row> rowIterator;
            Row previousRow = null;
            Row currentRow;
            int previousMonth;
            int currentMonth;
            int rowNumber = 0;
            List<AllocationBySubStrategy> allocationBySubStrategyList = new ArrayList<>();

            try {
                filesDto = filesDtoSet.iterator().next();
                InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
                XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
                XSSFSheet sheet = workbook.getSheet("Database");
                rowIterator = sheet.iterator();
            } catch (Exception ex) {
                logger.error("Failed to update Sub-strategy data: the file or the sheet 'Database' cannot be opened, ", ex);
                return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: the file or the sheet 'Database' cannot be opened!", "");
            }

            for (int i = 0; i < 1; i++) {
                if(rowIterator.hasNext()) {
                    previousRow = rowIterator.next();
                    rowNumber++;
                } else {
                    logger.error("Failed to update Sub-strategy data: the sheet 'Database' contains less than 10 rows!");
                    return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: the sheet 'Database' contains less than 15 rows!", "");
                }
            }

            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                rowNumber++;

                if (previousRow.getCell(0) == null || currentRow.getCell(0) == null) {
                    //end of data
                    break;
                }

                try {
                    previousMonth = DateUtils.getMonth(previousRow.getCell(1).getDateCellValue());
                    currentMonth = DateUtils.getMonth(previousRow.getCell(0).getDateCellValue());

                    if (previousMonth != currentMonth) {
                        allocationBySubStrategyList.add(this.create(previousRow, employee));
                    }
                } catch (Exception ex) {
                    logger.error("Failed to update Sub-strategy data: error parsing row #" + rowNumber + ", ", ex);
                    return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: error parsing row #" + rowNumber + "!", "");
                }

                previousRow = currentRow;
            }

            try {
                if(allocationBySubStrategyList.size() > 0) {
                    filesDto.setType(FileTypeLookup.MONITORING_RISK_ALLOCATION_BY_SUB_STRATEGY.getCode());
                    Long fileId = this.fileService.save(filesDto, FileTypeLookup.MONITORING_RISK_ALLOCATION_BY_SUB_STRATEGY.getCatalog());

                    Files file = new Files();
                    file.setId(fileId);
                    for (AllocationBySubStrategy allocationBySubStrategy : allocationBySubStrategyList) {
                        allocationBySubStrategy.setFile(file);
                    }

                    this.repository.save(allocationBySubStrategyList);

//                    List<Long> portfoliosToDelete = new ArrayList<>();
//                    List<Long> filesToDelete = new ArrayList<>();
//
//                    for (AllocationBySubStrategy allocationBySubStrategy : this.repository.findAll()) {
//                        if (allocationBySubStrategy.getFile() != null && ! allocationBySubStrategy.getFile().getId().equals(fileId)) {
//                            portfoliosToDelete.add(allocationBySubStrategy.getId());
//                            if ( ! filesToDelete.contains(allocationBySubStrategy.getFile().getId())) {
//                                filesToDelete.add(allocationBySubStrategy.getFile().getId());
//                            }
//                        }
//                    }
//
//                    for (Long id : portfoliosToDelete) {
//                        this.repository.delete(id);
//                    }
//
//                    for (Long id : filesToDelete) {
//                        this.fileService.delete(id);
//                    }
                }
            } catch (Exception ex) {
                logger.error("Failed to update Sub-strategy data: repository problem, ", ex);
                return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: repository problem!", "");
            }

            logger.info("Sub-strategy data has been updated successfully, updater: " + updater);
            List<MonitoringRiskHedgeFundAllocationSubStrategyDto> allocationSubStrategyDtos = this.converter.disassembleList(this.repository.findAllByOrderByFirstDateAsc());
            return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(allocationSubStrategyDtos, ResponseStatusType.SUCCESS, "", "Sub-strategy data has been updated successfully!", "");

        } catch (Exception ex) {
            logger.error("Failed to update Sub-strategy data, ", ex);
            return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data!", "");
        }
    }

    private AllocationBySubStrategy create(Row row, Employee updater) {
        try {
            return new AllocationBySubStrategy(
                    updater,
                    new Files(),
                    ExcelUtils.getStringValueFromCell(row.getCell(2)),
                    row.getCell(0).getDateCellValue(),
                    row.getCell(1).getDateCellValue(),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(3)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(4))
            );
        } catch (Exception ex) {
            logger.error("Failed to update Allocation by Sub-strategy data: row parsing error, ", ex);
            throw ex;
        }
    }

    private MonitoringRiskHedgeFundReportDto getDummyMonthlyHedgeFundReport(){
        MonitoringRiskHedgeFundReportDto report = new MonitoringRiskHedgeFundReportDto();
        report.setDate(new Date());

        List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            MonitoringRiskHedgeFundFundAllocationDto fund1 = new MonitoringRiskHedgeFundFundAllocationDto();
            fund1.setFundName("MW Eureka Fund");
            fund1.setClassName("B");
            fund1.setNav(0.1133);
            fund1.setMtd(0.0154);
            fund1.setQtd(0.0123);
            fund1.setYtd(0.0456);
            fund1.setContributionToYTD(0.0111);
            fund1.setContributionToVAR(0.0222);
            topFundAllocations.add(fund1);
        }
        report.setTopFundAllocations(topFundAllocations);

        List<MonitoringRiskHedgeFundBetaFactorDto> factorBetas = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            MonitoringRiskHedgeFundBetaFactorDto factor = new MonitoringRiskHedgeFundBetaFactorDto();
            factor.setName("HFRI FoF");
            factor.setValueSinceInception(0.86);
            factor.setValue12M(0.80);
            factorBetas.add(factor);
        }
        report.setFactorBetas(factorBetas);

        List<MonitoringRiskHedgeFundPortfolioVarDto> vars = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            MonitoringRiskHedgeFundPortfolioVarDto var = new MonitoringRiskHedgeFundPortfolioVarDto();
            var.setName("1M VaR 95%");
            var.setValue(6.09);
            vars.add(var);
        }
        report.setPortfolioVars(vars);

        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> valuesMSCI = new ArrayList<>();
        MonitoringRiskHedgeFundMarketSensitivityRecordDto value1 = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
        value1.setName("Up 37 Months");
        value1.setPortfolioValue(0.2642);
        value1.setBenchmarkValue(1.3842);
        valuesMSCI.add(value1);

        MonitoringRiskHedgeFundMarketSensitivityRecordDto value2 = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
        value2.setName("Down 15 Months");
        value2.setPortfolioValue(-0.1312);
        value2.setBenchmarkValue(-0.4171);
        valuesMSCI.add(value2);

        MonitoringRiskHedgeFundMarketSensitivityRecordDto value3 = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
        value3.setName("All 52 Months");
        value3.setPortfolioValue(0.0983);
        value3.setBenchmarkValue(0.3897);
        valuesMSCI.add(value3);

        report.setMarketSensitivitesMSCI(valuesMSCI);

        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> valuesBarclays = new ArrayList<>();
        MonitoringRiskHedgeFundMarketSensitivityRecordDto v1 = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
        v1.setName("Up 34 Months");
        v1.setPortfolioValue(0.0743);
        v1.setBenchmarkValue(0.2735);
        valuesBarclays.add(v1);

        MonitoringRiskHedgeFundMarketSensitivityRecordDto v2 = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
        v2.setName("Down 18 Months");
        v2.setPortfolioValue(-0.1312);
        v2.setBenchmarkValue(-0.4171);
        valuesBarclays.add(v2);

        MonitoringRiskHedgeFundMarketSensitivityRecordDto v3 = new MonitoringRiskHedgeFundMarketSensitivityRecordDto();
        v3.setName("All 52 Months");
        v3.setPortfolioValue(0.0983);
        v3.setBenchmarkValue(0.3897);
        valuesBarclays.add(v3);

        report.setMarketSensitivitesBarclays(valuesBarclays);

        List<MonitoringRiskHedgeFundPerformanceRecordDto> performance12M = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            MonitoringRiskHedgeFundPerformanceRecordDto dto = new MonitoringRiskHedgeFundPerformanceRecordDto();
            dto.setName("Annualized Return");
            dto.setPortfolioValue(0.0619);
            dto.setBenchmarkValue(0.0447);
            performance12M.add(dto);
        }
        report.setPerformance12M(performance12M);


        List<MonitoringRiskHedgeFundPerformanceRecordDto> performanceSinceInception = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            MonitoringRiskHedgeFundPerformanceRecordDto dto = new MonitoringRiskHedgeFundPerformanceRecordDto();
            dto.setName("Ann. Standard Deviation");
            dto.setPortfolioValue(0.0355);
            dto.setBenchmarkValue(0.0381);
            performanceSinceInception.add(dto);
        }
        report.setPerformanceSinceInception(performanceSinceInception);

        List<RiskStressTestsDto> stressTests = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            RiskStressTestsDto dto = new RiskStressTestsDto();
            dto.setName("Russian Debt/LTCM Crisis 1998");
            dto.setValue(-0.0246);
            stressTests.add(dto);
        }
        report.setStressTests(stressTests);

        return report;
    }

    private class DateDoubleValue implements Comparable{
        private Date date;
        private Double value;

        public DateDoubleValue(){}

        public DateDoubleValue(Date date, Double value){
            this.date = date;
            this.value = value;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        @Override
        public int compareTo(Object o) {
            return this.date.compareTo(((DateDoubleValue) o).date);
        }
    }
}
