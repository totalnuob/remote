package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.WorstDrawdownDto;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.monitoring.MonitoringRiskService;
import kz.nicnbk.service.api.monitoring.NicPortfolioService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.risk.RiskStressTestsService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.monitoring.*;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.SingularityGeneralLedgerBalanceRecordDto;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MonitoringRiskServiceImpl implements MonitoringRiskService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringRiskServiceImpl.class);

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

    @Override
    public MonitoringRiskHedgeFundReportDto getMonthlyHedgeFundReport(MonitoringRiskReportSearchParamsDto searchParamsDto) {

        MonitoringRiskHedgeFundReportDto reportDto = new MonitoringRiskHedgeFundReportDto();//getDummyMonthlyHedgeFundReport();

        NicPortfolioResultDto nicPortfolioResultDto = this.nicPortfolioService.get();

        // Top fund allocations
        ListResponseDto topFundAllocationsResponse = getHedgeFundsTopAllocations(searchParamsDto.getDate());

        // TODO: warning
        reportDto.setTopFundAllocationsWarning("Calculations done on Preliminary data");

        if(!topFundAllocationsResponse.isStatusOK()){
            reportDto.setTopFundAllocationsError(topFundAllocationsResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = topFundAllocationsResponse.getRecords();
        reportDto.setTopFundAllocations(topFundAllocations);

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
        ListResponseDto performance12MResponsePortfolio = null;
        ListResponseDto performance12MResponseBenchmark = null;
        if(nicPortfolioResultDto != null && nicPortfolioResultDto.getNicPortfolioDtoList() != null){
            List<DateDoubleValue> values = new ArrayList<>();
            for(NicPortfolioDto dto: nicPortfolioResultDto.getNicPortfolioDtoList()){
                DateDoubleValue value = new DateDoubleValue(dto.getDate(), dto.getHedgeFundsMtd());
                values.add(value);
            }

            performance12MResponsePortfolio = getHedgeFundsPerformanceSummary12M(dateFrom12M, dateTo, values, false);
        }

        List<BenchmarkValueDto> hfri12M = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom12M, dateTo, BenchmarkLookup.HFRI.getCode());
        if(hfri12M != null && !hfri12M.isEmpty()){
            List<DateDoubleValue> values = new ArrayList<>();
            for(BenchmarkValueDto dto: hfri12M){
                DateDoubleValue value = new DateDoubleValue(dto.getDate(), dto.getReturnValue());
                values.add(value);
            }

            performance12MResponseBenchmark = getHedgeFundsPerformanceSummary12M(dateFrom12M, dateTo, values, true);
        }

        ListResponseDto mergedPerformance12MResponse = mergePerformanceResponses(performance12MResponsePortfolio, performance12MResponseBenchmark);
        if(!mergedPerformance12MResponse.isStatusOK()){
            reportDto.setPerformanceError(mergedPerformance12MResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundPerformanceRecordDto> performance12M = mergedPerformance12MResponse.getRecords();
        reportDto.setPerformance12M(performance12M);


        // Performance since inception
        Date dateFromSI = DateUtils.getLastDayOfCurrentMonth(DateUtils.getDate("31.08.2015"));
        ListResponseDto performanceSIResponsePortfolio = null;
        ListResponseDto performanceSIResponseBenchmark = null;
        if(nicPortfolioResultDto != null && nicPortfolioResultDto.getNicPortfolioDtoList() != null){
            List<DateDoubleValue> values = new ArrayList<>();
            for(NicPortfolioDto dto: nicPortfolioResultDto.getNicPortfolioDtoList()){
                DateDoubleValue value = new DateDoubleValue(dto.getDate(), dto.getHedgeFundsMtd());
                values.add(value);
            }

            performanceSIResponsePortfolio = getHedgeFundsPerformanceSummary12M(dateFromSI, dateTo, values, false);
        }

        List<BenchmarkValueDto> hfriSI = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFromSI, dateTo, BenchmarkLookup.HFRI.getCode());
        if(hfriSI != null && !hfriSI.isEmpty()){
            List<DateDoubleValue> values = new ArrayList<>();
            for(BenchmarkValueDto dto: hfriSI){
                DateDoubleValue value = new DateDoubleValue(dto.getDate(), dto.getReturnValue());
                values.add(value);
            }

            performanceSIResponseBenchmark = getHedgeFundsPerformanceSummary12M(dateFromSI, dateTo, values, true);
        }

        ListResponseDto mergedPerformanceSIResponse = mergePerformanceResponses(performanceSIResponsePortfolio, performanceSIResponseBenchmark);
        if(!mergedPerformanceSIResponse.isStatusOK()){
            reportDto.setPerformanceError(mergedPerformanceSIResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundPerformanceRecordDto> performanceSI = mergedPerformanceSIResponse.getRecords();
        reportDto.setPerformanceSinceInception(performanceSI);

        // Market Sensitivity
        Date dateFromSinceInception = DateUtils.getDate("31.08.2015");
        // MSCI
        ListResponseDto marketSensitivityMSCIResponse = getMarketSensitivity(BenchmarkLookup.MSCI_ACWI_IMI.getCode(), dateFromSinceInception, dateTo, nicPortfolioResultDto);
        if(!marketSensitivityMSCIResponse.isStatusOK()){
            reportDto.setMarketSensitivityError(marketSensitivityMSCIResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesMSCIRecords = marketSensitivityMSCIResponse.getRecords();
        reportDto.setMarketSensitivitesMSCI(marketSensitivitesMSCIRecords);

        // Barclays Global Agg
        ListResponseDto marketSensitivityBarclaysGlobalResponse = getMarketSensitivity(BenchmarkLookup.GLOBAL_FI.getCode(), dateFromSinceInception, dateTo, nicPortfolioResultDto);
        if(!marketSensitivityBarclaysGlobalResponse.isStatusOK()){
            reportDto.setMarketSensitivityError(marketSensitivityBarclaysGlobalResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesBarclaysRecords = marketSensitivityBarclaysGlobalResponse.getRecords();
        reportDto.setMarketSensitivitesBarclays(marketSensitivitesBarclaysRecords);

        // VARS
        ListResponseDto portfolioVarResponse = getHedgeFundPortfolioVars(searchParamsDto.getDate());
        if(!portfolioVarResponse.isStatusOK()){
            reportDto.setPortfolioVarsError(marketSensitivityBarclaysGlobalResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundPortfolioVarDto> portfolioVars = portfolioVarResponse.getRecords();
        reportDto.setPortfolioVars(portfolioVars);

        // Stress Tests
        List<RiskStressTestsDto> stressTests = riskStressTestsService.getStressTestsByDate(searchParamsDto.getDate());
        if(stressTests != null && !stressTests.isEmpty()){
            reportDto.setStressTests(stressTests);
        }

        if(reportDto.getStatus() == null){
            reportDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return reportDto;
    }

    private ListResponseDto mergePerformanceResponses(ListResponseDto portfolioResponse, ListResponseDto benchmarkResponse){
        if(portfolioResponse == null){
            return benchmarkResponse;
        }else if(benchmarkResponse == null){
            return portfolioResponse;
        }else{
            ListResponseDto mergedResponse = new ListResponseDto();
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

    private ListResponseDto getHedgeFundPortfolioVars(Date date){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundPortfolioVarDto> records = new ArrayList<>();

        // Actual
        MonitoringRiskHedgeFundPortfolioVarDto recordPortfolio = new MonitoringRiskHedgeFundPortfolioVarDto("1M VaR 95%", null);
        records.add(recordPortfolio);
        // Simulation
        MonitoringRiskHedgeFundPortfolioVarDto recordSimulation = new MonitoringRiskHedgeFundPortfolioVarDto("1M simulation VaR 95%", null);
        records.add(recordSimulation);

        responseDto.setRecords(records);
        if(responseDto.getStatus() == null){
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return responseDto;
    }
    /* MARKET SENSITIVITY *********************************************************************************************/
    private ListResponseDto getMarketSensitivity(String benchmarkCode, Date dateFrom, Date dateTo, NicPortfolioResultDto nicPortfolioResultDto){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> records = new ArrayList<>();
        List<BenchmarkValueDto> benchmarks = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, benchmarkCode);
        if(benchmarks == null || benchmarks.isEmpty()){
            String errorMessage = "Failed to calculate market sensitivity: '" + benchmarkCode + "' benchmarks missing. ";
            logger.error(errorMessage);
            responseDto.appendErrorMessageEn(errorMessage);
        }else{
            int monthDiff = DateUtils.getMonthsChanged(dateFrom, dateTo);
            if(benchmarks.size() != monthDiff){
                String errorMessage = "Failed to calculate market sensitivity: '" + benchmarkCode + "' benchmarks for period [" +
                        DateUtils.getDateFormatted(dateFrom) + ", " + DateUtils.getDateFormatted(dateTo) + "] expected " + monthDiff + ", found " + benchmarks.size() + ". ";
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }
            List<NicPortfolioDto> periodReturns = new ArrayList<>();
            if(nicPortfolioResultDto != null && nicPortfolioResultDto.getNicPortfolioDtoList() != null) {
                for (NicPortfolioDto dto : nicPortfolioResultDto.getNicPortfolioDtoList()) {
                    if (dto.getDate().compareTo(dateFrom) >= 0 && dto.getDate().compareTo(dateTo) <= 0) {
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

    /* TOP HEDGE FUNDS ALLOCATIONS ************************************************************************************/
    private ListResponseDto getHedgeFundsTopAllocations(Date date){
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReportDto periodicReportDto = this.periodicReportService.findReportByReportDate(date);
        if(periodicReportDto != null){
            ConsolidatedReportRecordHolderDto recordsHolder = this.hfGeneralLedgerBalanceService.getWithoutExcludedRecords(periodicReportDto.getId());
            if(recordsHolder != null && recordsHolder.getGeneralLedgerBalanceList() != null){
                Map<String, MonitoringRiskHedgeFundFundAllocationDto> fundNAVMap = new HashMap<>();
                Double total = 0.0;
                for(SingularityGeneralLedgerBalanceRecordDto dto: recordsHolder.getGeneralLedgerBalanceList()){
                    if(dto.getFinancialStatementCategoryDescription() != null &&
                            dto.getFinancialStatementCategoryDescription().contains(PeriodicReportConstants.INVESTMENT_IN_PORTFOLIO_FUNDS) && dto.getShortName() != null){
                        if(fundNAVMap.get(dto.getShortName()) == null) {
                            fundNAVMap.put(dto.getShortName(), new MonitoringRiskHedgeFundFundAllocationDto());
                        }
                        MonitoringRiskHedgeFundFundAllocationDto value = fundNAVMap.get(dto.getShortName());
                        value.setClassName(dto.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_A_LOWER_CASE) ? "A" :
                                dto.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_B_LOWER_CASE) ? "B" : "");
                        value.setNav(MathUtils.add(value.getNav(), dto.getGLAccountBalance()));
                    }

                    if(dto.getChartAccountsLongDescription() != null &&
                            dto.getChartAccountsLongDescription().trim().equalsIgnoreCase(PeriodicReportConstants.INVESTOR_SUBSCR_RECEIVED_IN_ADVANCE)){
                        // Exclude subscriptions from total
                        //total = MathUtils.add(total, dto.getGLAccountBalance());
                    }else if(dto.getFinancialStatementCategory() != null &&
                            (dto.getFinancialStatementCategory().trim().equalsIgnoreCase("A") ||
                                    dto.getFinancialStatementCategory().trim().equalsIgnoreCase("L"))) {
                        total = MathUtils.add(total, dto.getGLAccountBalance());
                    }
                }
                List<MonitoringRiskHedgeFundFundAllocationDto> valueList = new ArrayList<>();
                fundNAVMap.forEach((key, value)->{
                    value.setFundName(key);
                    valueList.add(value);
                });
                Collections.sort(valueList);

                // MTD
                Date previousMonthDate = DateUtils.getLastDayOfPreviousMonth(periodicReportDto.getReportDate());
                Map<String, MonitoringRiskHedgeFundFundAllocationDto> prevFundNAVMap = getHedgeFundNavMap(previousMonthDate);
                if(prevFundNAVMap == null){
                    String errorMessage = "Failed to calculate MTD: funds NAV data is missing for '" +
                            DateUtils.getDateFormatted(previousMonthDate) + "' (General Ledger Balance). ";
                    responseDto.appendErrorMessageEn(errorMessage);
                    logger.error(errorMessage);
                }
                //QTD
                int month = DateUtils.getMonth(periodicReportDto.getReportDate()) + 1;
                int year = DateUtils.getYear(periodicReportDto.getReportDate());
                int quarter = month / 3;
                Date quarterDate = DateUtils.getLastDayOfCurrentMonth(DateUtils.getDate("01." + (quarter < 4 ? "0" : "") + (quarter * 3) + "." + year));
                Map<String, MonitoringRiskHedgeFundFundAllocationDto> quarterFundNAVMap = getHedgeFundNavMap(quarterDate);
                if(quarterFundNAVMap == null){
                    String errorMessage = "Failed to calculate QTD: funds NAV data is missing for '" +
                            DateUtils.getDateFormatted(quarterDate) + "' (General Ledger Balance). ";
                    responseDto.appendErrorMessageEn(errorMessage);
                    logger.error(errorMessage);
                }
                //YTD
                Date ytdDate  = DateUtils.getDate("31.01." + year);
                Map<String, MonitoringRiskHedgeFundFundAllocationDto> yearFundNAVMap = getHedgeFundNavMap(ytdDate);
                if(yearFundNAVMap == null){
                    String errorMessage = "Failed to calculate YTD: funds NAV data is missing for '" +
                            DateUtils.getDateFormatted(ytdDate) + "' (General Ledger Balance). ";
                    responseDto.appendErrorMessageEn(errorMessage);
                    logger.error(errorMessage);
                }

                Double totalTop10 = 0.0;
                Double totalTop20 = 0.0;
                Double totalTop10MTD = null;
                Double totalTop20MTD = null;
                Double totalMTD = null;
                Double totalTop10QTD = null;
                Double totalTop20QTD = null;
                Double totalQTD = null;
                Double totalTop10YTD = null;
                Double totalTop20YTD = null;
                Double totalYTD = null;
                int index = 0;
                for(MonitoringRiskHedgeFundFundAllocationDto dto: valueList){
                    dto.setNavPercent(MathUtils.divide(4, dto.getNav(),total));
                    // MTD
                    if(prevFundNAVMap != null) {
                        Double prevNav = prevFundNAVMap.get(dto.getFundName()) != null ? prevFundNAVMap.get(dto.getFundName()).getNav() : null;
                        if (prevNav != null) {
                            Double mtd = MathUtils.divide(4, MathUtils.subtract(18, dto.getNav(), prevNav), dto.getNav());
                            dto.setMtd(mtd);
                        }
                    }
                    //QTD
                    if(quarterFundNAVMap != null) {
                        Double qNav = quarterFundNAVMap.get(dto.getFundName()) != null ? quarterFundNAVMap.get(dto.getFundName()).getNav() : null;
                        if (qNav != null) {
                            Double qtd = MathUtils.divide(4, MathUtils.subtract(18, dto.getNav(), qNav), dto.getNav());
                            dto.setQtd(qtd);
                        }
                    }
                    // YTD
                    if(yearFundNAVMap != null) {
                        Double yNav = yearFundNAVMap.get(dto.getFundName()) != null ? yearFundNAVMap.get(dto.getFundName()).getNav() : null;
                        if (yNav != null) {
                            Double ytd = MathUtils.divide(4, MathUtils.subtract(18, dto.getNav(), yNav), dto.getNav());
                            dto.setYtd(ytd);
                        }
                    }
                    if(index < 10){
                        totalTop10 = MathUtils.add(totalTop10, dto.getNav());
                        totalTop20 = MathUtils.add(totalTop20, dto.getNav());
                        // MTD
                        if(dto.getMtd() != null) {
                            totalTop10MTD = MathUtils.add(18, totalTop10MTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getMtd()));
                            totalTop20MTD = MathUtils.add(18, totalTop20MTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getMtd()));
                        }
                        //QTD
                        if(dto.getQtd() != null) {
                            totalTop10QTD = MathUtils.add(18, totalTop10QTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getQtd()));
                            totalTop20QTD = MathUtils.add(18, totalTop20QTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getQtd()));
                        }
                        //YTD
                        if(dto.getYtd() != null) {
                            totalTop10YTD = MathUtils.add(18, totalTop10YTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getYtd()));
                            totalTop20YTD = MathUtils.add(18, totalTop20YTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getYtd()));
                        }
                    }else if(index < 20){
                        totalTop20 = MathUtils.add(totalTop20, dto.getNav());
                        //MTD
                        if(dto.getMtd() != null) {
                            totalTop20MTD = MathUtils.add(18, totalTop20MTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getMtd()));
                        }
                        //QTD
                        if(dto.getQtd() != null) {
                            totalTop20QTD = MathUtils.add(18, totalTop20QTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getQtd()));
                        }
                        //YTD
                        if(dto.getYtd() != null) {
                            totalTop20YTD = MathUtils.add(18, totalTop20YTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getYtd()));
                        }
                    }
                    // MTD
                    if(dto.getMtd() != null) {
                        totalMTD = MathUtils.add(18, totalMTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getMtd()));
                    }
                    //QTD
                    if(dto.getQtd() != null) {
                        totalQTD = MathUtils.add(18, totalQTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getQtd()));
                    }
                    //YTD
                    if(dto.getYtd() != null) {
                        totalYTD = MathUtils.add(18, totalYTD, MathUtils.multiply(18, dto.getNavPercent(), dto.getYtd()));
                    }

                    index++;
                }

                List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = new ArrayList<>();
                topFundAllocations.addAll(valueList.subList(0, 10));

                MonitoringRiskHedgeFundFundAllocationDto top10TotalRecord = new MonitoringRiskHedgeFundFundAllocationDto();
                top10TotalRecord.setFundName("TOP 10");
                top10TotalRecord.setNavPercent(MathUtils.divide(4, totalTop10, total));
                top10TotalRecord.setNav(totalTop10);
                top10TotalRecord.setMtd(totalTop10MTD != null ? MathUtils.add(4, 0.0, totalTop10MTD) : null);
                top10TotalRecord.setQtd(totalTop10QTD != null ? MathUtils.add(4, 0.0, totalTop10QTD) : null);
                top10TotalRecord.setYtd(totalTop10YTD != null ? MathUtils.add(4, 0.0, totalTop10YTD) : null);
                topFundAllocations.add(top10TotalRecord);

                MonitoringRiskHedgeFundFundAllocationDto top20TotalRecord = new MonitoringRiskHedgeFundFundAllocationDto();
                top20TotalRecord.setFundName("TOP 20");
                top20TotalRecord.setNavPercent(MathUtils.divide(4, totalTop20, total));
                top20TotalRecord.setNav(totalTop20);
                top20TotalRecord.setMtd(totalTop20MTD != null ? MathUtils.add(4, 0.0, totalTop20MTD) : null);
                top20TotalRecord.setQtd(totalTop20QTD != null ? MathUtils.add(4, 0.0, totalTop20QTD) : null);
                top20TotalRecord.setYtd(totalTop20YTD != null ? MathUtils.add(4, 0.0, totalTop20YTD) : null);
                topFundAllocations.add(top20TotalRecord);

                MonitoringRiskHedgeFundFundAllocationDto totalRecord = new MonitoringRiskHedgeFundFundAllocationDto();
                totalRecord.setFundName("TOTAL");
                totalRecord.setNavPercent(1.0);
                totalRecord.setNav(total);
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
        }
        return null;
    }

    private Map<String, MonitoringRiskHedgeFundFundAllocationDto> getHedgeFundNavMap(Date date){
        Map<String, MonitoringRiskHedgeFundFundAllocationDto> fundNAVMap = new HashMap<>();
        PeriodicReportDto report = this.periodicReportService.findReportByReportDate(date);
        if(report != null) {
            ConsolidatedReportRecordHolderDto recordHolderDto = this.hfGeneralLedgerBalanceService.getWithoutExcludedRecords(report.getId());
            if (recordHolderDto != null && recordHolderDto.getGeneralLedgerBalanceList() != null && !recordHolderDto.getGeneralLedgerBalanceList().isEmpty()) {
                for (SingularityGeneralLedgerBalanceRecordDto dto : recordHolderDto.getGeneralLedgerBalanceList()) {
                    if (dto.getFinancialStatementCategoryDescription() != null &&
                            dto.getFinancialStatementCategoryDescription().contains(PeriodicReportConstants.INVESTMENT_IN_PORTFOLIO_FUNDS) && dto.getShortName() != null) {
                        if (fundNAVMap.get(dto.getShortName()) == null) {
                            fundNAVMap.put(dto.getShortName(), new MonitoringRiskHedgeFundFundAllocationDto());
                        }
                        MonitoringRiskHedgeFundFundAllocationDto value = fundNAVMap.get(dto.getShortName());
                        value.setClassName(dto.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_A_LOWER_CASE) ? "A" :
                                dto.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_B_LOWER_CASE) ? "B" : "");
                        value.setNav(MathUtils.add(value.getNav(), dto.getGLAccountBalance()));
                    }
                }
                return fundNAVMap;
            }
        }
        return null;
    }
    /* ****************************************************************************************************************/

    /* HEDGE FUNDS FACTOR BETAS ***************************************************************************************/
    private ListResponseDto getHedgeFundsFactorBetas(Date date, NicPortfolioResultDto nicPortfolioResultDto){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundBetaFactorDto> records = new ArrayList<>();

        // Dates
        Date dateTo = DateUtils.getLastDayOfCurrentMonth(date);
        Date dateFrom12M = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(date, -11));
        Date dateFromSinceInception = DateUtils.getDate("31.08.2015");
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
        //List<BenchmarkValueDto> tbills = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.T_BILLS.getCode());
        int monthDiff = DateUtils.getMonthsChanged(dateFrom, dateTo);
        if (tbills == null || tbills.size() != monthDiff) {
            String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing T-bills records for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + monthDiff + ", found " + tbills.size();
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        //List<BenchmarkValueDto> hfri = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.HFRI.getCode());
        if (benchmarks == null || benchmarks.size() != monthDiff) {
            String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing " + benchmarkName + " records for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + monthDiff + ", found " + benchmarks.size();
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        if (nicPortfolioResultDto == null || nicPortfolioResultDto.getNicPortfolioDtoList() == null || nicPortfolioResultDto.getNicPortfolioDtoList().isEmpty()) {
            String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing Singularity returns for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }else{
            List<NicPortfolioDto> records12M = new ArrayList<>();
            for(NicPortfolioDto nicPortfolioDto: nicPortfolioResultDto.getNicPortfolioDtoList()){
                if(nicPortfolioDto.getHedgeFundsMtd() != null &&
                        nicPortfolioDto.getDate().compareTo(dateFrom) >= 0 && nicPortfolioDto.getDate().compareTo(dateTo) <= 0){
                    records12M.add(nicPortfolioDto);
                }
            }
            if(records12M.size() != monthDiff){
                String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing Singularity returns for period [" + DateUtils.getDateFormatted(dateFrom) +
                        ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + monthDiff + ", found " + records12M.size();
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
            double value = MathUtils.calculateSlope(data);
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
            List<BenchmarkValueDto> benchmark12M = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom12M, dateTo, lookup.getCode());
            List<BenchmarkValueDto> benchmarkSI = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFromSinceInception, dateTo, lookup.getCode());
            map.put(lookup.getCode(), benchmark12M);
            map.put(lookup.getCode() + "_SI", benchmarkSI);
        }
        return map;
    }
    /* ****************************************************************************************************************/

    /* HEDGE FUNDS PERFORMANCE SUMMARY 12M ****************************************************************************/
    private ListResponseDto getHedgeFundsPerformanceSummary12M(Date dateFrom, Date dateTo, List<DateDoubleValue> returnValues, boolean isBenchmark){
        int period = DateUtils.getMonthsChanged(dateFrom, dateTo);
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundPerformanceRecordDto> performance = new ArrayList<>();
        if(returnValues != null && !returnValues.isEmpty()){
            List<DateDoubleValue> records12M = new ArrayList<>();
            List<DateDoubleValue> recordsSinceInception = new ArrayList<>();
            for(DateDoubleValue value: returnValues){
                if(value.getValue() != null){
                    if(value.getDate().compareTo(dateFrom) >= 0 && value.getDate().compareTo(dateTo) <= 0){
                        records12M.add(value);
                    }
                    recordsSinceInception.add(value);
                }
            }

            if(records12M.size() != period){
                String errorMessage = "Missing Hedge Funds MTD records for period [" + DateUtils.getDateFormatted(dateFrom) +
                ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found " + records12M.size();
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }

            Collections.sort(records12M);
            double[] returns = new double[period];
            for(int i = 0; i < records12M.size(); i++){
                returns[i] = records12M.get(i).getValue();
            }
            double[] returnsSI = new double[recordsSinceInception.size()];
            for(int i = 0; i < recordsSinceInception.size(); i++){
                returnsSI[i] = recordsSinceInception.get(i).getValue();
            }
            List<BenchmarkValueDto> tbills = this.benchmarkService.getBenchmarkValuesForDatesAndType(dateFrom, dateTo, BenchmarkLookup.T_BILLS.getCode());
            if(tbills == null || tbills.size() != period){
                String errorMessage = "Missing T-bills records for period [" + DateUtils.getDateFormatted(dateFrom) +
                        ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found " + tbills.size();
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }
            double[] tbillsReturns = new double[tbills.size()];
            for(int i = 0; i < tbillsReturns.length; i++){
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
            Double std = MathUtils.getStandardDeviation(returns);
            if(std != null) {
                Double annStd = MathUtils.multiply(18, std, Math.sqrt(12));
                Double annStd1 = isBenchmark ? null : annStd;
                Double annStd2 = !isBenchmark ? null : annStd;
                performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Ann Standard Deviation", annStd1, annStd2,
                        annStd1 != null ? (MathUtils.multiply(4, annStd1, 100.0) + "%") : null,
                        annStd2 != null ? (MathUtils.multiply(4, annStd2, 100.0) + "%") : null));
            }

            // Downside Deviation
            Double downsideDeviation = MathUtils.getDownsideDeviation(returns);
            Double downsideDeviation1 = isBenchmark ? null : downsideDeviation;
            Double downsideDeviation2 = !isBenchmark ? null : downsideDeviation;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Downside Deviation", downsideDeviation1, downsideDeviation2,
                    downsideDeviation1 != null ? (MathUtils.multiply(4, downsideDeviation1, 100.0) + "%") : null,
                    downsideDeviation2 != null ? (MathUtils.multiply(4, downsideDeviation2, 100.0) + "%") : null));

            // Sharpe Ratio
            Double sharpeRatio = MathUtils.getSharpeRatio(18, returns, tbillsReturns);
            Double sharpeRatio1 = isBenchmark ? null : sharpeRatio;
            Double sharpeRatio2 = !isBenchmark ? null : sharpeRatio;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Sharpe Ratio", sharpeRatio1, sharpeRatio2,
                    sharpeRatio1 != null ? MathUtils.add(sharpeRatio1, 0.0).toString() : null,
                    sharpeRatio2 != null ? MathUtils.add(sharpeRatio2, 0.0).toString() : null));

            // Sortino
            Double annRoRTbills = MathUtils.getAnnualizedReturn(tbillsReturns, 18);
            Double sortino = MathUtils.getSortinoRatio(annRoR, annRoRTbills, returns, 18);
            Double sortino1 = isBenchmark ? null : sortino;
            Double sortino2 = !isBenchmark ? null : sortino;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Sortino Ratio", sortino1, sortino2,
                    sortino1 != null ? MathUtils.add(sortino1, 0.0).toString() : null,
                    sortino2 != null ? MathUtils.add(sortino2, 0.0).toString() : null));

            // Worst Drawdown
            double[] cumulativeReturnsSI = MathUtils.getCumulativeReturnsFromInitial(18, returnsSI, 1.0);
            double[] cumulativeReturns12M = new double[returns.length];
            int j = 0;
            for(int i = 0; i < recordsSinceInception.size(); i++){
                if(recordsSinceInception.get(i).getDate().compareTo(dateFrom) >= 0 && recordsSinceInception.get(i).getDate().compareTo(dateTo) <= 0){
                    if(j < cumulativeReturns12M.length) {
                        cumulativeReturns12M[j] = cumulativeReturnsSI[i];
                        j++;
                    }else{
                        // TODO: error?
                    }
                }
            }
            if(j != period){
                String errorMessage = "Missing calculated value added monthly return for period [" + DateUtils.getDateFormatted(dateFrom) +
                        ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found " + tbills.size();
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }
            WorstDrawdownDto worstDDDto = MathUtils.getWorstDrawdown(18, cumulativeReturns12M);
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
                    positiveSum = MathUtils.add(positiveSum, returns[i]);
                }else{
                    negatives += 1;
                    negativeSum = MathUtils.add(negativeSum, returns[i]);
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

                gainLossRatio = Math.abs(MathUtils.divide(18, positiveAvg, negativeAvg));
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
