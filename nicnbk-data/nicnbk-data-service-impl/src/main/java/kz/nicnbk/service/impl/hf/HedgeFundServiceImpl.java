package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.benchmark.BenchmarkValueRepository;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.hf.HedgeFundRepository;
import kz.nicnbk.repo.model.benchmark.Benchmark;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.hf.HedgeFundReturnService;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.api.hf.HedgeFundSubstrategyService;
import kz.nicnbk.service.api.hf.InvestorBaseService;
import kz.nicnbk.service.converter.hf.HedgeFundEntityConverter;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.hf.*;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by timur on 19.10.2016.
 */
@Service
public class HedgeFundServiceImpl implements HedgeFundService {

    private static final Logger logger = LoggerFactory.getLogger(HedgeFundServiceImpl.class);

    @Autowired
    private HedgeFundRepository repository;

    @Autowired
    private HedgeFundEntityConverter converter;

    @Autowired
    private HedgeFundSubstrategyService hedgeFundSubstrategyService;

    @Autowired
    private InvestorBaseService investorBaseService;

    @Autowired
    private HedgeFundReturnService hedgeFundReturnService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private BenchmarkValueRepository benchmarkValueRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(HedgeFundDto hedgeFundDto, String updater) {

        // TODO: handle error and rollback || transactional?

        try {
            HedgeFund entity = converter.assemble(hedgeFundDto);

            if(hedgeFundDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(hedgeFundDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.repository.findOne(hedgeFundDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = repository.findOne(hedgeFundDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }
            Long id = repository.save(entity).getId();
            hedgeFundDto.setId(id);

            // delete substrategies
            boolean deleted = this.hedgeFundSubstrategyService.deleteByFundId(id);
            // save substrategy
            if (hedgeFundDto.getStrategyBreakdownList() != null) {
                for (SubstrategyBreakdownDto substrategyBreakdownDto : hedgeFundDto.getStrategyBreakdownList()) {
                    HedgeFundSubstrategyDto dto = new HedgeFundSubstrategyDto();
                    HedgeFundDto fundDto = new HedgeFundDto();
                    fundDto.setId(id);
                    dto.setFund(fundDto);

                    BaseDictionaryDto substrategyDto = new BaseDictionaryDto();
                    substrategyDto.setCode(substrategyBreakdownDto.getCode());
                    dto.setSubstrategy(substrategyDto);
                    dto.setValue(substrategyBreakdownDto.getValue());
                    this.hedgeFundSubstrategyService.save(dto);
                }
            }

            // delete investor base
            deleted = this.investorBaseService.deleteByFundId(id);

            // save investor base
            if (hedgeFundDto.getInvestorBaseList() != null) {
                for (InvestorBaseDto dto : hedgeFundDto.getInvestorBaseList()) {
                    dto.setHedgeFund(hedgeFundDto);
                    this.investorBaseService.save(dto);
                }
            }

            // delete returns
            deleted = this.hedgeFundReturnService.deleteByFundId(id);

            // save returns
            // scale to numeric
            scaleToNumeric(hedgeFundDto);
            // save
            if (hedgeFundDto.getReturns() != null) {
                for (ReturnDto dto : hedgeFundDto.getReturns()) {
                    dto.setFund(hedgeFundDto);
                    Long returnId = this.hedgeFundReturnService.save(dto);
                    if (returnId == null) {
                        // failed to save return
                        // TODO: rollback
                    }
                }
            }

            logger.info(hedgeFundDto.getId() == null ? "HF fund created: " + id + ", by " + entity.getCreator().getUsername() :
                    "HF fund updated: " + id + ", by " + updater);
            return id;
        }catch (Exception ex){
            logger.error("Error saving HF fund: " + (hedgeFundDto != null && hedgeFundDto.getId() != null ? hedgeFundDto.getId() : "new") ,ex);
        }
        return null;
    }

    @Override
    public HedgeFundDto get(Long fundId) {
        try {
            HedgeFund entity = this.repository.findOne(fundId);
            HedgeFundDto fundDto = this.converter.disassemble(entity);

            // substrategy breakdown
            List<HedgeFundSubstrategyDto> substrategyDtoList = this.hedgeFundSubstrategyService.findByFundId(fundId);
            if (substrategyDtoList != null) {
                List<SubstrategyBreakdownDto> substrategyBreakdownDtoList = new ArrayList<>();
                for (HedgeFundSubstrategyDto entityDto : substrategyDtoList) {
                    SubstrategyBreakdownDto dto = new SubstrategyBreakdownDto();
                    dto.setCode(entityDto.getSubstrategy().getCode());
                    dto.setValue(entityDto.getValue());
                    substrategyBreakdownDtoList.add(dto);
                }
                fundDto.setStrategyBreakdownList(substrategyBreakdownDtoList);
            }
            // investor base
            List<InvestorBaseDto> investorBaseList = this.investorBaseService.findByFundId(fundId);
            fundDto.setInvestorBaseList(investorBaseList);
            // returns
            List<ReturnDto> returnsList = this.hedgeFundReturnService.findByFundId(fundId);
            Collections.sort(returnsList);
            fundDto.setReturns(returnsList);
            // calculated values
            setCalculatedValues(fundDto);
            // scale to percent
            scaleToPercent(fundDto);
            return fundDto;
        }catch(Exception ex){
            logger.error("Error loading HF fund: " + fundId, ex);
        }
        return null;
    }

    private void setCalculatedValues(HedgeFundDto fundDto){

//        if(fundDto.getInceptionDate() != null){
//            fundDto.setNumMonths(DateUtils.getMonthsDifference(fundDto.getInceptionDate(), new Date()));
//        }

        try {
            if (fundDto.getReturns() != null && !fundDto.getReturns().isEmpty()) {

                // TODO: to array with date?
                double[] fundReturnsArray = getReturnsAsArray(fundDto.getReturns());
                Date[] fundReturnDatesArray = getReturnDatesAsArray(fundDto.getReturns());

                // number of months
                fundDto.setNumMonths(fundReturnsArray.length);

                // number of positive/negative months
                fundDto.setNumPositiveMonths(getNumberOfPositiveMonths(fundReturnsArray));
                fundDto.setNumNegativeMonths(getNumberOfNegativeMonths(fundReturnsArray));

                // return since inception
                fundDto.setReturnSinceInception(getReturnSinceInception(fundReturnsArray));

                // YTD
                fundDto.setYTD(getYTD(fundDto.getReturns()));

                // annualized return
                fundDto.setAnnualizedReturn(MathUtils.getAnnualizedReturn(fundReturnsArray, 2));

                // beta
                Date dateFrom = fundDto.getReturns().get(0).getFirstNotNullMonth();
                Date dateTo = fundDto.getReturns().get(fundDto.getReturns().size() - 1).getLastNotNullMonth();
                double[] SNPBenchmarkReturns = this.benchmarkService.getMonthReturnValuesBetweenDatesAsArray(dateFrom, dateTo, BenchmarkLookup.SNP_500_SPTR.getCode());
                if (fundReturnsArray == null) {
                    logger.error("HF fund calculations: returns missing for fund=" + fundDto.getId() + ". Beta - will not be calculated");
                }else if(SNPBenchmarkReturns == null ){
                    logger.error("HF fund calculations: S&P returns missing. Beta - will not be calculated");
                }else if(fundReturnsArray.length != SNPBenchmarkReturns.length){
                    logger.error("HF fund calculations: fund returns and S&P returns array size different for fund=" + fundDto.getId() +
                            ". Beta - will not be calculated");
                } else {
                    fundDto.setBeta(MathUtils.getBeta(fundReturnsArray, SNPBenchmarkReturns, 4));
                }

                // annual volatility
                fundDto.setAnnualVolatility(getAnnualVolatility(fundReturnsArray));

                // worst drawdown
                Pair<Double, String> worsdDrawdownResult = getWorstDrawdown(fundReturnsArray, fundReturnDatesArray);
                fundDto.setWorstDrawdown(worsdDrawdownResult.getFirst());
                // worst drawdown period
                fundDto.setWorstDrawdownPeriod(worsdDrawdownResult.getSecond());

                // fund annualized return
                double fundAnnualizedReturn = MathUtils.getAnnualizedReturn(fundReturnsArray, 2);
                double[] TBillsBenchmarkReturns = this.benchmarkService.getMonthReturnValuesBetweenDatesAsArray(dateFrom, dateTo, BenchmarkLookup.T_BILLS.getCode());
                // check returns array sizes
                if (fundReturnsArray.length != TBillsBenchmarkReturns.length) {
                    logger.error("HF fund calculations: fund returns and TBills returns array size different for fund=" + fundDto.getId() +
                            " .Sharpe Ratio, Sortino Ratio - will not be calculated");
                    return;
                }
                Double benchmarkAnnualizedReturn = MathUtils.getAnnualizedReturn(TBillsBenchmarkReturns, 2);

                // std deviation
                Double stdDeviation = getStandardDeviation(fundReturnsArray);

                // sharpe ratio
                fundDto.setSharpeRatio(getSharpeRatio(fundAnnualizedReturn, benchmarkAnnualizedReturn, stdDeviation));

                // sortino ratio
                fundDto.setSortinoRatio(MathUtils.getSortinoRatio(fundAnnualizedReturn, benchmarkAnnualizedReturn, fundReturnsArray, 4));
            }
        }catch (Exception ex){
            logger.error("Failed to set calculated values for HF fund: fund=" + fundDto.getId(), ex);
        }
    }

    private Double getSharpeRatio(Double fundAnnualizedReturn, Double benchmarkAnnualizedReturn, Double stdDeviation){
        if(fundAnnualizedReturn == null || benchmarkAnnualizedReturn == null || stdDeviation == null){
            return null;
        }
        BigDecimal divider = BigDecimal.valueOf(stdDeviation).multiply(BigDecimal.valueOf(Math.sqrt(12)));
        if(divider == null || divider.doubleValue() == 0.0){
            return null;
        }
        BigDecimal value = BigDecimal.valueOf(fundAnnualizedReturn).subtract(BigDecimal.valueOf(benchmarkAnnualizedReturn));
        value = MathUtils.divide(value, divider);
        //return getRoundedValue(value);
        return value.doubleValue();
    }

    private Pair<Double, String> getWorstDrawdown(double[] returns, Date[] dates){
        if(returns == null || returns.length == 0){
            return null;
        }
        // cumulative returns
        BigDecimal[] cumulativeReturns = new BigDecimal[returns.length];
        for(int i = 0; i < returns.length; i++){
            if(i == 0){
                cumulativeReturns[i] = BigDecimal.ONE.add(BigDecimal.valueOf(returns[i]));
            }else{
                cumulativeReturns[i] = cumulativeReturns[i - 1].multiply(BigDecimal.ONE.add(BigDecimal.valueOf(returns[i])));
            }
        }

        // maxed cumulative returns
        BigDecimal[] maxedReturns = new BigDecimal[returns.length];
        BigDecimal max = null;
        for(int i = 0; i < cumulativeReturns.length; i++){
            if(max == null){
                max = cumulativeReturns[i];
                maxedReturns[i] = cumulativeReturns[i];
            }else{
                if(cumulativeReturns[i].compareTo(max) < 0){
                    maxedReturns[i] = max;
                }else{
                    maxedReturns[i] = cumulativeReturns[i];
                    max = cumulativeReturns[i];
                }
            }
        }

        // worst drawDown
        BigDecimal maxDifference = null;
        BigDecimal maxValue = null;
        BigDecimal minValue = null;
        int minIndex = 0;
        for(int i = 0; i < maxedReturns.length; i++){
            BigDecimal diff = maxedReturns[i].subtract(cumulativeReturns[i]);
            if(maxDifference == null || diff.compareTo(maxDifference) > 0){
                maxDifference = diff;
                maxValue = maxedReturns[i];
                minValue = cumulativeReturns[i];
                minIndex = i;
            }
        }

        if(maxValue != null && minValue != null && maxValue.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal worstDrawdown = maxValue.subtract(minValue);
            worstDrawdown = MathUtils.divide(worstDrawdown, maxValue);

            // worst drawdown period
            String period = null;
            for(int i = minIndex - 1; i >= 0; i--){
                if(cumulativeReturns[i].compareTo(maxValue) == 0){
                    period = DateUtils.getDateFormatted(dates[i]) + " - " + DateUtils.getDateFormatted(dates[minIndex]);
                }
            }

            Pair<Double, String> pair = new Pair<Double, String>(new Double(worstDrawdown.doubleValue()), period);
            return pair;
            //return worstDrawdown.doubleValue();
        }else{
            throw new IllegalStateException("Max Cumulative return cannot be zero");
        }

//        if(returns == null || returns.length == 0){
//            return null;
//        }
//        Double max = null;
//        Double minAfterMax = null;
//        for(int i = 0; i < returns.length; i++){
//            if(max == null || max < returns[i]){
//                max = returns[i];
//                minAfterMax = null;
//            } else if(max != null && (minAfterMax == null || returns[i] < minAfterMax)){
//                minAfterMax = returns[i];
//            }
//        }
//        if(max != null && max != 0.0 && minAfterMax != null) {
//            BigDecimal value = BigDecimal.valueOf(max).subtract(BigDecimal.valueOf(minAfterMax)).
//                    divide(BigDecimal.valueOf(max), 4, RoundingMode.HALF_UP);
//            //return getRoundedValue(value);
//            return value.doubleValue();
//        }
//        return null;
    }

    /**
     * Calculates annualized volatility using StandardDeviation class from apache-commons-math.
     *
     * @param returns
     * @return
     */
    private Double getAnnualVolatility(double[] returns){
        if(returns == null || returns.length == 0){
            return null;
        }
        double stdValue = getStandardDeviation(returns);
        double value = (stdValue * Math.sqrt(12));
        //return getRoundedValue(value);
        return value;
    }

    private int getNumberOfPositiveMonths(List<ReturnDto> returns){
        if(returns != null && !returns.isEmpty()){
            int count = 0;
            for(ReturnDto returnDto: returns){
                count += returnDto.getNumberOfPositiveMonths();
            }
            return count;
        }
        return 0;
    }

    private int getNumberOfNegativeMonths(List<ReturnDto> returns){
        if(returns != null && !returns.isEmpty()){
            int count = 0;
            for(ReturnDto returnDto: returns){
                count += returnDto.getNumberOfNegativeMonths();
            }
            return count;
        }
        return 0;
    }

    private int getNumberOfPositiveMonths(double[] returns){
        if(returns != null && returns.length > 0){
            int count = 0;
            for(int i = 0; i < returns.length; i++){
                count += returns[i] > 0 ? 1 : 0;
            }
            return count;
        }
        return 0;
    }

    private int getNumberOfNegativeMonths(double[] returns){
        if(returns != null && returns.length > 0){
            int count = 0;
            for(int i = 0; i < returns.length; i++){
                count += returns[i] < 0 ? 1 : 0;
            }
            return count;
        }
        return 0;
    }

    private Double getReturnSinceInception(double[] returns){
        if(returns != null && returns.length > 0){
//            double value = 1;
//            for(int i = 0; i < returns.length; i++){
//                value *= (1 + returns[i]);
//            }
//            return value;

            BigDecimal value = new BigDecimal("1.0");
            for(int i = 0; i < returns.length; i++){
                value = value.multiply(new BigDecimal("1.0").add(BigDecimal.valueOf(returns[i])));
            }
            value = value.subtract(BigDecimal.ONE);
            return value.doubleValue();

        }
        return null;
    }

    private Double getYTD(List<ReturnDto> returns){
        if(returns != null && !returns.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            int currentYear = DateUtils.getCurrentYear();
            boolean YTDexists = false;
            for (ReturnDto returnDto : returns) {
                if(returnDto.getYear() == currentYear){
                    sum = sum.add(returnDto.getSum());
                    YTDexists = true;
                }
            }
            //return getRoundedValue(sum);
            return YTDexists ? sum.doubleValue() : null;
        }
        return null;
    }

    private Double getStandardDeviation(double[] values){
        StandardDeviation standardDeviation = new StandardDeviation();
        Double stdValue = standardDeviation.evaluate(values);
        return stdValue;
    }

    private double[] getReturnsAsArray(List<ReturnDto> returns){
        if(returns != null){
            List<Double> tempReturns = new ArrayList<Double>();
            for(ReturnDto returnDto: returns){
                if(returnDto.getJanuary() != null){
                    tempReturns.add(returnDto.getJanuary());
                }
                if(returnDto.getFebruary() != null){
                    tempReturns.add(returnDto.getFebruary());
                }
                if(returnDto.getMarch() != null){
                    tempReturns.add(returnDto.getMarch());
                }
                if(returnDto.getApril() != null){
                    tempReturns.add(returnDto.getApril());
                }
                if(returnDto.getMay() != null){
                    tempReturns.add(returnDto.getMay());
                }
                if(returnDto.getJune() != null){
                    tempReturns.add(returnDto.getJune());
                }
                if(returnDto.getJuly() != null){
                    tempReturns.add(returnDto.getJuly());
                }
                if(returnDto.getAugust() != null){
                    tempReturns.add(returnDto.getAugust());
                }
                if(returnDto.getSeptember() != null){
                    tempReturns.add(returnDto.getSeptember());
                }
                if(returnDto.getOctober() != null){
                    tempReturns.add(returnDto.getOctober());
                }
                if(returnDto.getNovember() != null){
                    tempReturns.add(returnDto.getNovember());
                }
                if(returnDto.getDecember() != null){
                    tempReturns.add(returnDto.getDecember());
                }
            }
            double[] returnsArray = new double[tempReturns.size()];
            for(int i = 0; i < tempReturns.size(); i++){
                returnsArray[i] = tempReturns.get(i);
            }
            return returnsArray;
        }
        return null;
    }

    private Date[] getReturnDatesAsArray(List<ReturnDto> returns){
        if(returns != null){
            List<Date> tempReturns = new ArrayList<Date>();
            for(ReturnDto returnDto: returns){
                if(returnDto.getJanuary() != null){
                    tempReturns.add(DateUtils.getDate("31.01." + returnDto.getYear()));
                }
                if(returnDto.getFebruary() != null){
                    String date = DateUtils.isLeapYear(returnDto.getYear()) ? "29.02." + returnDto.getYear() : "28.02." + returnDto.getYear();
                    tempReturns.add(DateUtils.getDate(date));
                }
                if(returnDto.getMarch() != null){
                    tempReturns.add(DateUtils.getDate("31.03." + returnDto.getYear()));
                }
                if(returnDto.getApril() != null){
                    tempReturns.add(DateUtils.getDate("30.04." + returnDto.getYear()));
                }
                if(returnDto.getMay() != null){
                    tempReturns.add(DateUtils.getDate("31.05." + returnDto.getYear()));
                }
                if(returnDto.getJune() != null){
                    tempReturns.add(DateUtils.getDate("30.06." + returnDto.getYear()));
                }
                if(returnDto.getJuly() != null){
                    tempReturns.add(DateUtils.getDate("31.07." + returnDto.getYear()));
                }
                if(returnDto.getAugust() != null){
                    tempReturns.add(DateUtils.getDate("31.08." + returnDto.getYear()));
                }
                if(returnDto.getSeptember() != null){
                    tempReturns.add(DateUtils.getDate("30.09." + returnDto.getYear()));
                }
                if(returnDto.getOctober() != null){
                    tempReturns.add(DateUtils.getDate("31.10." + returnDto.getYear()));
                }
                if(returnDto.getNovember() != null){
                    tempReturns.add(DateUtils.getDate("30.11." + returnDto.getYear()));
                }
                if(returnDto.getDecember() != null){
                    tempReturns.add(DateUtils.getDate("31.12." + returnDto.getYear()));
                }
            }
            Date[] returnsArray = new Date[tempReturns.size()];
            for(int i = 0; i < tempReturns.size(); i++){
                returnsArray[i] = tempReturns.get(i);
            }
            return returnsArray;
        }
        return null;
    }

    private Double getAnnualizedReturn(List<ReturnDto> returns){
        if(returns != null){
            Double value = 1.0;
            int n = 0; // number of months
            for(ReturnDto returnDto: returns){
                if(returnDto.getJanuary() != null){
                    value *= 1 + returnDto.getJanuary() / 100;
                    n++;
                }
                if(returnDto.getFebruary() != null){
                    value *= 1 + returnDto.getFebruary() / 100;
                    n++;
                }
                if(returnDto.getMarch() != null){
                    value *= 1 + returnDto.getMarch() / 100;
                    n++;
                }
                if(returnDto.getApril() != null){
                    value *= 1 + returnDto.getApril() / 100;
                    n++;
                }
                if(returnDto.getMay() != null){
                    value *= 1 + returnDto.getMay() / 100;
                    n++;
                }
                if(returnDto.getJune() != null){
                    value *= 1 + returnDto.getJune() / 100;
                    n++;
                }
                if(returnDto.getJuly() != null){
                    value *= 1 + returnDto.getJuly() / 100;
                    n++;
                }
                if(returnDto.getAugust() != null){
                    value *= 1 + returnDto.getAugust() / 100;
                    n++;
                }
                if(returnDto.getSeptember() != null){
                    value *= 1 + returnDto.getSeptember() / 100;
                    n++;
                }
                if(returnDto.getOctober() != null){
                    value *= 1 + returnDto.getOctober() / 100;
                    n++;
                }
                if(returnDto.getNovember() != null){
                    value *= 1 + returnDto.getNovember() / 100;
                    n++;
                }
                if(returnDto.getDecember() != null){
                    value *= 1 + returnDto.getDecember() / 100;
                    n++;
                }
            }
            value = Math.pow(value, 12 / n) - 1;
            //return getRoundedValue(value);
            return value;
        }
        return null;
    }

    private Double getRoundedValue(Double value){
        if(value == null){
            return null;
        }
        int precision = 2;
        Double rounded = BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_UP).doubleValue();
        return rounded;
    }

    private Double getRoundedValue(BigDecimal value){
        if(value == null){
            return null;
        }
        int precision = 2;
        Double rounded = value.setScale(precision, RoundingMode.HALF_UP).doubleValue();
        return rounded;
    }

    private void scaleToNumeric(HedgeFundDto fundDto){
        // scale returns
        if(fundDto.getReturns() != null) {
            for (ReturnDto returnDto : fundDto.getReturns()) {
                returnDto.scaleReturnsToNumber();
            }
        }

        // return since inception
        fundDto.setReturnSinceInception(scaleValueToNumeric(fundDto.getReturnSinceInception()));

        // annualized return
        fundDto.setAnnualizedReturn(scaleValueToNumeric(fundDto.getAnnualizedReturn()));

        // YTD
        fundDto.setYTD(scaleValueToNumeric(fundDto.getYTD()));

        // annual volatility
        fundDto.setAnnualVolatility(scaleValueToNumeric(fundDto.getAnnualVolatility()));

        // worst drawdown
        fundDto.setWorstDrawdown(scaleValueToNumeric(fundDto.getWorstDrawdown()));

    }

    private void scaleToPercent(HedgeFundDto fundDto){
        // scale returns
        if(fundDto.getReturns() != null) {
            for (ReturnDto returnDto : fundDto.getReturns()) {
                returnDto.scaleReturnsToPercent();
            }
        }

        // return since inception
        fundDto.setReturnSinceInception(scaleValueToPercent(fundDto.getReturnSinceInception()));

        // annualized return
        fundDto.setAnnualizedReturn(scaleValueToPercent(fundDto.getAnnualizedReturn()));

        // YTD
        fundDto.setYTD(scaleValueToPercent(fundDto.getYTD()));

        // Beta
        fundDto.setBeta(getRoundedValue(fundDto.getBeta()));

        // annual volatility
        fundDto.setAnnualVolatility(scaleValueToPercent(fundDto.getAnnualVolatility()));

        // worst drawdown
        fundDto.setWorstDrawdown(scaleValueToPercent(fundDto.getWorstDrawdown()));

        // sharpe ratio
        fundDto.setSharpeRatio(getRoundedValue(fundDto.getSharpeRatio()));

        // sortino ratio
        fundDto.setSortinoRatio(getRoundedValue(fundDto.getSortinoRatio()));

    }

    private Double scaleValueToPercent(Double value){
        if(value == null){
            return null;
        }
        BigDecimal calculated = BigDecimal.valueOf(value).multiply(new BigDecimal("100"));
        return getRoundedValue(calculated);
    }

    private Double scaleValueToNumeric(Double value){
        if(value == null){
            return null;
        }
        BigDecimal calculated = MathUtils.divide(BigDecimal.valueOf(value), new BigDecimal("100"));
        return getRoundedValue(calculated);
    }

//    @Deprecated
//    private List<ReturnDto> formatBenchmarkToReturns(List<BenchmarkValueDto> benchmarks){
//        // ASSUMED benchmarks are sorted by date
//        // TODO: check they are sorted or sort them
//        List<ReturnDto> returnDtos = new ArrayList<>();
//        if(benchmarks != null){
//            int currentYear = 0;
//            ReturnDto returnDto = new ReturnDto();
//            for(BenchmarkValueDto benchmark: benchmarks){
//                int year = DateUtils.getYear(benchmark.getDate());
//                int month = DateUtils.getMonth(benchmark.getDate()) + 1;
//                if(currentYear != 0 && currentYear != year) {
//                    returnDtos.add(returnDto);
//                    returnDto = new ReturnDto();
//                }
//                returnDto.setYear(year);
//                returnDto.setReturnByMonth(month, benchmark.getReturnValue());
//                currentYear = year;
//            }
//            returnDtos.add(returnDto);
//        }
//        return returnDtos;
//    }

    @Override
    public List<HedgeFundDto> loadManagerFunds(Long managerId) {
        try {
            Page<HedgeFund> page = repository.findByManager(managerId,
                    new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id")));
            return this.converter.disassembleList(page.getContent());
        }catch (Exception ex){
            logger.error("Failed to load HF manager funds: manager=" + managerId, ex);
        }
        return null;
    }

    @Override
    public HedgeFundPagedSearchResult findByName(HedgeFundSearchParams searchParams) {
        try {
            int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
            Page<HedgeFund> entityPage = this.repository.findByName(searchParams.getName(),
                    //new PageRequest(searchParams.getPage(), searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "id")));
                    new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id")));

            HedgeFundPagedSearchResult result = new HedgeFundPagedSearchResult();
            if (entityPage != null) {
                result.setTotalElements(entityPage.getTotalElements());
                if (entityPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page + 1, result.getShowPageFrom(), entityPage.getTotalPages()));
                }
                result.setTotalPages(entityPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                result.setFunds(this.converter.disassembleList(entityPage.getContent()));
            }
            return result;
        }catch (Exception ex){
            logger.error("Failed to search HF funds", ex);
        }
        return null;
    }


    // TODO: temp, delete
    private void upload (String[] args){
        String csvFile = "C:/Users/magzumov/Desktop/3monthtbill.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                line = line.replace(" ", "");
                String[] array = line.split(cvsSplitBy);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = simpleDateFormat.parse(array[0]);
                //Double index = Double.parseDouble(array[1].replace(",", ".").trim());
                Double returnValue = array.length == 2 ? Double.parseDouble(array[1].replace(",", ".").trim()) : null;

                BenchmarkValue benchmarkValue = new BenchmarkValue();
                benchmarkValue.setDate(date);
                benchmarkValue.setReturnValue(returnValue);

                Benchmark benchmark = new Benchmark();
                benchmark.setId(2);

                benchmarkValue.setBenchmark(benchmark);

                this.benchmarkValueRepository.save(benchmarkValue);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
