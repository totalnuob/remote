package kz.nicnbk.service.impl.monitoring;

import kz.nicnbk.common.service.exception.ExcelFileParseException;
import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.risk.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.PortfolioVarLookup;
import kz.nicnbk.repo.model.lookup.reporting.MonitoringRiskHFPortfolioTypeLookup;
import kz.nicnbk.repo.model.risk.*;
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
import kz.nicnbk.service.converter.files.FilesEntityConverter;
import kz.nicnbk.service.converter.risk.AllocationBySubStrategyEntityConverter;
import kz.nicnbk.service.converter.risk.AllocationByTopPortfolioEntityConverter;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import kz.nicnbk.service.dto.monitoring.*;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.risk.PortfolioVarValueDto;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.annotation.ExceptionProxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class MonitoringRiskServiceImpl implements MonitoringRiskService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringRiskServiceImpl.class);
    public static final Date SINGULAR_PORTFOLIO_START_DATE = DateUtils.getDate("31.08.2015");
    public static final Date SINGULAR_PORTFOLIO_CLASS_B_START_DATE = DateUtils.getDate("31.01.2017");

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
    private AllocationBySubStrategyRepository subStrategyRepository;

    @Autowired
    private AllocationByTopPortfolioRepository topPortfolioRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private AllocationBySubStrategyEntityConverter subStrategyEntityConverter;

    @Autowired
    private AllocationByTopPortfolioEntityConverter topPortfolioEntityConverter;

    @Autowired
    private MonitoringRiskHFMonthlyReportRepository riskReportRepository;

    @Autowired
    private MonitoringRiskHFPortfolioReturnRepository riskHFReturnRepository;

    @Autowired
    private MonitoringRiskHFPortfolioAllocationMonthRepository riskHFPortfolioAllocationMonthRepository;

    @Autowired
    private MonitoringRiskHFPortfolioAllocationQuarterRepository riskHFPortfolioAllocationQuarterRepository;

    @Autowired
    private MonitoringRiskHFPortfolioAllocationYearRepository riskHFPortfolioAllocationYearRepository;

    @Autowired
    private FilesEntityConverter filesEntityConverter;

    @Override
    public MonitoringRiskHedgeFundReportDto getMonthlyHedgeFundRiskReport(MonitoringRiskReportSearchParamsDto searchParamsDto){
        MonitoringRiskHedgeFundReportDto reportDto = new MonitoringRiskHedgeFundReportDto();

        MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findByReportDate(searchParamsDto.getDate());
        if(report == null){
            return null;
        }
        reportDto.setReportId(report.getId());

        MonitoringRiskHFMonthlyReport previousReport = this.riskReportRepository.findByReportDate(searchParamsDto.getPreviousDate());

        if(report.getReturnsClassAFile() != null) {
            FilesDto filesDto = this.filesEntityConverter.disassemble(report.getReturnsClassAFile());
            reportDto.setReturnsFileClassA(filesDto);
        }
        if(report.getReturnsClassBFile() != null) {
            FilesDto filesDto = this.filesEntityConverter.disassemble(report.getReturnsClassBFile());
            reportDto.setReturnsFileClassB(filesDto);
        }

        if(report.getReturnsConsFile() != null) {
            FilesDto filesDto = this.filesEntityConverter.disassemble(report.getReturnsConsFile());
            reportDto.setReturnsFileCons(filesDto);
        }

        if(report.getAllocationsConsFile() != null) {
            FilesDto filesDto = this.filesEntityConverter.disassemble(report.getAllocationsConsFile());
            reportDto.setAllocationsFileCons(filesDto);
        }
        // Performance summary
        Date dateTo = DateUtils.getLastDayOfCurrentMonth(searchParamsDto.getDate());
        Date dateFrom12M = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(searchParamsDto.getDate(), -11));
        ListResponseDto performance12MResponsePortfolioA = null;
        ListResponseDto performance12MResponsePortfolioB = null;
        ListResponseDto performance12MResponseHfriFoF = null;
        ListResponseDto performance12MResponseHfriAWC = null;

        // Get returns
        List<DateDoubleValue> valuesA = new ArrayList<>();
        List<DateDoubleValue> valuesB = new ArrayList<>();
        List<DateDoubleValue> valuesCons = new ArrayList<>();
        List<MonitoringRiskHFPortfolioReturn> returns = this.riskHFReturnRepository.findByReportId(report.getId());
        if(returns != null){
            for(MonitoringRiskHFPortfolioReturn value: returns){
                if(value.getPortfolioType().getCode().equalsIgnoreCase(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_A.getCode())){
                    DateDoubleValue valueA = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(value.getDate()), value.getValue());
                    valuesA.add(valueA);
                }else if(value.getPortfolioType().getCode().equalsIgnoreCase(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_B.getCode())){
                    DateDoubleValue valueB = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(value.getDate()), value.getValue());
                    valuesB.add(valueB);
                }else if(value.getPortfolioType().getCode().equalsIgnoreCase(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_CONS.getCode())){
                    DateDoubleValue valueCons = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(value.getDate()), value.getValue());
                    valuesCons.add(valueCons);
                }
            }
        }
        // 12M
        performance12MResponsePortfolioA = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesA, 1,false);
        performance12MResponsePortfolioB = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesB, 2, false);


        String[] benchmarkCodesHFRIFOF = {BenchmarkLookup.HFRIFOF.getCode()};
        String[] benchmarkCodesHFRIAWC = {BenchmarkLookup.HFRIAWC.getCode()};
        List<BenchmarkValueDto> hfrifof12M = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom12M, dateTo, 10, benchmarkCodesHFRIFOF);
        List<BenchmarkValueDto> hfriawc12M = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom12M, dateTo, 10, benchmarkCodesHFRIAWC);
        if(hfrifof12M != null && !hfrifof12M.isEmpty()){
            List<DateDoubleValue> valuesHfriFoF = new ArrayList<>();
            for(BenchmarkValueDto dto: hfrifof12M){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                valuesHfriFoF.add(value);
            }
            performance12MResponseHfriFoF = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesHfriFoF, 1, true);
        }
        if (hfriawc12M != null && !hfriawc12M.isEmpty()){
            List<DateDoubleValue> valuesHfriAWC = new ArrayList<>();
            for (BenchmarkValueDto dto : hfriawc12M) {
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                valuesHfriAWC.add(value);
            }
            performance12MResponseHfriAWC = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesHfriAWC, 2, true);
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
        Date dateFromSIClassB = DateUtils.getLastDayOfCurrentMonth(SINGULAR_PORTFOLIO_CLASS_B_START_DATE);
        ListResponseDto performanceSIResponsePortfolioA = null;
        ListResponseDto performanceSIResponsePortfolioB = null;
        ListResponseDto performanceSIResponseHfriFoF = null;
        ListResponseDto performanceSIResponseHfriAWC = null;

        performanceSIResponsePortfolioA = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesA, 1, false);
        performanceSIResponsePortfolioB = getHedgeFundsPerformanceSummary(dateFromSIClassB, dateTo, valuesB, 2, false);

        List<BenchmarkValueDto> hfriFoFSI = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSI, dateTo, 10, benchmarkCodesHFRIFOF);
        List<BenchmarkValueDto> hfriAWCSI = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSIClassB, dateTo, 10, benchmarkCodesHFRIAWC);
        if(hfriFoFSI != null && !hfriFoFSI.isEmpty()){
            List<DateDoubleValue> valuesHfriFoF = new ArrayList<>();
            for(BenchmarkValueDto dto: hfriFoFSI){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                valuesHfriFoF.add(value);
            }

            performanceSIResponseHfriFoF = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesHfriFoF,1, true);
        }
        if (hfriAWCSI != null && !hfriAWCSI.isEmpty()) {
            List<DateDoubleValue> valuesHfriAWC = new ArrayList<>();
            for (BenchmarkValueDto dto: hfriAWCSI){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                valuesHfriAWC.add(value);
            }
            performanceSIResponseHfriAWC = getHedgeFundsPerformanceSummary(dateFromSIClassB, dateTo, valuesHfriAWC, 2, true);
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

        // Set previous period values
        if(previousReport != null){
            List<DateDoubleValue> prevValuesA = new ArrayList<>();
            List<DateDoubleValue> prevValuesB = new ArrayList<>();
            List<DateDoubleValue> prevValuesCons = new ArrayList<>();
            List<MonitoringRiskHFPortfolioReturn> prevReturns = this.riskHFReturnRepository.findByReportId(previousReport.getId());
            if(returns != null){
                for(MonitoringRiskHFPortfolioReturn value: prevReturns){
                    if(value.getPortfolioType().getCode().equalsIgnoreCase(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_A.getCode())){
                        DateDoubleValue valueA = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(value.getDate()), value.getValue());
                        prevValuesA.add(valueA);
                    }else if(value.getPortfolioType().getCode().equalsIgnoreCase(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_B.getCode())){
                        DateDoubleValue valueB = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(value.getDate()), value.getValue());
                        prevValuesB.add(valueB);
                    }else if(value.getPortfolioType().getCode().equalsIgnoreCase(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_CONS.getCode())){
                        DateDoubleValue valueCons = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(value.getDate()), value.getValue());
                        prevValuesCons.add(valueCons);
                    }
                }
            }
            Date prevDateFrom = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(previousReport.getReportDate(), -11));
            Date prevDateTo = DateUtils.getLastDayOfCurrentMonth(previousReport.getReportDate());

            if(reportDto.getPerformance12M() != null && !reportDto.getPerformance12M().isEmpty()){
                for(MonitoringRiskHedgeFundPerformanceRecordDto currRecord: reportDto.getPerformance12M()){
                    // 12M class A
                    ListResponseDto prevPerformance12MResponsePortfolioA = getHedgeFundsPerformanceSummary(prevDateFrom, prevDateTo, prevValuesA, 1,false);
                    if(prevPerformance12MResponsePortfolioA.getRecords() != null && !prevPerformance12MResponsePortfolioA.getRecords().isEmpty()){
                        for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) prevPerformance12MResponsePortfolioA.getRecords()){
                            if(currRecord.getName().equalsIgnoreCase(prevRecord.getName())){
                                currRecord.setPortfolioValuePrev(prevRecord.getPortfolioValue());
                                currRecord.setPortfolioValueTxtPrev(prevRecord.getPortfolioValueTxt());
                            }
                        }
                    }
                    // 12M class B
                    ListResponseDto prevPerformance12MResponsePortfolioB = getHedgeFundsPerformanceSummary(prevDateFrom, prevDateTo, prevValuesB, 2, false);
                    if(prevPerformance12MResponsePortfolioB.getRecords() != null && !prevPerformance12MResponsePortfolioB.getRecords().isEmpty()){
                        for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) prevPerformance12MResponsePortfolioB.getRecords()){
                            if(currRecord.getName().equalsIgnoreCase(prevRecord.getName())){
                                currRecord.setPortfolioBValuePrev(prevRecord.getPortfolioBValue());
                                currRecord.setPortfolioBValueTxtPrev(prevRecord.getPortfolioBValueTxt());
                            }
                        }
                    }
                    //12M Benchmark HRFI
                    List<BenchmarkValueDto> hfriFoFPrev = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(prevDateFrom, prevDateTo, 10, benchmarkCodesHFRIFOF);
                    if(hfriFoFPrev != null && !hfriFoFPrev.isEmpty()){
                        List<DateDoubleValue> valuesHfriFoFPrev = new ArrayList<>();
                        for(BenchmarkValueDto dto: hfriFoFPrev){
                            DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                            valuesHfriFoFPrev.add(value);
                        }

                        ListResponseDto performance12MResponseHfriFoFPrev = getHedgeFundsPerformanceSummary(prevDateFrom, prevDateTo, valuesHfriFoFPrev,1, true);
                        if(performance12MResponseHfriFoFPrev.getRecords() != null && !performance12MResponseHfriFoFPrev.getRecords().isEmpty()){
                            for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) performance12MResponseHfriFoFPrev.getRecords()) {
                                if (currRecord.getName().equalsIgnoreCase(prevRecord.getName())) {
                                    currRecord.setBenchmarkValuePrev(prevRecord.getBenchmarkValue());
                                    currRecord.setBenchmarkValueTxtPrev(prevRecord.getBenchmarkValueTxt());
                                }

                            }
                        }
                    }
                    // 12M Benchmark AWC
                    List<BenchmarkValueDto> hfriAWCPrev = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(prevDateFrom, prevDateTo, 10, benchmarkCodesHFRIAWC);
                    if (hfriAWCPrev != null && !hfriAWCPrev.isEmpty()) {
                        List<DateDoubleValue> valuesHfriAWCPrev = new ArrayList<>();
                        for (BenchmarkValueDto dto: hfriAWCPrev){
                            DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                            valuesHfriAWCPrev.add(value);
                        }
                        ListResponseDto performance12MResponseHfriAWCPrev = getHedgeFundsPerformanceSummary(prevDateFrom, prevDateTo, valuesHfriAWCPrev, 2, true);
                        if(performance12MResponseHfriAWCPrev.getRecords() != null && !performance12MResponseHfriAWCPrev.getRecords().isEmpty()){
                            for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) performance12MResponseHfriAWCPrev.getRecords()) {
                                if (currRecord.getName().equalsIgnoreCase(prevRecord.getName())) {
                                    currRecord.setBenchmarkAwcValuePrev(prevRecord.getBenchmarkAwcValue());
                                    currRecord.setBenchmarkAwcValueTxtPrev(prevRecord.getBenchmarkAwcValueTxt());
                                }
                            }
                        }
                    }
                }
            }
            if(reportDto.getPerformanceSinceInception() != null && !reportDto.getPerformanceSinceInception().isEmpty()){
                for(MonitoringRiskHedgeFundPerformanceRecordDto currRecord: reportDto.getPerformanceSinceInception()){
                    // SI class A
                    ListResponseDto prevPerformanceSIResponsePortfolioA = getHedgeFundsPerformanceSummary(dateFromSI, prevDateTo, prevValuesA, 1,false);
                    if(prevPerformanceSIResponsePortfolioA.getRecords() != null && !prevPerformanceSIResponsePortfolioA.getRecords().isEmpty()){
                        for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) prevPerformanceSIResponsePortfolioA.getRecords()){
                            if(currRecord.getName().equalsIgnoreCase(prevRecord.getName())){
                                currRecord.setPortfolioValuePrev(prevRecord.getPortfolioValue());
                                currRecord.setPortfolioValueTxtPrev(prevRecord.getPortfolioValueTxt());
                            }
                        }
                    }
                    // SI class B
                    ListResponseDto prevPerformanceSIResponsePortfolioB = getHedgeFundsPerformanceSummary(dateFromSIClassB, prevDateTo, prevValuesB, 2, false);
                    if(prevPerformanceSIResponsePortfolioB.getRecords() != null && !prevPerformanceSIResponsePortfolioB.getRecords().isEmpty()){
                        for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) prevPerformanceSIResponsePortfolioB.getRecords()){
                            if(currRecord.getName().equalsIgnoreCase(prevRecord.getName())){
                                currRecord.setPortfolioBValuePrev(prevRecord.getPortfolioBValue());
                                currRecord.setPortfolioBValueTxtPrev(prevRecord.getPortfolioBValueTxt());
                            }
                        }
                    }
                    //SI Benchmark HRFI
                    List<BenchmarkValueDto> hfriFoFPrev = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSI, prevDateTo, 10, benchmarkCodesHFRIFOF);
                    if(hfriFoFPrev != null && !hfriFoFPrev.isEmpty()){
                        List<DateDoubleValue> valuesHfriFoFPrev = new ArrayList<>();
                        for(BenchmarkValueDto dto: hfriFoFPrev){
                            DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                            valuesHfriFoFPrev.add(value);
                        }

                        ListResponseDto performanceSIResponseHfriFoFPrev = getHedgeFundsPerformanceSummary(dateFromSI, prevDateTo, valuesHfriFoFPrev,1, true);
                        if(performanceSIResponseHfriFoFPrev.getRecords() != null && !performanceSIResponseHfriFoFPrev.getRecords().isEmpty()){
                            for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) performanceSIResponseHfriFoFPrev.getRecords()) {
                                if (currRecord.getName().equalsIgnoreCase(prevRecord.getName())) {
                                    currRecord.setBenchmarkValuePrev(prevRecord.getBenchmarkValue());
                                    currRecord.setBenchmarkValueTxtPrev(prevRecord.getBenchmarkValueTxt());
                                }

                            }
                        }
                    }
                    // SI Benchmark AWC
                    List<BenchmarkValueDto> hfriAWCPrev = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSIClassB, prevDateTo, 10, benchmarkCodesHFRIAWC);
                    if (hfriAWCPrev != null && !hfriAWCPrev.isEmpty()) {
                        List<DateDoubleValue> valuesHfriAWCPrev = new ArrayList<>();
                        for (BenchmarkValueDto dto: hfriAWCPrev){
                            DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                            valuesHfriAWCPrev.add(value);
                        }
                        ListResponseDto performanceSIResponseHfriAWCPrev = getHedgeFundsPerformanceSummary(dateFromSIClassB, prevDateTo, valuesHfriAWCPrev, 2, true);
                        if(performanceSIResponseHfriAWCPrev.getRecords() != null && !performanceSIResponseHfriAWCPrev.getRecords().isEmpty()){
                            for(MonitoringRiskHedgeFundPerformanceRecordDto prevRecord: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) performanceSIResponseHfriAWCPrev.getRecords()) {
                                if (currRecord.getName().equalsIgnoreCase(prevRecord.getName())) {
                                    currRecord.setBenchmarkAwcValuePrev(prevRecord.getBenchmarkAwcValue());
                                    currRecord.setBenchmarkAwcValueTxtPrev(prevRecord.getBenchmarkAwcValueTxt());
                                }
                            }
                        }
                    }
                }
            }
        }

        // Factor Betas
        ListResponseDto factorBetasResponse = getHedgeFundsFactorBetas(searchParamsDto.getDate(), valuesCons);
        if(!factorBetasResponse.isStatusOK()){
            reportDto.setFactorBetasError(factorBetasResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundBetaFactorDto> factorBetas = factorBetasResponse.getRecords();
        reportDto.setFactorBetas(factorBetas);

        // Market Sensitivity
        // MSCI
        ListResponseDto marketSensitivityMSCIResponse = getMarketSensitivity(BenchmarkLookup.MSCI_ACWI_IMI.getCode(), dateFromSI, dateTo, valuesCons);
        if(!marketSensitivityMSCIResponse.isStatusOK()){
            reportDto.setMarketSensitivityError(marketSensitivityMSCIResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesMSCIRecords = marketSensitivityMSCIResponse.getRecords();
        reportDto.setMarketSensitivitesMSCI(marketSensitivitesMSCIRecords);

        // LEGATRUH
        ListResponseDto marketSensitivityBarclaysGlobalResponse = getMarketSensitivity(BenchmarkLookup.LEGATRUH.getCode(), dateFromSI, dateTo, valuesCons);
        if(!marketSensitivityBarclaysGlobalResponse.isStatusOK()){
            reportDto.setMarketSensitivityError(marketSensitivityBarclaysGlobalResponse.getErrorMessageEn());
        }
        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesBarclaysRecords = marketSensitivityBarclaysGlobalResponse.getRecords();
        reportDto.setMarketSensitivitesBarclays(marketSensitivitesBarclaysRecords);

        // VARS
        ListResponseDto portfolioVar95Response = getHedgeFundPortfolioVars(searchParamsDto.getDate(), PortfolioVarLookup.VAR95.getCode());
        if(!portfolioVar95Response.isStatusOK()){
            reportDto.setPortfolioVarsError(portfolioVar95Response.getErrorMessageEn());
        }
        ListResponseDto portfolioVar99Response = getHedgeFundPortfolioVars(searchParamsDto.getDate(), PortfolioVarLookup.VAR99.getCode());
        if(!portfolioVar99Response.isStatusOK()){
            if(reportDto.getPortfolioVarsError() != null){
                reportDto.setPortfolioVarsError(reportDto.getPortfolioVarsError() + " " +  portfolioVar99Response.getErrorMessageEn());
            }else{
                reportDto.setPortfolioVarsError(portfolioVar99Response.getErrorMessageEn());
            }
        }
        List<MonitoringRiskHedgeFundPortfolioVarDto> portfolioVars = new ArrayList<>();
        if(portfolioVar95Response.getRecords() != null) {
            portfolioVars.addAll(portfolioVar95Response.getRecords());
        }
        if(portfolioVar99Response.getRecords() != null) {
            portfolioVars.addAll(portfolioVar99Response.getRecords());
        }
        reportDto.setPortfolioVars(portfolioVars);

        // Top fund allocations
        ListResponseDto topFundAllocationsResponse = getTopFundAllocationsByNav(report.getId());
        if (topFundAllocationsResponse != null) {
            if (!topFundAllocationsResponse.isStatusOK()) {
                reportDto.setTopFundAllocationsError(topFundAllocationsResponse.getErrorMessageEn());
            }
            List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = topFundAllocationsResponse.getRecords();
            reportDto.setTopFundAllocations(topFundAllocations);
        }

        // Allocation by strategy
        //Sub-strategy allocations
        Long previousReportId = previousReport != null ? previousReport.getId() : null;
        ListResponseDto subStrategyAllocationsResponse = getAllocationBySubStrategy(report.getId(), previousReportId);
        if (subStrategyAllocationsResponse != null) {
            if (!subStrategyAllocationsResponse.isStatusOK()) {
                reportDto.setSubStrategyAllocationsError(subStrategyAllocationsResponse.getErrorMessageEn());
            }
            List<MonitoringRiskHedgeFundAllocationSubStrategyDto> subStrategyAllocations = subStrategyAllocationsResponse.getRecords();
            reportDto.setSubStrategyAllocations(subStrategyAllocations);
        }

        // Stress Tests
        List<RiskStressTestsDto> stressTests = riskStressTestsService.getStressTestsByDate(report.getReportDate());
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

    private ListResponseDto getTopFundAllocationsByNav(Long reportId){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundFundAllocationDto> recordsAll = new ArrayList<>();
        List<MonitoringRiskHedgeFundFundAllocationDto> recordsTop = new ArrayList<>();
        List<MonitoringRiskHFAllocationMonth> allocationsMonths = this.riskHFPortfolioAllocationMonthRepository.findByReportId(reportId);
        List<MonitoringRiskHFAllocationQuarter> allocationsQuarter = this.riskHFPortfolioAllocationQuarterRepository.findByReportId(reportId);
        List<MonitoringRiskHFAllocationYear> allocationsYear = this.riskHFPortfolioAllocationYearRepository.findByReportId(reportId);

        if(allocationsMonths != null && !allocationsMonths.isEmpty()){
            double totalNav = 0.0;
            Map<String, Double> gainLossMap = new HashMap<>();
            Map<String, Double> openingBalanceMap = new HashMap<>();
            for(MonitoringRiskHFAllocationMonth allocationMonth: allocationsMonths){
                totalNav = MathUtils.add(18, totalNav, allocationMonth.getEndingBalance());
            }
            for(MonitoringRiskHFAllocationMonth allocationMonth: allocationsMonths){
                if(allocationMonth.getFund().equalsIgnoreCase("N/A")){
                    continue;
                }
                MonitoringRiskHedgeFundFundAllocationDto record = new MonitoringRiskHedgeFundFundAllocationDto();
                record.setFundName(allocationMonth.getFund());
                //record.setClassName();
                record.setNav(allocationMonth.getEndingBalance());
                if(record.getNav() != null && record.getNav().doubleValue() != 0.0) {
                    record.setNavPercent(MathUtils.divide(18, record.getNav(), totalNav));
                }
                record.setMtd(allocationMonth.getRor());

                gainLossMap.put(record.getFundName(), allocationMonth.getGainLoss());
                openingBalanceMap.put(record.getFundName(), allocationMonth.getOpeningBalance());
                //record.setCtr(allocationMonth.getCtr());
                recordsAll.add(record);
            }

            // set QTD
            Map<String, Double> allocationQtdMap = new HashMap<>();
            if(allocationsQuarter != null && !allocationsQuarter.isEmpty()){
                for (MonitoringRiskHFAllocationQuarter allocationQuarter : allocationsQuarter) {
                    if(allocationQuarter.getFund().equalsIgnoreCase("N/A")){
                        continue;
                    }
                    allocationQtdMap.put(allocationQuarter.getFund(), allocationQuarter.getRor());
                }
            }

            // set YTD
            Map<String, Double> allocationYtdMap = new HashMap<>();
            Map<String, Double> allocationContribYtdMap = new HashMap<>();
            Map<String, Double> allocationContribToVarMap = new HashMap<>();
            if(allocationsYear != null && !allocationsYear.isEmpty()){
                for (MonitoringRiskHFAllocationYear allocationYear : allocationsYear) {
                    if(allocationYear.getFund().equalsIgnoreCase("N/A")){
                        continue;
                    }
                    allocationYtdMap.put(allocationYear.getFund(), allocationYear.getRor());
                    allocationContribYtdMap.put(allocationYear.getFund(), allocationYear.getCtr());
                    allocationContribToVarMap.put(allocationYear.getFund(), allocationYear.getContribToVar());
                }
            }
            for(MonitoringRiskHedgeFundFundAllocationDto record: recordsAll) {
                record.setQtd(allocationQtdMap.get(record.getFundName()));
                record.setYtd(allocationYtdMap.get(record.getFundName()));
                record.setContributionToYTD(allocationContribYtdMap.get(record.getFundName()));
                record.setContributionToVAR(allocationContribToVarMap.get(record.getFundName()));
            }

            Collections.sort(recordsAll);

            // Calculate TOP
            double top10Nav = 0.0;
            double top10GainLoss = 0.0;
            double top10OpeningBalance = 0.0;
            double top10contributionToYTD = 0.0;
            double top10contributionToVar = 0.0;

            double top20Nav = 0.0;
            double top20GainLoss = 0.0;
            double top20OpeningBalance = 0.0;
            double top20contributionToVar = 0.0;

            double top20contributionToYTD = 0.0;
            double totalGainLoss = 0.0;
            double totalOpeningBalance = 0.0;
            double totalContributionToYTD = 0.0;
            double totalContributionToVar = 0.0;
            int i = 1;
            for(MonitoringRiskHedgeFundFundAllocationDto record: recordsAll){
                top10Nav = MathUtils.add(18, top10Nav, (i <= 10 ? record.getNav() : 0.0));
                top10GainLoss = MathUtils.add(18, top10GainLoss, (i <= 10 ? gainLossMap.get(record.getFundName()) : 0.0));
                top10OpeningBalance = MathUtils.add(18, top10OpeningBalance, (i <= 10 ? openingBalanceMap.get(record.getFundName()) : 0.0));

                top10contributionToYTD = MathUtils.add(18, top10contributionToYTD, (i <= 10 && record.getContributionToYTD() != null ? record.getContributionToYTD() : 0.0));
                top10contributionToVar = MathUtils.add(18, top10contributionToVar, (i <= 10 && record.getContributionToVAR() != null ? record.getContributionToVAR() : 0.0));

                top20Nav = MathUtils.add(18, top20Nav, (i <= 20 ? record.getNav() : 0.0));
                top20GainLoss = MathUtils.add(18, top20GainLoss,  (i <= 20 ? gainLossMap.get(record.getFundName()): 0.0));
                top20OpeningBalance = MathUtils.add(18, top20OpeningBalance,  (i <= 20 ? openingBalanceMap.get(record.getFundName()) : 0.0));
                top20contributionToYTD = MathUtils.add(18, top20contributionToYTD, (i <= 20 && record.getContributionToYTD() != null ? record.getContributionToYTD() : 0.0));
                top20contributionToVar = MathUtils.add(18, top20contributionToVar, (i <= 20 && record.getContributionToVAR() != null ? record.getContributionToVAR() : 0.0));

                totalGainLoss = MathUtils.add(18, totalGainLoss, gainLossMap.get(record.getFundName()));
                totalOpeningBalance = MathUtils.add(18, totalOpeningBalance, openingBalanceMap.get(record.getFundName()));
                totalContributionToYTD = MathUtils.add(18, totalContributionToYTD, (record.getContributionToYTD() != null ? record.getContributionToYTD(): 0.0));
                totalContributionToVar = MathUtils.add(18, totalContributionToVar, (record.getContributionToVAR() != null ? record.getContributionToVAR(): 0.0));

                i++;
            }
            recordsTop = recordsAll.subList(0, 10);

            // TOP 10
            MonitoringRiskHedgeFundFundAllocationDto recordTop10 =
                    new MonitoringRiskHedgeFundFundAllocationDto("TOP 10", "", top10Nav, MathUtils.divide(18, top10Nav, totalNav),
                    MathUtils.divide(18, top10GainLoss, top10OpeningBalance), null, null, top10contributionToYTD, top10contributionToVar);
            recordsTop.add(recordTop10);

            // TOP 10
            MonitoringRiskHedgeFundFundAllocationDto recordTop20 =
                    new MonitoringRiskHedgeFundFundAllocationDto("TOP 20", "", top20Nav, MathUtils.divide(18, top20Nav, totalNav),
                            MathUtils.divide(18, top20GainLoss, top20OpeningBalance), null, null, top20contributionToYTD, top20contributionToVar);
            recordsTop.add(recordTop20);

            // TOTAL
            MonitoringRiskHedgeFundFundAllocationDto recordTotal =
                    new MonitoringRiskHedgeFundFundAllocationDto("TOTAL", "", null, 1.0,
                            MathUtils.divide(18, totalGainLoss, totalOpeningBalance), null, null, totalContributionToYTD, totalContributionToVar);
            recordsTop.add(recordTotal);
        }

        responseDto.setRecords(recordsTop);
        return responseDto;
    }

    @Override
    public MonitoringRiskHedgeFundReportDto getMonthlyHedgeFundReport(MonitoringRiskReportSearchParamsDto searchParamsDto) {

        MonitoringRiskHedgeFundReportDto reportDto = new MonitoringRiskHedgeFundReportDto();//getDummyMonthlyHedgeFundReport();

        NicPortfolioResultDto nicPortfolioResultDto = this.nicPortfolioService.get();

        // Top fund allocations
        ListResponseDto topFundAllocationUploadedResponse = getAllocationTopPorfolioUploaded(searchParamsDto.getDate());
        if (topFundAllocationUploadedResponse != null) {
            if (!topFundAllocationUploadedResponse.isStatusOK()) {
                reportDto.setTopFundAllocationsError(topFundAllocationUploadedResponse.getErrorMessageEn());
            }
            List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = topFundAllocationUploadedResponse.getRecords();
            reportDto.setTopFundAllocations(topFundAllocations);
        }

        if (topFundAllocationUploadedResponse == null) {
            ListResponseDto topFundAllocationsResponse = getHedgeFundsTopAllocations(searchParamsDto.getDate());
            if (topFundAllocationsResponse != null) {
                reportDto.setTopFundAllocationsWarning(topFundAllocationsResponse.getWarningMessageEn());

                if(!topFundAllocationsResponse.isStatusOK()){
                    reportDto.setTopFundAllocationsError(topFundAllocationsResponse.getErrorMessageEn());
                }
                List<MonitoringRiskHedgeFundFundAllocationDto> topFundAllocations = topFundAllocationsResponse.getRecords();
                reportDto.setTopFundAllocations(topFundAllocations);
            }
            if (topFundAllocationsResponse == null) {
                reportDto.setTopFundAllocationsError(topFundAllocationsResponse.getErrorMessageEn());
            }
        }


        //Sub-strategy allocations
        ListResponseDto subStrategyAllocationsResponse = getAllocationBySubStrategy(searchParamsDto.getDate());
        if (subStrategyAllocationsResponse != null) {
            if (!subStrategyAllocationsResponse.isStatusOK()) {
                reportDto.setSubStrategyAllocationsError(subStrategyAllocationsResponse.getErrorMessageEn());
            }
            List<MonitoringRiskHedgeFundAllocationSubStrategyDto> subStrategyAllocations = subStrategyAllocationsResponse.getRecords();
            reportDto.setSubStrategyAllocations(subStrategyAllocations);
        }
        if (subStrategyAllocationsResponse == null) {
            reportDto.setSubStrategyAllocationsError(subStrategyAllocationsResponse.getErrorMessageEn());
        }

        // Factor Betas
//        ListResponseDto factorBetasResponse = getHedgeFundsFactorBetas(searchParamsDto.getDate(), nicPortfolioResultDto);
//        if(!factorBetasResponse.isStatusOK()){
//            reportDto.setFactorBetasError(factorBetasResponse.getErrorMessageEn());
//        }
//        List<MonitoringRiskHedgeFundBetaFactorDto> factorBetas = factorBetasResponse.getRecords();
//        reportDto.setFactorBetas(factorBetas);


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

            performance12MResponsePortfolioA = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesA, 1, false);
            performance12MResponsePortfolioB = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesB, 2, false);
        }


        String[] benchmarkCodesHFRIFOF = {BenchmarkLookup.HFRIFOF.getCode()};
        String[] benchmarkCodesHFRIAWC = {BenchmarkLookup.HFRIAWC.getCode()};
        List<BenchmarkValueDto> hfrifof12M = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom12M, dateTo, 10, benchmarkCodesHFRIFOF);
        List<BenchmarkValueDto> hfriawc12M = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFrom12M, dateTo, 10, benchmarkCodesHFRIAWC);
        if(hfrifof12M != null && !hfrifof12M.isEmpty()){
            List<DateDoubleValue> valuesHfriFoF = new ArrayList<>();
            for(BenchmarkValueDto dto: hfrifof12M){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                valuesHfriFoF.add(value);
            }

            performance12MResponseHfriFoF = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesHfriFoF,1,  true);
        }
        if (hfriawc12M != null && !hfriawc12M.isEmpty()){
            List<DateDoubleValue> valuesHfriAWC = new ArrayList<>();
            for (BenchmarkValueDto dto : hfriawc12M) {
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                valuesHfriAWC.add(value);
            }
            performance12MResponseHfriAWC = getHedgeFundsPerformanceSummary(dateFrom12M, dateTo, valuesHfriAWC,2,  true);
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

            performanceSIResponsePortfolioA = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesA, 1,  false);
            performanceSIResponsePortfolioB = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesB, 2,  false);
        }

        List<BenchmarkValueDto> hfriFoFSI = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSI, dateTo, 10, benchmarkCodesHFRIFOF);
        List<BenchmarkValueDto> hfriAWCSI = this.benchmarkService.getBenchmarkValuesEndOfMonthForDateRangeAndTypes(dateFromSI, dateTo, 10, benchmarkCodesHFRIAWC);
        if(hfriFoFSI != null && !hfriFoFSI.isEmpty()){
            List<DateDoubleValue> valuesHfriFoF = new ArrayList<>();
            for(BenchmarkValueDto dto: hfriFoFSI){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
                valuesHfriFoF.add(value);
            }

            performanceSIResponseHfriFoF = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesHfriFoF, 1, true);
        }
        if (hfriAWCSI != null && !hfriAWCSI.isEmpty()) {
            List<DateDoubleValue> valuesHfriAWC = new ArrayList<>();
            for (BenchmarkValueDto dto: hfriAWCSI){
                DateDoubleValue value = new DateDoubleValue(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getCalculatedMonthReturn());
            }
            performanceSIResponseHfriAWC = getHedgeFundsPerformanceSummary(dateFromSI, dateTo, valuesHfriAWC, 2, true);
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
//        ListResponseDto marketSensitivityMSCIResponse = getMarketSensitivity(BenchmarkLookup.MSCI_ACWI_IMI.getCode(), dateFromSI, dateTo, nicPortfolioResultDto);
//        if(!marketSensitivityMSCIResponse.isStatusOK()){
//            reportDto.setMarketSensitivityError(marketSensitivityMSCIResponse.getErrorMessageEn());
//        }
//        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesMSCIRecords = marketSensitivityMSCIResponse.getRecords();
//        reportDto.setMarketSensitivitesMSCI(marketSensitivitesMSCIRecords);
//
//        // Barclays Global Agg
//        ListResponseDto marketSensitivityBarclaysGlobalResponse = getMarketSensitivity(BenchmarkLookup.LEGATRUH.getCode(), dateFromSI, dateTo, nicPortfolioResultDto);
//        if(!marketSensitivityBarclaysGlobalResponse.isStatusOK()){
//            reportDto.setMarketSensitivityError(marketSensitivityBarclaysGlobalResponse.getErrorMessageEn());
//        }
//        List<MonitoringRiskHedgeFundMarketSensitivityRecordDto> marketSensitivitesBarclaysRecords = marketSensitivityBarclaysGlobalResponse.getRecords();
//        reportDto.setMarketSensitivitesBarclays(marketSensitivitesBarclaysRecords);

        // VARS
//        ListResponseDto portfolioVarResponse = getHedgeFundPortfolioVars(searchParamsDto.getDate(), PortfolioVarLookup.VAR95.getCode(), PortfolioVarLookup.VAR99.getCode());
//        if(!portfolioVarResponse.isStatusOK()){
//            reportDto.setPortfolioVarsError(portfolioVarResponse.getErrorMessageEn());
//        }
//        List<MonitoringRiskHedgeFundPortfolioVarDto> portfolioVars = portfolioVarResponse.getRecords();
//        reportDto.setPortfolioVars(portfolioVars);

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

    @Override
    public List<Date> getReportDateList(){
        List<Date> dates = new ArrayList<>();
        Iterator<MonitoringRiskHFMonthlyReport> iterator = this.riskReportRepository.findAll().iterator();
        if(iterator != null){
            while(iterator.hasNext()){
                MonitoringRiskHFMonthlyReport report = iterator.next();
                dates.add(DateUtils.getLastDayOfCurrentMonth(report.getReportDate()));
            }
        }
        Collections.sort(dates);
        Collections.reverse(dates);
        return dates;
    }

    @Transactional
    public ResponseDto uploadHFReturns(Long reportId, FilesDto filesDto, String updater){
        ResponseDto responseDto = new ResponseDto();
        // Save file
        if(filesDto == null || filesDto.getType() == null){
            responseDto.setErrorMessageEn("Failed to save return file: file missing or type not specified");
            return responseDto;
        }
        String catalog = filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_A.getCode()) ?
                FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_A.getCatalog() :
                filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_B.getCode()) ?
                        FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_B.getCatalog():
                        filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CONS.getCode()) ?
                                FileTypeLookup.MONITORING_HF_RISK_RETURNS_CONS.getCatalog(): null;
        if(catalog == null){
            responseDto.setErrorMessageEn("Failed to save return file: file type could not be determined");
            return responseDto;
        }
        // Save file
        Long fileId = this.fileService.save(filesDto, catalog);
        MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findOne(reportId);
        if(report == null){
            responseDto.setErrorMessageEn("Failed to save return file: missing report");
            return responseDto;
        }
        try{
            InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
            XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
            //XSSFSheet sheet = workbook.getSheet("Returns");
            Iterator<Row> rowIterator = null;
            try {
                rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), 0);
            }catch (ExcelFileParseException ex){
                String errorMessage = "Error parsing data file." + ex.getMessage();
                logger.error(errorMessage, ex);
                responseDto.setErrorMessageEn(errorMessage);
            }
            if(rowIterator != null) {
                int rowNum = 0;
                boolean headerOk = false;
                List<MonitoringRiskHFPortfolioReturn> returns = new ArrayList<>();
                while (rowIterator.hasNext()) { // each row
                    Row row = rowIterator.next();
                    if(headerOk) {
                        if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null) {
                            String portfolio = ExcelUtils.getStringValueFromCell(row.getCell(0));
                            Date date = row.getCell(1).getDateCellValue();
                            Double value = ExcelUtils.getDoubleValueFromCell(row.getCell(2));

                            MonitoringRiskHFPortfolioReturn returnEntity = new MonitoringRiskHFPortfolioReturn();
                            returnEntity.setReport(report);
                            returnEntity.setDate(date);
                            returnEntity.setValue(value);
                            if(portfolio != null){
                                if(portfolio.equalsIgnoreCase("SINGULAR")){
                                    returnEntity.setPortfolioType(new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_A.getId()));
                                }else  if(portfolio.equalsIgnoreCase("SINGULAR B")){
                                    returnEntity.setPortfolioType(new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_B.getId()));
                                }else  if(portfolio.equalsIgnoreCase("SINGULARCONS")){
                                    returnEntity.setPortfolioType(new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_CONS.getId()));
                                }
                            }
                            if(returnEntity.getDate() == null){
                                responseDto.setErrorMessageEn("Failed to save file: missing date value for one of records");
                                return responseDto;
                            }
                            if(returnEntity.getValue() == null){
                                responseDto.setErrorMessageEn("Failed to save file: missing return value for one of records");
                                return responseDto;
                            }
                            if(returnEntity.getPortfolioType() == null){
                                responseDto.setErrorMessageEn("Failed to save file: missing portfolio type for one of records " +
                                        "(must be SINGULAR, SINGULAR B, SINGULARCONS)");
                                return responseDto;
                            }
                            returns.add(returnEntity);
                        }
                    }else{
                        headerOk = ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0),"Portfolio") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1),"Date") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2),"Rate of Return");
                    }
                }
                if(!returns.isEmpty()){
                    this.riskHFReturnRepository.save(returns);
                    if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_A.getCode())) {
                        report.setReturnsClassAFile(new Files(fileId));
                    }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CLASS_B.getCode())) {
                        report.setReturnsClassBFile(new Files(fileId));
                    }else if(filesDto.getType().equalsIgnoreCase(FileTypeLookup.MONITORING_HF_RISK_RETURNS_CONS.getCode())) {
                        report.setReturnsConsFile(new Files(fileId));
                    }
                    this.riskReportRepository.save(report);
                }
            }

        }catch (Exception ex){
            logger.error("Monitoring Risk HF Monthly: failed to parse HF returns with exception",ex);
            responseDto.setErrorMessageEn("Failed to parse HF returns");
            return responseDto;
        }
        responseDto.setStatus(ResponseStatusType.SUCCESS);
        return responseDto;
    }

    @Transactional
    @Override
    public ResponseDto uploadHFAllocations(Long reportId, FilesDto filesDto, String updater){
        ResponseDto responseDto = new ResponseDto();
        // Save file
        if(filesDto == null || filesDto.getType() == null){
            responseDto.setErrorMessageEn("Failed to save return file: file missing or type not specified");
            return responseDto;
        }
        String catalog = FileTypeLookup.MONITORING_HF_RISK_ALLOCATIONS_CONS.getCatalog();
        if(catalog == null){
            responseDto.setErrorMessageEn("Failed to save allocations file: file type could not be determined");
            return responseDto;
        }
        // Save file
        MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findOne(reportId);
        if(report == null){
            responseDto.setErrorMessageEn("Failed to save return file: missing report");
            return responseDto;
        }
        responseDto = parseHFAllocationsMonth(filesDto, report);// TODO: chek transactions
        if(responseDto.isStatusOK()) {
            responseDto = parseHFAllocationsQuarter(filesDto, report);
            if(responseDto.isStatusOK()) {
                responseDto = parseHFAllocationsYear(filesDto, report);
            }
        }
        if(!responseDto.isStatusOK()){
            // delete from DB
            this.riskHFPortfolioAllocationMonthRepository.deleteByReportId(report.getId());
            this.riskHFPortfolioAllocationQuarterRepository.deleteByReportId(report.getId());
            this.riskHFPortfolioAllocationYearRepository.deleteByReportId(report.getId());
            return responseDto;
        }

        Long fileId = this.fileService.save(filesDto, catalog);
        report.setAllocationsConsFile(new Files(fileId));
        this.riskReportRepository.save(report);

        responseDto.setStatus(ResponseStatusType.SUCCESS);
        return responseDto;
    }

    private ResponseDto parseHFAllocationsMonth(FilesDto filesDto, MonitoringRiskHFMonthlyReport report){
        ResponseDto responseDto = new ResponseDto();
        try{
            Iterator<Row> rowIterator = null;
            try {
                rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), "Month");
            }catch (ExcelFileParseException ex){
                String errorMessage = "Error parsing data file." + ex.getMessage();
                logger.error(errorMessage, ex);
                responseDto.setErrorMessageEn(errorMessage);
            }
            if(rowIterator != null) {
                //int rowNum = 0;
                boolean headerOk = false;
                String[] tableHeader = {"Portfolio","Portfolio Currency","Date","Fund","Strategy","Sub Strategy","Separate Account",
                        "Opening Balance","Allocation","Gain/Loss","Ending Balance","ROR","CTR"};
                List<MonitoringRiskHFAllocationMonth> allocationsMonths = new ArrayList<>();
                while (rowIterator.hasNext()) { // each row
                    Row row = rowIterator.next();
                    if(headerOk) {
                        if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null) {
                            String portfolio = ExcelUtils.getStringValueFromCell(row.getCell(0));
                            Date date = row.getCell(2).getDateCellValue();
                            String fund = ExcelUtils.getStringValueFromCell(row.getCell(3));
                            String strategy = ExcelUtils.getStringValueFromCell(row.getCell(4));
                            String subStrategy = ExcelUtils.getStringValueFromCell(row.getCell(5));

                            Double openingBalance = ExcelUtils.getDoubleValueFromCell(row.getCell(7));
                            Double gainLoss = ExcelUtils.getDoubleValueFromCell(row.getCell(9));
                            Double endingBalance = ExcelUtils.getDoubleValueFromCell(row.getCell(10));
                            Double ror = ExcelUtils.getDoubleValueFromCell(row.getCell(11));
                            Double ctr = ExcelUtils.getDoubleValueFromCell(row.getCell(12));

                            MonitoringRiskHFPortfolioType type = null;
                            if(portfolio != null){
                                if(portfolio.equalsIgnoreCase("SINGULAR")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_A.getId());
                                }else  if(portfolio.equalsIgnoreCase("SINGULAR B")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_B.getId());
                                }else  if(portfolio.equalsIgnoreCase("SINGULARCONS")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_CONS.getId());
                                }
                            }
                            MonitoringRiskHFAllocationMonth allocationMonth =
                                    new MonitoringRiskHFAllocationMonth(report, type, date, fund, strategy, subStrategy,
                                            openingBalance, gainLoss, endingBalance, ror, ctr);
                            allocationsMonths.add(allocationMonth);
                        }
                    }else{
                        headerOk = ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0),"Portfolio") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1),"Portfolio Currency") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2),"Date") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(3),"Fund") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4),"Strategy") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(5),"Sub Strategy") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6),"Separate Account") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7),"Opening Balance") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8),"Allocation") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9),"Gain/Loss") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10),"Ending Balance") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(11),"ROR") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12),"CTR");
                    }
                }
                if(!allocationsMonths.isEmpty()){
                    // TODO: check required fields
                    this.riskHFPortfolioAllocationMonthRepository.deleteByReportId(report.getId());
                    this.riskHFPortfolioAllocationMonthRepository.save(allocationsMonths);
                }else{
                    if(!headerOk) {
                        responseDto.setErrorMessageEn("Error saving 'Month' Allocations - table header check failed. Please check table header. " +
                                "Expected:" + Arrays.toString(tableHeader));
                        return  responseDto;
                    }
                }
            }

        }catch (Exception ex){
            logger.error("Monitoring Risk HF Monthly: failed to parse HF allocations (month) with exception",ex);
            responseDto.setErrorMessageEn("Failed to parse HF allocations (month)");
            return responseDto;
        }
        responseDto.setStatus(ResponseStatusType.SUCCESS);
        return responseDto;
    }

    private ResponseDto parseHFAllocationsQuarter(FilesDto filesDto, MonitoringRiskHFMonthlyReport report){
        ResponseDto responseDto = new ResponseDto();
        try{
            Iterator<Row> rowIterator = null;
            try {
                rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), "Quarter-to-Date");
            }catch (ExcelFileParseException ex){
                String errorMessage = "Error parsing data file." + ex.getMessage();
                logger.error(errorMessage, ex);
                responseDto.setErrorMessageEn(errorMessage);
            }
            if(rowIterator != null) {
                //int rowNum = 0;
                boolean headerOk = false;
                String[] tableHeader = {"Portfolio","Portfolio Currency","Date","Fund","Strategy","Sub Strategy","Separate Account",
                        "Net Activity","ROR","CTR"};
                List<MonitoringRiskHFAllocationQuarter> allocationsQuarter = new ArrayList<>();
                while (rowIterator.hasNext()) { // each row
                    Row row = rowIterator.next();
                    if(headerOk) {
                        if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null) {
                            String portfolio = ExcelUtils.getStringValueFromCell(row.getCell(0));
                            Date date = row.getCell(2).getDateCellValue();
                            String fund = ExcelUtils.getStringValueFromCell(row.getCell(3));
                            String strategy = ExcelUtils.getStringValueFromCell(row.getCell(4));
                            String subStrategy = ExcelUtils.getStringValueFromCell(row.getCell(5));

                            Double ror = ExcelUtils.getDoubleValueFromCell(row.getCell(8));
                            Double ctr = ExcelUtils.getDoubleValueFromCell(row.getCell(9));

                            MonitoringRiskHFPortfolioType type = null;
                            if(portfolio != null){
                                if(portfolio.equalsIgnoreCase("SINGULAR")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_A.getId());
                                }else  if(portfolio.equalsIgnoreCase("SINGULAR B")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_B.getId());
                                }else  if(portfolio.equalsIgnoreCase("SINGULARCONS")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_CONS.getId());
                                }
                            }
                            MonitoringRiskHFAllocationQuarter allocationQuarter =
                                    new MonitoringRiskHFAllocationQuarter(report, type, date, fund, strategy, subStrategy, ror, ctr);
                            allocationsQuarter.add(allocationQuarter);
                        }
                    }else{
                        headerOk = ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0),"Portfolio") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1),"Portfolio Currency") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2),"Date") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(3),"Fund") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4),"Strategy") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(5),"Sub Strategy") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6),"Separate Account") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7),"Net Activity") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8),"ROR") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9),"CTR");
                    }
                }
                if(!allocationsQuarter.isEmpty()){
                    this.riskHFPortfolioAllocationQuarterRepository.deleteByReportId(report.getId());
                    this.riskHFPortfolioAllocationQuarterRepository.save(allocationsQuarter);
                }else{
                    if(!headerOk) {
                        responseDto.setErrorMessageEn("Error saving 'Quarter' Allocations - table header check failed. Please check table header. " +
                                "Expected:" + Arrays.toString(tableHeader));
                        return  responseDto;
                    }
                }
            }

        }catch (Exception ex){
            logger.error("Monitoring Risk HF Monthly: failed to parse HF allocations (quarter) with exception",ex);
            responseDto.setErrorMessageEn("Failed to parse HF allocations (quarter)");
            return responseDto;
        }
        responseDto.setStatus(ResponseStatusType.SUCCESS);
        return responseDto;
    }

    private ResponseDto parseHFAllocationsYear(FilesDto filesDto, MonitoringRiskHFMonthlyReport report){
        ResponseDto responseDto = new ResponseDto();
        try{
            Iterator<Row> rowIterator = null;
            try {
                rowIterator = ExcelUtils.getRowIterator(filesDto.getBytes(), filesDto.getFileName(), "Year-to-Date");
            }catch (ExcelFileParseException ex){
                String errorMessage = "Error parsing data file." + ex.getMessage();
                logger.error(errorMessage, ex);
                responseDto.setErrorMessageEn(errorMessage);
            }
            if(rowIterator != null) {
                //int rowNum = 0;
                boolean headerOk = false;
                String[] tableHeader = {"Portfolio","Portfolio Currency","Date","Fund","Strategy","Sub Strategy","Separate Account",
                        "Net Activity","ROR","CTR", "Partial Period Date", "Contrib to VaR"};
                List<MonitoringRiskHFAllocationYear> allocationsYear = new ArrayList<>();
                while (rowIterator.hasNext()) { // each row
                    Row row = rowIterator.next();
                    if(headerOk) {
                        if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null) {
                            String portfolio = ExcelUtils.getStringValueFromCell(row.getCell(0));
                            Date date = row.getCell(2).getDateCellValue();
                            String fund = ExcelUtils.getStringValueFromCell(row.getCell(3));
                            String strategy = ExcelUtils.getStringValueFromCell(row.getCell(4));
                            String subStrategy = ExcelUtils.getStringValueFromCell(row.getCell(5));

                            Double ror = ExcelUtils.getDoubleValueFromCell(row.getCell(8));
                            Double ctr = ExcelUtils.getDoubleValueFromCell(row.getCell(9));
                            Double contribToVar = ExcelUtils.getDoubleValueFromCell(row.getCell(11));

                            MonitoringRiskHFPortfolioType type = null;
                            if(portfolio != null){
                                if(portfolio.equalsIgnoreCase("SINGULAR")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_A.getId());
                                }else  if(portfolio.equalsIgnoreCase("SINGULAR B")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_B.getId());
                                }else  if(portfolio.equalsIgnoreCase("SINGULARCONS")){
                                    type = new MonitoringRiskHFPortfolioType(MonitoringRiskHFPortfolioTypeLookup.SINGULAR_CONS.getId());
                                }
                            }
                            MonitoringRiskHFAllocationYear allocationYear =
                                    new MonitoringRiskHFAllocationYear(report, type, date, fund, strategy, subStrategy, ror, ctr, contribToVar);
                            allocationsYear.add(allocationYear);
                        }
                    }else{
                        headerOk = ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0),"Portfolio") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1),"Portfolio Currency") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2),"Date") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(3),"Fund") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4),"Strategy") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(5),"Sub Strategy") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6),"Separate Account") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7),"Net Activity") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8),"ROR") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9),"CTR") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10),"Partial Period Date") &&
                                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(11),"Contrib to VaR");
                    }
                }
                if(!allocationsYear.isEmpty()){
                    this.riskHFPortfolioAllocationYearRepository.deleteByReportId(report.getId());
                    this.riskHFPortfolioAllocationYearRepository.save(allocationsYear);
                }else{
                    if(!headerOk) {
                        responseDto.setErrorMessageEn("Error saving 'Year' Allocations - table header check failed. Please check table header. " +
                                "Expected:" + Arrays.toString(tableHeader));
                        return  responseDto;
                    }
                }
            }

        }catch (Exception ex){
            logger.error("Monitoring Risk HF Monthly: failed to parse HF allocations (year) with exception",ex);
            responseDto.setErrorMessageEn("Failed to parse HF allocations (year)");
            return responseDto;
        }
        responseDto.setStatus(ResponseStatusType.SUCCESS);
        return responseDto;
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
                                portfolioDto.setPortfolioBValue(portfolioBDto.getPortfolioBValue());
                                portfolioDto.setPortfolioBValueTxt(portfolioBDto.getPortfolioBValueTxt());
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
                if (mergedBenchmarkResponse.getRecords() == null || mergedBenchmarkResponse.getRecords().isEmpty()) {
                    mergedBenchmarkResponse.setRecords(hfriAwcResponse.getRecords());
                } else {
                    //Merge benchmark values
                    for (MonitoringRiskHedgeFundPerformanceRecordDto benchmarkDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) mergedBenchmarkResponse.getRecords()) {
                        for (MonitoringRiskHedgeFundPerformanceRecordDto hfriAwcDto: (List<MonitoringRiskHedgeFundPerformanceRecordDto>) hfriAwcResponse.getRecords()) {
                            if (benchmarkDto.getName().equalsIgnoreCase(hfriAwcDto.getName())) {
                                //benchmarkDto.setBenchmarkValue(hfriAwcDto.getBenchmarkValue());
                                benchmarkDto.setBenchmarkAwcValue(hfriAwcDto.getBenchmarkAwcValue());
                                //benchmarkDto.setBenchmarkValueTxt(hfriAwcDto.getBenchmarkValueTxt());
                                benchmarkDto.setBenchmarkAwcValueTxt(hfriAwcDto.getBenchmarkAwcValueTxt());
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
                                portfolioDto.setBenchmarkAwcValue(benchmarkDto.getBenchmarkAwcValue());
                                portfolioDto.setBenchmarkAwcValueTxt(benchmarkDto.getBenchmarkAwcValueTxt());
                                break;
                            }
                        }
                    }
                }
            }
            return mergedResponse;
        }
    }

    private ListResponseDto getHedgeFundPortfolioVars(Date date, String portfolioVarCode){
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
        if(responseDto.getStatus() == null){
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return responseDto;
    }
    /* MARKET SENSITIVITY *********************************************************************************************/
    private ListResponseDto getMarketSensitivity(String benchmarkCode, Date dateFrom, Date dateTo, List<DateDoubleValue> valuesCons){
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
            List<DateDoubleValue> periodReturns = new ArrayList<>();
            if(valuesCons != null) {
                for (DateDoubleValue value : valuesCons) {
                    Date portfolioReturnDateEndMonth = DateUtils.getLastDayOfCurrentMonth(value.getDate());
                    if (portfolioReturnDateEndMonth.compareTo(dateFrom) >= 0 && portfolioReturnDateEndMonth.compareTo(dateTo) <= 0) {
                        periodReturns.add(value);
                    }
                }
            }
            if(periodReturns.size() != monthDiff){
                String errorMessage = "Failed to calculate market sensitivity: missing consolidated returns for period [" +
                        DateUtils.getDateFormatted(dateFrom) + ", " + DateUtils.getDateFormatted(dateTo) + "], expected " + monthDiff + ", found " + periodReturns.size() + ". ";
                logger.error(errorMessage);
                responseDto.appendErrorMessageEn(errorMessage);
                return responseDto;
            }

            Double[] totalPortfolio = new Double[monthDiff];
            Map<Date, DateDoubleValue> portfolioMap = new HashMap<>();
            int i = 0;
            for(DateDoubleValue value: periodReturns){
                if(value.getDate() == null || value.getValue() == null){
                    String errorMessage = "Failed to calculate market sensitivity: missing consolidated return for date '" + DateUtils.getDateFormatted(value.getDate()) + "'. ";
                    logger.error(errorMessage);
                    responseDto.appendErrorMessageEn(errorMessage);
                    return responseDto;
                }
                portfolioMap.put(DateUtils.getLastDayOfCurrentMonth(value.getDate()), value);
                totalPortfolio[i] = value.getValue();
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
                if(benchmark.getCalculatedMonthReturn() == null){
                    String errorMessage = "Failed to calculate market sensitivity: missing benchmark (" + benchmarkCode + ") return for date '" + DateUtils.getDateFormatted(benchmark.getDate()) + "'. ";
                    logger.error(errorMessage);
                    responseDto.appendErrorMessageEn(errorMessage);
                    return responseDto;
                }
                Date benchmarkDate = DateUtils.getLastDayOfCurrentMonth(benchmark.getDate());
                if(benchmark.getCalculatedMonthReturn().doubleValue() >= 0){
                    positiveBenchmark.add(benchmark.getCalculatedMonthReturn());
                    if(portfolioMap.get(benchmarkDate) != null) {
                        positivePortfolio.add(portfolioMap.get(benchmarkDate).getValue());
                    }else{
                        String errorMessage = "Failed to calculate market sensitivity: missing consolidated return for date '" + DateUtils.getDateFormatted(benchmarkDate) + "'. ";
                        logger.error(errorMessage);
                        responseDto.appendErrorMessageEn(errorMessage);
                        return responseDto;
                    }
                }else{
                    negativeBenchmark.add(benchmark.getCalculatedMonthReturn());
                    if(portfolioMap.get(benchmarkDate) != null) {
                        negativePortfolio.add(portfolioMap.get(benchmarkDate).getValue());
                    }else{
                        String errorMessage = "Failed to calculate market sensitivity: missing consolidated return for date '" + DateUtils.getDateFormatted(benchmarkDate) + "'. ";
                        logger.error(errorMessage);
                        responseDto.appendErrorMessageEn(errorMessage);
                        return responseDto;
                    }
                }
                totalBenchmarks[i] = benchmark.getCalculatedMonthReturn();
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
//                    if (topPortfolioRepository.findByFundName(fundName).getQtd() != null) {
//                        value.setQtd(topPortfolioEntityConverter.disassemble(topPortfolioRepository.findByFundName(fundName)).getQtd());
//                    } else {
//
//                    }
                    Double qtd = calculatePeriodToDate(18, fundMap, qtdDate, date);
                    value.setQtd(qtd);
//                    if (topPortfolioRepository.findByFundName(fundName).getYtd() != null) {
//                        value.setYtd(topPortfolioEntityConverter.disassemble(topPortfolioRepository.findByFundName(fundName)).getYtd());
//                    } else {
//
//                    }
                    Double ytd = calculatePeriodToDate(18, fundMap, ytdDate, date);
                    value.setYtd(ytd);
//                    if (topPortfolioRepository.findByFundName(fundName).getContributionToYTD() != null) {
//                        value.setContributionToYTD(topPortfolioEntityConverter.disassemble(topPortfolioRepository.findByFundName(fundName)).getContributionToYTD());
//                    }
//                    if (topPortfolioRepository.findByFundName(fundName).getContributionToVAR() != null) {
//                        value.setContributionToVAR(topPortfolioEntityConverter.disassemble(topPortfolioRepository.findByFundName(fundName)).getContributionToVAR());
//                    }
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

    private ListResponseDto getAllocationTopPorfolioUploaded(Date date) {
        ListResponseDto responseDto = new ListResponseDto();
        try {
            List<MonitoringRiskHedgeFundFundAllocationDto> allocationTopPortfolioDtos = this.topPortfolioEntityConverter.
                    disassembleList(this.topPortfolioRepository.findAllocationsByDate(date));
            if (!allocationTopPortfolioDtos.isEmpty()) {
                responseDto.setRecords(allocationTopPortfolioDtos);
                responseDto.setStatus(ResponseStatusType.SUCCESS);
            } else {
                responseDto.appendErrorMessageEn("No uploaded data for given date");
                responseDto.setStatus(ResponseStatusType.FAIL);
            }
        } catch (Exception ex) {
            logger.error("Error finding Top Portfolios");
        }
        return responseDto;
    }

    private ListResponseDto getAllocationBySubStrategy(Date date) {
        ListResponseDto responseDto = new ListResponseDto();
        try {
            List<MonitoringRiskHedgeFundAllocationSubStrategyDto> allocationSubStrategyDtos =
                    this.subStrategyEntityConverter.disassembleList(this.subStrategyRepository.findByFirstDate(date));
            if (!allocationSubStrategyDtos.isEmpty()) {
                responseDto.setRecords(allocationSubStrategyDtos);
                responseDto.setStatus(ResponseStatusType.SUCCESS);
            } else {
                responseDto.appendErrorMessageEn("No uploaded data for given date");
                responseDto.setStatus(ResponseStatusType.FAIL);
            }
        } catch (Exception ex) {
            logger.error("Error finding Sub-strategies");
        }
        return responseDto;
    }

    private ListResponseDto getAllocationBySubStrategy(Long reportId, Long previousReportId) {
        ListResponseDto responseDto = new ListResponseDto();
        try {
            MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findOne(reportId);
            MonitoringRiskHFMonthlyReport prevReport = previousReportId != null ?
                    this.riskReportRepository.findOne(previousReportId) : null;
            if(report == null){
                responseDto.setErrorMessageEn("Failed to calculate allocation by strategy: report id not found (" +
                        (reportId != null ? reportId.longValue() : null) + ")");
                return responseDto;
            }
            List<MonitoringRiskHFAllocationMonth> allocations = this.riskHFPortfolioAllocationMonthRepository.findByReportId(reportId);
            if (!allocations.isEmpty()) {
                List<MonitoringRiskHFAllocationMonth> prevAllocations = prevReport != null ?
                        this.riskHFPortfolioAllocationMonthRepository.findByReportId(prevReport.getId()) : new ArrayList<>();
                Map<String, Double> prevStrategyMap = new HashMap<>();
                Double prevTotalNav = 0.0;
                if(prevAllocations != null){
                    for(MonitoringRiskHFAllocationMonth prevAllocation: prevAllocations){
                        if(!prevAllocation.getFund().equalsIgnoreCase("N/A") &&
                                prevAllocation.getStrategy() != null && prevAllocation.getEndingBalance() != null){
                            Double value = prevStrategyMap.get(prevAllocation.getSubStrategy());
                            prevStrategyMap.put(prevAllocation.getSubStrategy(), MathUtils.add(18, (value != null ? value : 0.0), prevAllocation.getEndingBalance()));
                        }
                        prevTotalNav = MathUtils.add(prevTotalNav, prevAllocation.getEndingBalance());
                    }
                }
                List<MonitoringRiskHedgeFundAllocationSubStrategyDto> records = new ArrayList<>();
                Map<String, Double> strategyMap = new HashMap<>();
                Double totalNav = 0.0;
                for(MonitoringRiskHFAllocationMonth allocation: allocations){
                    if(!allocation.getFund().equalsIgnoreCase("N/A") &&
                            allocation.getStrategy() != null && allocation.getEndingBalance() != null){
                        Double value = strategyMap.get(allocation.getSubStrategy());
                        strategyMap.put(allocation.getSubStrategy(), MathUtils.add(18, (value != null ? value : 0.0), allocation.getEndingBalance()));
                    }
                    totalNav = MathUtils.add(totalNav, allocation.getEndingBalance());
                }
                for(String substrategy: strategyMap.keySet()){
                    MonitoringRiskHedgeFundAllocationSubStrategyDto record = new MonitoringRiskHedgeFundAllocationSubStrategyDto();
                    record.setSubStrategyName(substrategy);
                    record.setCurrentDate(report.getReportDate());
                    record.setCurrentValue(MathUtils.divide(strategyMap.get(substrategy), totalNav));
                    if(prevReport != null && prevReport.getReportDate() != null) {
                        record.setPreviousDate(prevReport.getReportDate());
                    }
                    if(prevStrategyMap.get(substrategy) != null && prevTotalNav != 0.0) {
                            record.setPreviousValue(MathUtils.divide(prevStrategyMap.get(substrategy), prevTotalNav));
                    }

                    records.add(record);
                }
                responseDto.setRecords(records);
                responseDto.setStatus(ResponseStatusType.SUCCESS);
            } else {
                responseDto.appendErrorMessageEn("No uploaded data for given date");
                responseDto.setStatus(ResponseStatusType.FAIL);
            }
        } catch (Exception ex) {
            logger.error("Error finding strategy data");
        }
        return responseDto;
    }

    /* HEDGE FUNDS FACTOR BETAS ***************************************************************************************/
    private ListResponseDto getHedgeFundsFactorBetas(Date date, List<DateDoubleValue> valuesCons){
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundBetaFactorDto> records = new ArrayList<>();

        // Dates
        Date dateTo = DateUtils.getLastDayOfCurrentMonth(date);
        Date dateFrom12M = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(date, -11));
        Date dateFromSinceInception = SINGULAR_PORTFOLIO_START_DATE;
        // Benchmarks map
        Map<String, List<BenchmarkValueDto>> benchmarksMap = getBenchmarksMap(dateFromSinceInception, dateFrom12M, dateTo);

        //HFRIAWC
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "HFRI AWC (B class)", BenchmarkLookup.HFRIAWC.getCode(), benchmarksMap.get(BenchmarkLookup.HFRIAWC.getCode()), benchmarksMap.get(BenchmarkLookup.HFRIAWC.getCode() + "_SI"), valuesCons);
        // HFRIFOF 12M
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "HFRI FoF (A class)", BenchmarkLookup.HFRIFOF.getCode(), benchmarksMap.get(BenchmarkLookup.HFRIFOF.getCode()), benchmarksMap.get(BenchmarkLookup.HFRIFOF.getCode() + "_SI"), valuesCons);
        // MSCI ACWI IMI
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "MSCI ACWI IMI", BenchmarkLookup.MSCI_ACWI_IMI.getCode(), benchmarksMap.get(BenchmarkLookup.MSCI_ACWI_IMI.getCode()), benchmarksMap.get(BenchmarkLookup.MSCI_ACWI_IMI.getCode() + "_SI"), valuesCons);
        // MSCI World
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "MSCI World", BenchmarkLookup.MSCI_WORLD.getCode(), benchmarksMap.get(BenchmarkLookup.MSCI_WORLD.getCode()), benchmarksMap.get(BenchmarkLookup.MSCI_WORLD.getCode() + "_SI"), valuesCons);
        // S&P 12M
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "S&P 500", BenchmarkLookup.SNP_500_SPX.getCode(), benchmarksMap.get(BenchmarkLookup.SNP_500_SPX.getCode()), benchmarksMap.get(BenchmarkLookup.SNP_500_SPX.getCode() + "_SI"), valuesCons);
        // MSCI EM
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "MSCI EM", BenchmarkLookup.MSCI_EM.getCode(), benchmarksMap.get(BenchmarkLookup.MSCI_EM.getCode()), benchmarksMap.get(BenchmarkLookup.MSCI_EM.getCode() + "_SI"), valuesCons);
        // Global FI
//        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
//                "Barclays Global Agg Index Hedged", BenchmarkLookup.GLOBAL_FI.getCode(), benchmarksMap.get(BenchmarkLookup.GLOBAL_FI.getCode()), benchmarksMap.get(BenchmarkLookup.GLOBAL_FI.getCode() + "_SI"), valuesCons);
        // Global FI Unhedged
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "Barclays Global Agg Index Unhedged", BenchmarkLookup.LEGATRUU.getCode(), benchmarksMap.get(BenchmarkLookup.LEGATRUU.getCode()), benchmarksMap.get(BenchmarkLookup.LEGATRUU.getCode() + "_SI"), valuesCons);
        // Barclays Global Agg Index USD Hedged
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "Barclays Global Agg Index USD Hedged", BenchmarkLookup.LEGATRUH.getCode(), benchmarksMap.get(BenchmarkLookup.LEGATRUH.getCode()), benchmarksMap.get(BenchmarkLookup.LEGATRUH.getCode() + "_SI"), valuesCons);
        // US IG Credit
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "US IG Credit", BenchmarkLookup.US_IG_CREDIT.getCode(), benchmarksMap.get(BenchmarkLookup.US_IG_CREDIT.getCode()), benchmarksMap.get(BenchmarkLookup.US_IG_CREDIT.getCode() + "_SI"), valuesCons);
        // US High Yields
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "US High Yield", BenchmarkLookup.US_HIGH_YIELDS.getCode(), benchmarksMap.get(BenchmarkLookup.US_HIGH_YIELDS.getCode()), benchmarksMap.get(BenchmarkLookup.US_HIGH_YIELDS.getCode() + "_SI"), valuesCons);
        // EM Debt
        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
                "EM Debt", BenchmarkLookup.EM_DEBT.getCode(), benchmarksMap.get(BenchmarkLookup.EM_DEBT.getCode()), benchmarksMap.get(BenchmarkLookup.EM_DEBT.getCode() + "_SI"), valuesCons);

//        // OIL
//        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
//                "Oil", BenchmarkLookup.OIL.getCode(), benchmarksMap.get(BenchmarkLookup.OIL.getCode()), benchmarksMap.get(BenchmarkLookup.OIL.getCode() + "_SI"), nicPortfolioResultDto);
//
//        // Dollar
//        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
//                "Dollar", BenchmarkLookup.DOLLAR.getCode(), benchmarksMap.get(BenchmarkLookup.DOLLAR.getCode()), benchmarksMap.get(BenchmarkLookup.DOLLAR.getCode() + "_SI"), nicPortfolioResultDto);
//
//        // Gold
//        setFactorBeta(responseDto, records, dateFromSinceInception, dateFrom12M, dateTo, benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode()), benchmarksMap.get(BenchmarkLookup.T_BILLS.getCode() + "_SI"),
//                "Gold", BenchmarkLookup.GOLD.getCode(), benchmarksMap.get(BenchmarkLookup.GOLD.getCode()), benchmarksMap.get(BenchmarkLookup.GOLD.getCode() + "_SI"), nicPortfolioResultDto);


        responseDto.setRecords(records);
        if(responseDto.getStatus() == null){
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return  responseDto;
    }

    private Double getHedgeFundsFactorBeta(Date dateFrom, Date dateTo, String benchmarkName, List<BenchmarkValueDto> tbills,
                                           List<BenchmarkValueDto> benchmarks, List<DateDoubleValue> valuesCons){
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
        if (valuesCons == null || valuesCons.isEmpty()) {
            String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing Singularity Consolidated returns for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]. ";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }else{
            List<DateDoubleValue> records = new ArrayList<>();
            for(DateDoubleValue dateDoubleValue: valuesCons){
                Date dateLastOfMonth = DateUtils.getLastDayOfCurrentMonth(dateDoubleValue.getDate());
                if(dateDoubleValue.getValue() !=null &&
                        dateLastOfMonth.compareTo(dateFrom) >= 0 && dateLastOfMonth.compareTo(dateTo) <= 0){
                    records.add(dateDoubleValue);
                }
            }
            if(records.size() != monthDiff){
                String errorMessage = "Failed to calculate Factor Betas (" + benchmarkName + "): missing Singularity returns for period [" + DateUtils.getDateFormatted(dateFrom) +
                        ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + monthDiff + ", found " + records.size() + ". ";
                logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            double[][] data = new double[monthDiff][];
            Collections.reverse(records);
            for(int i = 0; i < monthDiff; i++){
                data[i] = new double[2];
                data[i][0] = MathUtils.subtract(18, benchmarks.get(i).getCalculatedMonthReturn(), tbills.get(i).getCalculatedMonthReturn());
                data[i][1] = MathUtils.subtract(18, records.get(i).getValue(), tbills.get(i).getCalculatedMonthReturn());
                data[i][0] = MathUtils.subtract(18, benchmarks.get(i).getCalculatedMonthReturn(), tbills.get(i).getCalculatedMonthReturn());
                data[i][1] = MathUtils.subtract(18, records.get(i).getValue(), tbills.get(i).getCalculatedMonthReturn());
            }
            double value = MathUtils.calculateSlope(data, false);
            return MathUtils.add(0.0, value);
        }
    }

    private void setFactorBeta(ListResponseDto responseDto, List<MonitoringRiskHedgeFundBetaFactorDto> records,
                               Date dateFromSinceInception, Date dateFrom12M, Date dateTo,
                               List<BenchmarkValueDto> tbills,  List<BenchmarkValueDto> tbillsSI,
                               String factorName, String factorCode, List<BenchmarkValueDto> factor,  List<BenchmarkValueDto> factorSI,
                               List<DateDoubleValue> valuesCons){
        MonitoringRiskHedgeFundBetaFactorDto factorHFRI = new MonitoringRiskHedgeFundBetaFactorDto(factorName, null, null);
        try{
            Double value12M = getHedgeFundsFactorBeta(dateFrom12M, dateTo, factorCode, tbills, factor, valuesCons);
            factorHFRI.setValue12M(value12M);

            Double valueSinceInception = getHedgeFundsFactorBeta(dateFromSinceInception, dateTo, factorCode, tbillsSI, factorSI, valuesCons);
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
    private ListResponseDto getHedgeFundsPerformanceSummary(Date dateFrom, Date dateTo, List<DateDoubleValue> returnValues,
                                                            int tranche, boolean isBenchmark){
        int period = DateUtils.getMonthsChanged(dateFrom, dateTo);
        ListResponseDto responseDto = new ListResponseDto();
        List<MonitoringRiskHedgeFundPerformanceRecordDto> performance = new ArrayList<>();
        if(returnValues != null && !returnValues.isEmpty()){
            List<DateDoubleValue> recordsForPeriod = new ArrayList<>();
            List<DateDoubleValue> recordsSinceInception =  new ArrayList<>();
            for(DateDoubleValue value: returnValues){
                if(value.getValue() != null){
                    if(value.getDate().compareTo(dateFrom) >= 0 && value.getDate().compareTo(dateTo) <= 0){
                        recordsForPeriod.add(value);
                    }
                    recordsSinceInception.add(value);
                }
            }

            if(recordsForPeriod.size() != period){
                String errorMessage = "Missing " + (isBenchmark ? "HFRI returns " : "HF " +
                        (tranche == 1? " (class A) " : " (class B) ") + "returns") + " for period [" + DateUtils.getDateFormatted(dateFrom) +
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
                if(tbills.get(i).getCalculatedMonthReturn() == null){
                    String errorMessage = "Missing T-bills return values for date " +
                            DateUtils.getDateFormatted(tbills.get(i).getDate());
                    logger.error(errorMessage);
                    return null;
                }
                tbillsReturns[i] = tbills.get(i).getCalculatedMonthReturn();
            }
            // AnRoR
            Double annRoR = MathUtils.getAnnualizedReturn(returns, 18);
            Double annRoR1 = isBenchmark || tranche == 2 ? null : annRoR;
            Double annRoR2 = !isBenchmark || tranche == 2 ? null : annRoR;
            Double annRoR3 = isBenchmark || tranche == 1 ? null : annRoR;
            Double annRoR4 = !isBenchmark || tranche == 1 ? null : annRoR;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Annualized Return", annRoR1, annRoR2, annRoR3, annRoR4,
                    annRoR1 != null ? (MathUtils.multiply(4, annRoR1, 100.0) + "%") : null,
                    annRoR2 != null ? (MathUtils.multiply(4, annRoR2, 100.0) + "%") : null,
                    annRoR3 != null ? (MathUtils.multiply(4, annRoR3, 100.0) + "%") : null,
                    annRoR4 != null ? (MathUtils.multiply(4, annRoR4, 100.0) + "%") : null));

            // STD
            Double std = MathUtils.getStandardDeviation(returns, true);
            if(std != null) {
                Double annStd = MathUtils.multiply(18, std, Math.sqrt(12));
                Double annStd1 = isBenchmark || tranche == 2 ? null : annStd;
                Double annStd2 = !isBenchmark || tranche == 2 ? null : annStd;
                Double annStd3 = isBenchmark || tranche == 1 ? null : annStd;
                Double annStd4 = !isBenchmark || tranche == 1? null : annStd;
                performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Ann Standard Deviation", annStd1, annStd2,annStd3, annStd4,
                        annStd1 != null ? (MathUtils.multiply(4, annStd1, 100.0) + "%") : null,
                        annStd2 != null ? (MathUtils.multiply(4, annStd2, 100.0) + "%") : null,
                        annStd3 != null ? (MathUtils.multiply(4, annStd3, 100.0) + "%") : null,
                        annStd4 != null ? (MathUtils.multiply(4, annStd4, 100.0) + "%") : null));
            }

            // Downside Deviation
            Double downsideDeviation = MathUtils.getAnnualizedDownsideDeviation(18, returns);
            Double downsideDeviation1 = isBenchmark || tranche == 2 ? null : downsideDeviation;
            Double downsideDeviation2 = !isBenchmark || tranche == 2 ? null : downsideDeviation;
            Double downsideDeviation3 = isBenchmark || tranche == 1 ? null : downsideDeviation;
            Double downsideDeviation4 = !isBenchmark || tranche == 1 ? null : downsideDeviation;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Downside Deviation", downsideDeviation1, downsideDeviation2,downsideDeviation3, downsideDeviation4,
                    downsideDeviation1 != null ? (MathUtils.multiply(4, downsideDeviation1, 100.0) + "%") : null,
                    downsideDeviation2 != null ? (MathUtils.multiply(4, downsideDeviation2, 100.0) + "%") : null,
                    downsideDeviation3 != null ? (MathUtils.multiply(4, downsideDeviation3, 100.0) + "%") : null,
                    downsideDeviation4 != null ? (MathUtils.multiply(4, downsideDeviation4, 100.0) + "%") : null));

            // Sharpe Ratio
            Double sharpeRatio = MathUtils.getSharpeRatioAvg12MReturns(18, returns, tbillsReturns, true);
            Double sharpeRatio1 = isBenchmark || tranche == 2 ? null : sharpeRatio;
            Double sharpeRatio2 = !isBenchmark || tranche == 2 ? null : sharpeRatio;
            Double sharpeRatio3 = isBenchmark || tranche == 1 ? null : sharpeRatio;
            Double sharpeRatio4 = !isBenchmark || tranche == 1 ? null : sharpeRatio;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Sharpe Ratio", sharpeRatio1, sharpeRatio2,sharpeRatio3, sharpeRatio4,
                    sharpeRatio1 != null ? MathUtils.add(sharpeRatio1, 0.0).toString() : null,
                    sharpeRatio2 != null ? MathUtils.add(sharpeRatio2, 0.0).toString() : null,
                    sharpeRatio3 != null ? MathUtils.add(sharpeRatio3, 0.0).toString() : null,
                    sharpeRatio4 != null ? MathUtils.add(sharpeRatio4, 0.0).toString() : null));

            // Sortino
            Double annRoRTbills = MathUtils.getAnnualizedReturn(tbillsReturns, 18);
            Double sortino = MathUtils.getSortinoRatioAvgReturns(annRoRTbills, returns, 18);
            Double sortino1 = isBenchmark || tranche == 2 ? null : sortino;
            Double sortino2 = !isBenchmark || tranche == 2 ? null : sortino;
            Double sortino3 = isBenchmark || tranche == 1 ? null : sortino;
            Double sortino4 = !isBenchmark || tranche == 1 ? null : sortino;
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Sortino Ratio", sortino1, sortino2,sortino3, sortino4,
                    sortino1 != null ? MathUtils.add(sortino1, 0.0).toString() : null,
                    sortino2 != null ? MathUtils.add(sortino2, 0.0).toString() : null,
                    sortino3 != null ? MathUtils.add(sortino3, 0.0).toString() : null,
                    sortino4 != null ? MathUtils.add(sortino4, 0.0).toString() : null));

            if(returns.length > 12) { // SINCE INCEPTION
                // Worst Drawdown
                // reverse returns
                double[] cumulativeReturns = MathUtils.getCumulativeReturnsFromInitial(18, returns, 1.0);
                if (cumulativeReturns.length != period) {
                    String errorMessage = "Missing calculated value added monthly return for period [" + DateUtils.getDateFormatted(dateFrom) +
                            ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found " + cumulativeReturns.length + ". ";
                    logger.error(errorMessage);
                    responseDto.appendErrorMessageEn(errorMessage);
                    return responseDto;
                }
                double[] arrPrepended = new double[cumulativeReturns.length + 1];
                for (int i = 0; i < arrPrepended.length; i++) {
                    if (i == 0) {
                        arrPrepended[i] = 1.0;
                    } else {
                        arrPrepended[i] = cumulativeReturns[i - 1];
                    }
                }
                cumulativeReturns = arrPrepended;

                WorstDrawdownDto worstDDDto = MathUtils.getWorstDrawdown(18, cumulativeReturns);
                WorstDrawdownDto worstDDDto1 = isBenchmark || tranche == 2 ? null : worstDDDto;
                WorstDrawdownDto worstDDDto2 = !isBenchmark || tranche == 2 ? null : worstDDDto;
                WorstDrawdownDto worstDDDto3 = isBenchmark || tranche == 1 ? null : worstDDDto;
                WorstDrawdownDto worstDDDto4 = !isBenchmark || tranche == 1 ? null : worstDDDto;
                performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Worst DD",
                        worstDDDto1 != null && worstDDDto1.getWorstDDValue() != null ? worstDDDto1.getWorstDDValue() : null,
                        worstDDDto2 != null && worstDDDto2.getWorstDDValue() != null ? worstDDDto2.getWorstDDValue() : null,
                        worstDDDto3 != null && worstDDDto3.getWorstDDValue() != null ? worstDDDto3.getWorstDDValue() : null,
                        worstDDDto4 != null && worstDDDto4.getWorstDDValue() != null ? worstDDDto4.getWorstDDValue() : null,
                        worstDDDto1 != null && worstDDDto1.getWorstDDValue() != null ? (MathUtils.multiply(4, worstDDDto1.getWorstDDValue(), 100.0) + "%") : null,
                        worstDDDto2 != null && worstDDDto2.getWorstDDValue() != null ? (MathUtils.multiply(4, worstDDDto2.getWorstDDValue(), 100.0) + "%") : null,
                        worstDDDto3 != null && worstDDDto3.getWorstDDValue() != null ? (MathUtils.multiply(4, worstDDDto3.getWorstDDValue(), 100.0) + "%") : null,
                        worstDDDto4 != null && worstDDDto4.getWorstDDValue() != null ? (MathUtils.multiply(4, worstDDDto4.getWorstDDValue(), 100.0) + "%") : null));

                // Worst DD Period
                performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Worst DD Duration (months)",
                        worstDDDto1 != null && worstDDDto1.getWorstDDPeriod() != null ? MathUtils.add(worstDDDto1.getWorstDDPeriod(), 0.0) : null,
                        worstDDDto2 != null && worstDDDto2.getWorstDDPeriod() != null ? MathUtils.add(worstDDDto2.getWorstDDPeriod(), 0.0) : null,
                        worstDDDto3 != null && worstDDDto3.getWorstDDPeriod() != null ? MathUtils.add(worstDDDto3.getWorstDDPeriod(), 0.0) : null,
                        worstDDDto4 != null && worstDDDto4.getWorstDDPeriod() != null ? MathUtils.add(worstDDDto4.getWorstDDPeriod(), 0.0) : null,
                        worstDDDto1 != null && worstDDDto1.getWorstDDPeriod() != null ? worstDDDto1.getWorstDDPeriod().toString() : null,
                        worstDDDto2 != null && worstDDDto2.getWorstDDPeriod() != null ? worstDDDto2.getWorstDDPeriod().toString() : null,
                        worstDDDto3 != null && worstDDDto3.getWorstDDPeriod() != null ? worstDDDto3.getWorstDDPeriod().toString() : null,
                        worstDDDto4 != null && worstDDDto4.getWorstDDPeriod() != null ? worstDDDto4.getWorstDDPeriod().toString() : null));

                // Recovery Months
                Integer recoveryMonths = worstDDDto.getRecoveryMonths();
                Integer recoveryMonths1 = isBenchmark || tranche == 2 ? null : recoveryMonths;
                Integer recoveryMonths2 = !isBenchmark || tranche == 2 ? null : recoveryMonths;
                Integer recoveryMonths3 = isBenchmark || tranche == 1 ? null : recoveryMonths;
                Integer recoveryMonths4 = !isBenchmark || tranche == 1 ? null : recoveryMonths;
                performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Recovery (months)",
                        recoveryMonths1 != null ? MathUtils.add(recoveryMonths1, 0.0) : null,
                        recoveryMonths2 != null ? MathUtils.add(recoveryMonths2, 0.0) : null,
                        recoveryMonths3 != null ? MathUtils.add(recoveryMonths3, 0.0) : null,
                        recoveryMonths4 != null ? MathUtils.add(recoveryMonths4, 0.0) : null,
                        recoveryMonths1 != null ? recoveryMonths1.toString() : null,
                        recoveryMonths2 != null ? recoveryMonths2.toString() : null,
                        recoveryMonths3 != null ? recoveryMonths3.toString() : null,
                        recoveryMonths4 != null ? recoveryMonths4.toString() : null));
            }

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
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Winning Periods",
                    !isBenchmark && tranche == 1 ? MathUtils.divide(positives + 0.0, returns.length + 0.0) : null,
                    isBenchmark && tranche == 1 ? MathUtils.divide(positives + 0.0, returns.length + 0.0) : null,
                    !isBenchmark && tranche == 2 ? MathUtils.divide(positives + 0.0, returns.length + 0.0) : null,
                    isBenchmark && tranche == 2 ? MathUtils.divide(positives + 0.0, returns.length + 0.0) : null,
                    !isBenchmark && tranche == 1 ? MathUtils.multiply(2, 100.0, MathUtils.divide(positives + 0.0, returns.length + 0.0)) + "%" : null,
                    isBenchmark && tranche == 1 ? MathUtils.multiply(2, 100.0, MathUtils.divide(positives + 0.0, returns.length + 0.0)) + "%" : null,
                    !isBenchmark && tranche == 2 ? MathUtils.multiply(2, 100.0, MathUtils.divide(positives + 0.0, returns.length + 0.0)) + "%" : null,
                    isBenchmark && tranche == 2 ? MathUtils.multiply(2, 100.0, MathUtils.divide(positives + 0.0, returns.length + 0.0)) + "%" : null));

            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Losing Periods",
                    !isBenchmark && tranche == 1? MathUtils.divide(negatives + 0.0, returns.length + 0.0) : null,
                    isBenchmark && tranche == 1 ? MathUtils.divide(negatives + 0.0, returns.length + 0.0) : null,
                    !isBenchmark && tranche == 2 ? MathUtils.divide(negatives + 0.0, returns.length + 0.0) : null,
                    isBenchmark && tranche == 2 ? MathUtils.divide(negatives + 0.0, returns.length + 0.0) : null,
                    !isBenchmark && tranche == 1 ? MathUtils.multiply(2, 100.0, MathUtils.divide(negatives + 0.0, returns.length + 0.0)) + "%" : null,
                    isBenchmark && tranche == 1 ? MathUtils.multiply(2, 100.0, MathUtils.divide(negatives + 0.0, returns.length + 0.0)) + "%" : null,
                    !isBenchmark && tranche == 2 ? MathUtils.multiply(2, 100.0, MathUtils.divide(negatives + 0.0, returns.length + 0.0)) + "%" : null,
                    isBenchmark && tranche == 2 ? MathUtils.multiply(2, 100.0, MathUtils.divide(negatives + 0.0, returns.length + 0.0)) + "%" : null));

            // Gain Loss ratio
            Double gainLossRatio = null;
            Double gainLossRatio1 = null;
            Double gainLossRatio2 = null;
            Double gainLossRatio3 = null;
            Double gainLossRatio4 = null;
            if(negatives > 0 && negativeSum != 0.0) {
                Double positiveAvg = MathUtils.divide(18, positiveSum, positives + 0.0);
                Double negativeAvg = MathUtils.divide(18, negativeSum, negatives + 0.0);

                gainLossRatio = MathUtils.abs(MathUtils.divide(18, positiveAvg, negativeAvg));
                gainLossRatio1 = !isBenchmark && tranche == 1 ? gainLossRatio : null;
                gainLossRatio2 = isBenchmark && tranche == 1 ? gainLossRatio : null;
                gainLossRatio3 = !isBenchmark && tranche == 2 ? gainLossRatio : null;
                gainLossRatio4 = isBenchmark && tranche == 2 ? gainLossRatio : null;

            }
            performance.add(new MonitoringRiskHedgeFundPerformanceRecordDto("Gain/Loss Ratio",gainLossRatio1, gainLossRatio2,gainLossRatio3, gainLossRatio4,
                    !isBenchmark && tranche == 1 ? MathUtils.add(0.0, gainLossRatio).toString() : null,
                    isBenchmark && tranche == 1 ? MathUtils.add(0.0, gainLossRatio).toString() : null,
                    !isBenchmark && tranche == 2? MathUtils.add(0.0, gainLossRatio).toString() : null,
                    isBenchmark && tranche == 2 ? MathUtils.add(0.0, gainLossRatio).toString() : null));
        }else{
            String errorMessage = "Missing " + (isBenchmark ? "HFRI returns " :"HF " + (tranche == 1 ? " (class A) " :
                    " (class B) ") + " returns") + " for period [" + DateUtils.getDateFormatted(dateFrom) +
                    ", " + DateUtils.getDateFormatted(dateTo) + "]: expected " + period + ", found 0 (null). ";
            logger.error(errorMessage);
            responseDto.appendErrorMessageEn(errorMessage);
            return responseDto;
        }
        responseDto.setRecords(performance);
        if(responseDto.getStatus() == null){
            responseDto.setStatus(ResponseStatusType.SUCCESS);
        }
        return responseDto;
    }
    /* ****************************************************************************************************************/

    @Override
    public ByteArrayInputStream exportTopPortfolio(Date date) {
        String[] columns = {"Date", "Name", "Class", "NAV",
                "MTD", "QTD", "YTD", "Contribution to YTD", "Contribution to VAR"};

        try {
            Workbook workbook = new XSSFWorkbook();
            CreationHelper creationHelper = workbook.getCreationHelper();

            Sheet sheet = workbook.createSheet("Top Portfolios");

            Font headerFont = workbook.createFont();
            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            populateHeaderRow(columns, headerCellStyle, headerRow);

            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            int rowNum = 1;
            for (MonitoringRiskHedgeFundFundAllocationDto dto:getTopPortfolioList(date)) {
                Row row = sheet.createRow(rowNum++);
                populateCells(dateCellStyle, dto, row, date);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            logger.error("IOException occurred");
            return null;
        }
    }

    private void populateHeaderRow(String[] columns, CellStyle headerCellStyle, Row headerRow) {
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
    }

    private void populateCells(CellStyle dateCellStyle, MonitoringRiskHedgeFundFundAllocationDto dto, Row row, Date date) {
        Cell dateCell = row.createCell(0);
        dateCell.setCellValue(date);
        dateCell.setCellStyle(dateCellStyle);

        row.createCell(1).setCellValue(dto.getFundName());
        row.createCell(2).setCellValue(dto.getClassName());
        row.createCell(3).setCellValue(dto.getNavPercent());
        row.createCell(4).setCellValue(dto.getMtd());
        row.createCell(5).setCellValue(dto.getQtd());
        row.createCell(6).setCellValue(dto.getYtd());
        row.createCell(7).setCellValue(dto.getContributionToYTD());
        Cell contribVAR = row.createCell(8);
        if (dto.getContributionToVAR() == null) {
            contribVAR.setCellValue("");
        } else {
            contribVAR.setCellValue(dto.getContributionToVAR());
        }
    }

    private List<MonitoringRiskHedgeFundFundAllocationDto> getTopPortfolioList(Date date) {
        return this.topPortfolioEntityConverter.disassembleList(this.topPortfolioRepository.findAllocationsByDate(date));
    }

    @Override
    public boolean deletePortfolios(Date date, String updater) {
        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to delete Top Portfolio data: the user is not found in the database!");
                return false;
            }
            List<AllocationByTopPortfolio> matchingFunds = this.topPortfolioRepository.findAllocationsByDate(date);
            if (matchingFunds.isEmpty()) {
                logger.error("No matching data found to be deleted");
                return false;
            }
            List<Long> portfoliosToDelete = new ArrayList<>();
            List<Long> filesToDelete = new ArrayList<>();
            for (AllocationByTopPortfolio portfolio : matchingFunds) {
                portfoliosToDelete.add(portfolio.getId());
                if (!filesToDelete.contains(portfolio.getFile().getId())) {
                    filesToDelete.add(portfolio.getFile().getId());
                }
            }

            for (Long id : portfoliosToDelete) {
                this.topPortfolioRepository.delete(id);
            }
            for (Long id : filesToDelete) {
                this.fileService.delete(id);
            }
            logger.info("Top Portfolio data has been deleted successfully, updater: " + updater);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to delete Top Portfolio data: repository problem, ", ex);
            return false;
        }
    }

    @Override
    public MonitoringRiskHedgeFundAllocationResultDto uploadTopPortfolio(Set<FilesDto> filesDtoSet, String updater) {

        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to update Top Portfolio data: the user is not found in the database!");
                return new MonitoringRiskHedgeFundAllocationResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Top Portfolio data: the user is not found in the database!", "");
            }

            FilesDto filesDto;
            Iterator<Row> rowIterator;
            Row previousRow = null;
            Row currentRow;
            int previousMonth;
            int currentMonth;
            int rowNumber = 0;
            List<AllocationByTopPortfolio> allocationByTopPortfolioList = new ArrayList<>();

            try {
                filesDto = filesDtoSet.iterator().next();
                InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
                XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
                XSSFSheet sheet = workbook.getSheet("Database");
                rowIterator = sheet.iterator();
            } catch (Exception ex) {
                logger.error("Failed to update Top Portfolio data: the file or the sheet 'Database' cannot be opened, ", ex);
                return new MonitoringRiskHedgeFundAllocationResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Top Portfolio data: the file or the sheet 'Database' cannot be opened!", "");
            }

//            for (int i = 0; i < 1; i++) {
//                if(rowIterator.hasNext()) {
//                    previousRow = rowIterator.next();
//                    rowNumber++;
//                } else {
//                    logger.error("Failed to update Top Portfolio data: the sheet 'Database' contains less than 10 rows!");
//                    return new MonitoringRiskHedgeFundAllocationResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Top Portfolio data: the sheet 'Database' contains less than 10 rows!", "");
//                }
//            }

            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                rowNumber++;

                if (currentRow.getCell(0) == null) {
                    //end of data
                    break;
                }

                try {
                    allocationByTopPortfolioList.add(this.createTopPortfolio(currentRow, employee));
                } catch (Exception ex) {
                    logger.error("Failed to update Top Portfolio data: error parsing row #" + rowNumber + ", ", ex);
                    return new MonitoringRiskHedgeFundAllocationResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Top Portfolio data: error parsing row #" + rowNumber + "!", "");
                }

            }

            try {
                if(allocationByTopPortfolioList.size() > 0) {
                    filesDto.setType(FileTypeLookup.MONITORING_RISK_ALLOCATION_BY_TOP_PORTFOLIO.getCode());
                    Long fileId = this.fileService.save(filesDto, FileTypeLookup.MONITORING_RISK_ALLOCATION_BY_TOP_PORTFOLIO.getCatalog());

                    Files file = new Files();
                    file.setId(fileId);
                    for (AllocationByTopPortfolio allocationByTopPortfolio : allocationByTopPortfolioList) {
                        allocationByTopPortfolio.setFile(file);
                    }

                    this.topPortfolioRepository.save(allocationByTopPortfolioList);
                }
            } catch (Exception ex) {
                logger.error("Failed to update Top Portfolio data: repository problem, ", ex);
                return new MonitoringRiskHedgeFundAllocationResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Top Portfolio data: repository problem!", "");
            }

            logger.info("Top Portfolio data has been updated successfully, updater: " + updater);
            List<MonitoringRiskHedgeFundFundAllocationDto> allocationSubStrategyDtos = this.topPortfolioEntityConverter.disassembleList(this.topPortfolioRepository.findAllByOrderByDateAsc());
            return new MonitoringRiskHedgeFundAllocationResultDto(allocationSubStrategyDtos, ResponseStatusType.SUCCESS, "", "Top Portfolio data has been updated successfully!", "");

        } catch (Exception ex) {
            logger.error("Failed to update Top Portfolio data, ", ex);
            return new MonitoringRiskHedgeFundAllocationResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Top Portfolio data!", "");
        }
    }

    private AllocationByTopPortfolio createTopPortfolio(Row row, Employee updater) {
        try {
            return new AllocationByTopPortfolio(
                    updater,
                    new Files(),
                    row.getCell(0).getDateCellValue(),
                    ExcelUtils.getStringValueFromCell(row.getCell(1)),
                    ExcelUtils.getStringValueFromCell(row.getCell(2)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(3)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(4)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(5)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(6)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(7)),
                    ExcelUtils.getDoubleValueFromCell(row.getCell(8))
            );
        } catch (Exception ex) {
            logger.error("Failed to update Allocation by Top Portfolio data: row parsing error, ", ex);
            throw ex;
        }
    }

    @Override
    public ByteArrayInputStream exportStrategy(Date date) {
        String[] columns = {"Current Month", "Previous Month", "Strategy Name", "Current Month Percentage",
                "Previous Month Percentage"};

        try {
            Workbook workbook = new XSSFWorkbook();
            CreationHelper creationHelper = workbook.getCreationHelper();

            Sheet sheet = workbook.createSheet("Sub-Strategies");

            Font headerFont = workbook.createFont();
            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            populateHeaderRow(columns, headerCellStyle, headerRow);

            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            int rowNum = 1;
            for (MonitoringRiskHedgeFundAllocationSubStrategyDto dto:getStrategyList(date)) {
                Row row = sheet.createRow(rowNum++);
                populateStrategyCells(dateCellStyle, dto, row, date);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            logger.error("IOException occurred");
            return null;
        }
    }

    private void populateStrategyCells(CellStyle dateCellStyle, MonitoringRiskHedgeFundAllocationSubStrategyDto dto, Row row, Date date) {
        Cell currentMonthCell = row.createCell(0);
        currentMonthCell.setCellValue(dto.getPreviousDate());
        currentMonthCell.setCellStyle(dateCellStyle);

        Cell previousMonthCell = row.createCell(1);
        previousMonthCell.setCellValue(dto.getCurrentDate());
        previousMonthCell.setCellStyle(dateCellStyle);

        row.createCell(2).setCellValue(dto.getSubStrategyName());
        row.createCell(3).setCellValue(dto.getCurrentValue());
        row.createCell(4).setCellValue(dto.getPreviousValue());
    }

    private List<MonitoringRiskHedgeFundAllocationSubStrategyDto> getStrategyList(Date date) {
        return this.subStrategyEntityConverter.disassembleList(this.subStrategyRepository.findByFirstDate(date));
    }

    @Override
    public boolean deleteReturnsClassAFile(Long reportId, String updater){
        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to delete Monitoring Risk HF returns file: the user is not found in the database!");
                return false;
            }
            MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findOne(reportId);
            if(report != null){
                this.riskHFReturnRepository.deleteByReportIdAndPortfolioTypeCode(reportId, MonitoringRiskHFPortfolioTypeLookup.SINGULAR_A.getCode());
                // delete file
                this.fileService.safeDelete(report.getReturnsClassAFile().getId());
                // delete file association
                report.setReturnsClassAFile(null);
                this.riskReportRepository.save(report);
                logger.info("Monitoring Risk HF Returns class A file data has been deleted successfully, updater: " + updater);
                return true;
            }
        } catch (Exception ex) {
            logger.error("Failed to delete Monitoring Risk HF Returns class A data: repository problem, ", ex);
        }
        return false;
    }

    @Override
    public boolean deleteReturnsClassBFile(Long reportId, String updater){
        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to delete Monitoring Risk HF returns file: the user is not found in the database!");
                return false;
            }
            MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findOne(reportId);
            if(report != null){
                this.riskHFReturnRepository.deleteByReportIdAndPortfolioTypeCode(reportId, MonitoringRiskHFPortfolioTypeLookup.SINGULAR_B.getCode());
                // delete file
                this.fileService.safeDelete(report.getReturnsClassBFile().getId());
                // delete file association
                report.setReturnsClassBFile(null);
                this.riskReportRepository.save(report);
                logger.info("Monitoring Risk HF Returns class A file data has been deleted successfully, updater: " + updater);
                return true;
            }
        } catch (Exception ex) {
            logger.error("Failed to delete Monitoring Risk HF Returns class A data: repository problem, ", ex);
        }
        return false;
    }

    @Override
    public boolean deleteReturnsConsFile(Long reportId, String updater){
        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to delete Monitoring Risk HF returns file: the user is not found in the database!");
                return false;
            }
            MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findOne(reportId);
            if(report != null){
                this.riskHFReturnRepository.deleteByReportIdAndPortfolioTypeCode(reportId, MonitoringRiskHFPortfolioTypeLookup.SINGULAR_CONS.getCode());
                // delete file
                this.fileService.safeDelete(report.getReturnsConsFile().getId());
                // delete file association
                report.setReturnsConsFile(null);
                this.riskReportRepository.save(report);
                logger.info("Monitoring Risk HF Returns cons file data has been deleted successfully, updater: " + updater);
                return true;
            }
        } catch (Exception ex) {
            logger.error("Failed to delete Monitoring Risk HF Returns cons data: repository problem, ", ex);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteAllocationsConsFile(Long reportId, String updater){
        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to delete Monitoring Risk HF allocations file: the user is not found in the database!");
                return false;
            }
            MonitoringRiskHFMonthlyReport report = this.riskReportRepository.findOne(reportId);
            if(report != null){
                this.riskHFPortfolioAllocationMonthRepository.deleteByReportId(reportId);
                this.riskHFPortfolioAllocationQuarterRepository.deleteByReportId(reportId);
                this.riskHFPortfolioAllocationYearRepository.deleteByReportId(reportId);
                // delete file
                this.fileService.safeDelete(report.getReturnsClassBFile().getId());
                // delete file association
                report.setAllocationsConsFile(null);
                this.riskReportRepository.save(report);
                logger.info("Monitoring Risk HF Allocations cons file data has been deleted successfully, updater: " + updater);
                return true;
            }
        } catch (Exception ex) {
            logger.error("Failed to delete Monitoring Risk HF Returns cons data: repository problem, ", ex);
        }
        return false;
    }


    @Override
    public boolean deleteStrategy(Date date, String updater) {
        try {
            Employee employee = this.employeeRepository.findByUsername(updater);

            if (employee == null) {
                logger.error("Failed to delete Sub Strategy data: the user is not found in the database!");
                return false;
            }
            List<AllocationBySubStrategy> matchingStrategies = this.subStrategyRepository.findByFirstDate(date);
            if (matchingStrategies.isEmpty()) {
                logger.error("No matching data found to be deleted");
                return false;
            }
            List<Long> strategiesToDelete = new ArrayList<>();
            List<Long> filesToDelete = new ArrayList<>();
            for (AllocationBySubStrategy strategy : matchingStrategies) {
                strategiesToDelete.add(strategy.getId());
                if (!filesToDelete.contains(strategy.getFile().getId())) {
                    filesToDelete.add(strategy.getFile().getId());
                }
            }

            for (Long id : strategiesToDelete) {
                this.subStrategyRepository.delete(id);
            }
            for (Long id : filesToDelete) {
                this.fileService.delete(id);
            }
            logger.info("Sub Strategy data has been deleted successfully, updater: " + updater);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to delete Sub Strategy data: repository problem, ", ex);
            return false;
        }
    }

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

//            for (int i = 0; i < 1; i++) {
//                if(rowIterator.hasNext()) {
//                    previousRow = rowIterator.next();
//                    rowNumber++;
//                } else {
//                    logger.error("Failed to update Sub-strategy data: the sheet 'Database' contains less than 10 rows!");
//                    return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: the sheet 'Database' contains less than 15 rows!", "");
//                }
//            }

            while (rowIterator.hasNext()) {
                currentRow = rowIterator.next();
                rowNumber++;

                if (currentRow.getCell(0) == null) {
                    //end of data
                    break;
                }

                try {
                    previousMonth = DateUtils.getMonth(currentRow.getCell(1).getDateCellValue());
                    currentMonth = DateUtils.getMonth(currentRow.getCell(0).getDateCellValue());

                    if (previousMonth != currentMonth) {
                        allocationBySubStrategyList.add(this.create(currentRow, employee));
                    }
                } catch (Exception ex) {
                    logger.error("Failed to update Sub-strategy data: error parsing row #" + rowNumber + ", ", ex);
                    return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: error parsing row #" + rowNumber + "!", "");
                }

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

                    this.subStrategyRepository.save(allocationBySubStrategyList);
                }
            } catch (Exception ex) {
                logger.error("Failed to update Sub-strategy data: repository problem, ", ex);
                return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data: repository problem!", "");
            }

            logger.info("Sub-strategy data has been updated successfully, updater: " + updater);
            List<MonitoringRiskHedgeFundAllocationSubStrategyDto> allocationSubStrategyDtos = this.subStrategyEntityConverter.disassembleList(this.subStrategyRepository.findAllByOrderByFirstDateAsc());
            return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(allocationSubStrategyDtos, ResponseStatusType.SUCCESS, "", "Sub-strategy data has been updated successfully!", "");

        } catch (Exception ex) {
            logger.error("Failed to update Sub-strategy data, ", ex);
            return new MonitoringRiskHedgeFundAllocationSubStrategyResultDto(null, ResponseStatusType.FAIL, "", "Failed to update Sub-strategy data!", "");
        }
    }

    @Override
    public EntitySaveResponseDto saveReport(MonitoringRiskHFMonthlyReportDto reportDto) {
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        if(reportDto != null){
            MonitoringRiskHFMonthlyReport entity = new MonitoringRiskHFMonthlyReport();
            entity.setReportDate(DateUtils.getLastDayOfCurrentMonth(reportDto.getReportDate()));
            entity.setId(reportDto.getId());
            this.riskReportRepository.save(entity);
            saveResponseDto.setEntityId(entity.getId());
            saveResponseDto.setStatus(ResponseStatusType.SUCCESS);
            return saveResponseDto;
        }else {
            saveResponseDto.setStatus(ResponseStatusType.FAIL);
            return saveResponseDto;
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
