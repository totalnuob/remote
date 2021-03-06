package kz.nicnbk.common.service.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.KthSelector;
import org.apache.commons.math3.util.MedianOf3PivotingStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by magzumov on 05.01.2017.
 */
public class MathUtils {

    public static final Double Z_SCORE_99_PERCENT = -2.32634787404084;

    public static final int DEFAULT_SCALE = MathUtils.SCALE_50;
    public static final int SCALE_50 = 50;
    public static final int SCALE_100 = 100;

    public static double calculateSlope(double[][] data){
        SimpleRegression simpleRegression = new SimpleRegression();
        simpleRegression.addData(data);
        return simpleRegression.getSlope();
    }

    public static double calculateSlope(double[][] data, boolean includeIntercept){
        SimpleRegression simpleRegression = new SimpleRegression(includeIntercept);
        simpleRegression.addData(data);
        return simpleRegression.getSlope();
    }

    public static BigDecimal divide(int scale, BigDecimal a, BigDecimal b){
        return a.divide(b, DEFAULT_SCALE, RoundingMode.HALF_UP).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal a, BigDecimal b){
        return a.divide(b, DEFAULT_SCALE, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
    public static Double divide(Double a, Double b){
        return divide(2, a, b);
    }
    public static Double divide(int scale, Double a, Double b){
        if(a == null || b == null || b.doubleValue() == 0.0){
            return null;
        }
        return divide(scale, new BigDecimal(a).setScale(scale, RoundingMode.HALF_UP),
                new BigDecimal(b).setScale(scale, RoundingMode.HALF_UP)).doubleValue();
    }

    public static Double multiply(Double value1, Double value2){
        return multiply(2, value1, value2);
    }
    public static Double multiply(int scale, Double value1, Double value2){
        if(value1 == null || value2 == null){
            return null;
        }
        if(value1 == 0.0 || value2 == 0.0){
            return 0.0;
        }

        BigDecimal calculated = new BigDecimal(value1).setScale(scale, RoundingMode.HALF_UP);
        calculated = calculated.multiply(new BigDecimal(value2).setScale(scale, RoundingMode.HALF_UP)).setScale(scale, RoundingMode.HALF_UP);
        return calculated.doubleValue();
    }

    public static BigDecimal sqrt(BigDecimal number){
        return sqrt(number, DEFAULT_SCALE);
    }
    public static BigDecimal sqrt(BigDecimal number, final int SCALE) {
        BigDecimal x0 = new BigDecimal("0").setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal x1 = new BigDecimal(Math.sqrt(number.doubleValue())).setScale(SCALE, RoundingMode.HALF_UP);
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = number.divide(x0, SCALE, BigDecimal.ROUND_HALF_UP);
            x1 = x1.add(x0).setScale(SCALE, RoundingMode.HALF_UP);
            x1 = x1.divide(BigDecimal.valueOf(2), SCALE, BigDecimal.ROUND_HALF_UP);

        }
        return x1;
    }

    public static Double subtract(Double a, Double b){
        return subtract(2, a, b);
    }
    public static Double subtract(int scale, Double a, Double b){
        a = a != null ? a : 0;
        b = b != null ? b : 0;
        return new BigDecimal(a).setScale(scale, RoundingMode.HALF_UP).subtract(new BigDecimal(b)
                .setScale(scale, RoundingMode.HALF_UP)).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }


//    public static Double add(Double a, Double b, Double c){
//        a = a != null ? a : 0;
//        b = b != null ? b : 0;
//        c = c != null ? c : 0;
//        return new BigDecimal(a).setScale(2, RoundingMode.HALF_UP).add(new BigDecimal(b).setScale(2, RoundingMode.HALF_UP))
//                .add(new BigDecimal(c).setScale(2, RoundingMode.HALF_UP)).doubleValue();
//    }

    public static Double add(Double... args){
        return add(2, args);
    }
    public static Double add(int scale, Double... args){
        BigDecimal sum = new BigDecimal("0");
        for(Double arg: args){
            BigDecimal value = arg != null ? new BigDecimal(arg).setScale(scale, RoundingMode.HALF_UP) : new BigDecimal("0");
            sum = add(scale, sum ,value);
        }
        return sum.doubleValue();
    }

//    public static BigDecimal add(BigDecimal a, BigDecimal b){
//        return a.setScale(2, RoundingMode.HALF_UP).add(b.setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
//    }

    public static BigDecimal add(BigDecimal... args){
        return add(2, args);
    }
    public static BigDecimal add(int scale, BigDecimal... args){
        BigDecimal sum = new BigDecimal("0");
        for(BigDecimal arg: args){
            sum = sum.add(arg.setScale(scale, RoundingMode.HALF_UP)).setScale(scale, RoundingMode.HALF_UP);
        }
        return sum;
    }

    public static int getRandomNumber(int from, int to) {
        Random r = new Random();
        return r.nextInt(to-from) + from;
    }

    public static int getRandomNumber() {
        return getRandomNumber(0, 1000);
    }

    /************************************************************************/

    public static Double getAnnualizedReturn(double[] returns, int scale){
        if(returns == null || returns.length == 0) {
            return null;
        }
        // ( 1 + r / 100)

        Double product = 1.0;
        for (int i = 0; i < returns.length; i++) {
            Double value = add(scale, returns[i], 1.0);
            product = multiply(scale, product, value);
        }
        Double returnValue = subtract(scale,Math.pow(product.doubleValue(), divide(scale, 12.0, returns.length + 0.0)), 1.0);
        return (new BigDecimal(returnValue).setScale(scale, RoundingMode.HALF_UP)).doubleValue();

    }

    public static Double getSharpeRatio(int scale, double[] returns, double[] tbills, boolean isBiasCorrected){
        Double annReturns = getAnnualizedReturn(returns, scale);
        Double annTbills = getAnnualizedReturn(tbills, scale);
        Double annSTD = MathUtils.multiply(scale, getStandardDeviation(returns, isBiasCorrected), Math.sqrt(12));
        return MathUtils.divide(scale, MathUtils.subtract(scale, annReturns, annTbills), annSTD);
    }

    public static Double getSharpeRatioAvg12MReturns(int scale, double[] returns, double[] tbills, boolean isBiasCorrected){
        if(returns == null || returns.length == 0 || tbills == null || tbills.length == 0){
            return null;
        }
        Double sumReturns = 0.0;
        for(int i = 0; i < returns.length; i++){
            sumReturns = MathUtils.add(scale, sumReturns, returns[i]);
        }
        Double avgReturns = MathUtils.divide(scale, sumReturns, new Double(returns.length));
        Double annTbills = getAnnualizedReturn(tbills, scale);
        Double annSTD = MathUtils.multiply(scale, getStandardDeviation(returns, isBiasCorrected), Math.sqrt(12));
        //return MathUtils.divide(scale, MathUtils.subtract(scale, annAvgReturns, annTbills), annSTD);
        return MathUtils.divide(scale, MathUtils.subtract(scale, MathUtils.multiply(scale, avgReturns, 12.0), annTbills), annSTD);
    }

    public static Double getSortinoRatio(Double fundAnnualizedReturn, Double benchmarkAnnualizedReturn, double[] returns, int scale){
        if(returns == null || returns.length == 0 || fundAnnualizedReturn == null || benchmarkAnnualizedReturn == null) {
            return null;
        }
        Double sumNegativeReturns = 0.0;
        int count = 0;
        for(int i = 0; i < returns.length; i++){
            if(returns[i] < 0){
                sumNegativeReturns = add(scale, sumNegativeReturns, multiply(scale, multiply(scale, returns[i], 100.0), multiply(scale, returns[i], 100.0)));
                count++;
            }

        }
        double divider = Math.max(0.01, multiply(scale, Math.sqrt(sumNegativeReturns.doubleValue()), Math.sqrt(MathUtils.divide(scale, 12.0, returns.length + 0.0))));
        double value = divide(scale, subtract(scale, fundAnnualizedReturn, benchmarkAnnualizedReturn), divider);
        //return getRoundedValue(value);
        value = MathUtils.multiply(scale, value, 100.0);
        return (new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP)).doubleValue();
    }

    public static Double getSortinoRatio2(double[] returns, double[] riskFreeReturns, int scale){
        if(returns == null || returns.length == 0 || riskFreeReturns == null || riskFreeReturns.length == 0) {
            return null;
        }

        Double meanReturn = getMean(returns);
        Double meanRiskFree = getMean(riskFreeReturns);

        Double sum = 0.0;
        for(int i = 0; i < returns.length; i++){
            //Double diff = subtract(scale, returns[i], riskFreeReturns[i]);
            if(returns[i] < 0){
                sum =  add(scale, sum, positivePower(scale, returns[i], 2));
            }
        }
        Double divider = Math.sqrt(divide(scale, sum, returns.length + 0.0));
        Double value = divide(scale, subtract(scale, meanReturn, meanRiskFree), divider);
        return (new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP)).doubleValue();
    }

    public static Double getSortinoRatioAvgReturns(Double benchmarkAnnualizedReturn, double[] returns, int scale){
        if(returns == null || returns.length == 0 || benchmarkAnnualizedReturn == null) {
            return null;
        }
        Double sumReturns = 0.0;
        for(int i = 0; i < returns.length; i++){
            sumReturns = MathUtils.add(scale, sumReturns, returns[i]);
        }
        Double avgReturns = MathUtils.divide(scale, sumReturns, new Double(returns.length));
        double[] negativeReturns = new double[returns.length];
        for(int i = 0; i < returns.length; i++){
            negativeReturns[i] = returns[i] < 0 ? returns[i] : 0.0;
        }
        Double annSTD = MathUtils.multiply(scale, getStandardDeviation(negativeReturns, true), Math.sqrt(12));
        //Double value = MathUtils.divide(scale, MathUtils.subtract(scale, annAvgReturns, benchmarkAnnualizedReturn), annSTD);
        Double value = MathUtils.divide(scale, MathUtils.subtract(scale, MathUtils.multiply(scale, avgReturns, 12.0), benchmarkAnnualizedReturn), annSTD);
        return value;
    }

    /**
     * Calculates beta using SLOPE() function from apache-commons-math.
     *
     * @param fundReturns
     * @param benchmarkReturns
     * @return
     */
    public static Double getBeta(double[] fundReturns, double[] benchmarkReturns, int scale){

        if(fundReturns == null  || fundReturns.length == 0 || benchmarkReturns == null || benchmarkReturns.length == 0){
            return null;
        }

        // TODO: handle - check benchmarks array size no less than returns size
        if(fundReturns.length != benchmarkReturns.length){
            return null;
        }

        // prepare data
        double[][] data = new double[fundReturns.length][2];
        for(int i = 0; i < fundReturns.length; i++){
            data[i][1] = fundReturns[i];
            data[i][0] = benchmarkReturns[i];
        }

        // call slope function
        double value = MathUtils.calculateSlope(data);
        return value;
//        double newValue = (new BigDecimal(value).setScale(1, RoundingMode.HALF_UP)).doubleValue();
//        if(newValue == 0.0){
//            newValue = value > 0 ? 0.1 : -0.1;
//        }
//        return newValue;
    }

    public static Double getBeta2(double[] fundReturns, double[] benchmarkReturns, double[] riskFreeReturns, int scale){

        if(fundReturns == null  || fundReturns.length == 0 || benchmarkReturns == null || benchmarkReturns.length == 0  ||
                riskFreeReturns == null || riskFreeReturns.length == 0){
            return null;
        }

        // TODO: handle - check benchmarks array size no less than returns size
        if(fundReturns.length != benchmarkReturns.length || fundReturns.length != riskFreeReturns.length){
            return null;
        }

        // prepare data
        double[][] data = new double[fundReturns.length][2];
        for(int i = 0; i < fundReturns.length; i++){
            data[i][1] = subtract(scale, fundReturns[i], riskFreeReturns[i]);
            data[i][0] = subtract(scale, benchmarkReturns[i], riskFreeReturns[i]);
        }

        // call slope function
        double value = MathUtils.calculateSlope(data);
        return value;
    }

    public static Double getAlpha(int scale, double[] returns, double[] riskfree, double[] indices, double beta){
        if(returns == null || riskfree == null || indices == null || returns.length == 0
                || returns.length == 0 || riskfree.length == 0 || indices.length == 0
                || returns.length != riskfree.length ||  indices.length != riskfree.length){
            return null;
        }

//        double returnSum = 0.0;
//        double riskfreeSum = 0.0;
//        double indicesSum = 0.0;
//        int scale = 4;
//        for(int i = 0; i < returns.length; i++){
//            returnSum = add(scale, returnSum, returns[i]);
//            riskfreeSum = add(scale, riskfreeSum, riskfree[i]);
//            indicesSum = add(scale, indicesSum, indices[i]);
//        }
//
//        double meanReturn = divide(scale, returnSum, returns.length + 0.0);
//        double meanRiskfree = divide(scale, riskfreeSum, riskfree.length + 0.0);
//        double meanIndices = divide(scale, indicesSum, indices.length + 0.0);

        double meanReturn = getMean(returns);
        double meanRiskfree = getMean(riskfree);
        double meanIndices = getMean(indices);

        meanReturn = new BigDecimal(meanReturn).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        meanRiskfree = new BigDecimal(meanRiskfree).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        meanIndices = new BigDecimal(meanIndices).setScale(scale, RoundingMode.HALF_UP).doubleValue();

        double a = add(scale, 1.0, subtract(scale, meanReturn, meanRiskfree));
        double b = multiply(scale, beta, subtract(scale, meanIndices, meanRiskfree));
        double result = subtract(scale, a, b);
        result = subtract(scale, positivePower(scale, result, 12), 1.0);
        return result;
    }

    public static Double getAlpha2(int scale, double[] returns, double[] riskfree, double[] indices, double beta){
        if(returns == null || riskfree == null || indices == null || returns.length == 0
                || returns.length == 0 || riskfree.length == 0 || indices.length == 0
                || returns.length != riskfree.length ||  indices.length != riskfree.length){
            return null;
        }

        double meanReturn = getMean(returns);
        double meanRiskfree = getMean(riskfree);
        double meanIndices = getMean(indices);

        meanReturn = new BigDecimal(meanReturn).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        meanRiskfree = new BigDecimal(meanRiskfree).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        meanIndices = new BigDecimal(meanIndices).setScale(scale, RoundingMode.HALF_UP).doubleValue();

        double a = subtract(scale, meanReturn, meanRiskfree);
        double b = multiply(scale, beta, subtract(scale, meanIndices, meanRiskfree));
//        double result = multiply(scale, subtract(scale, a, b), 12.0);
        double result = subtract(scale, a, b);
        return result;
    }

    public static Double positivePower(int scale, Double a, int power){
        if(power < 0.0){
            return null;
        }else if(power == 0){
            return multiply(scale, a, 1.0);
        }else{
            Double result = a;
            while(power > 1){
                result = multiply(scale, result, a);
                power--;
            }
            return result;
        }
    }

    public static Double getOmega(int scale, double[] returns){
        if(returns != null && returns.length > 0){
            double totalPositive = 0.0;
            double totalNegative = 0.0;
            for(int i = 0; i < returns.length; i++){
                if(returns[i] > 0){
                    totalPositive++;
                }else if(returns[i] < 0){
                    totalNegative++;
                }
            }
            double a = divide(scale, totalPositive, returns.length + 0.0);
            double b = divide(scale, totalNegative, returns.length + 0.0);
            return divide(scale, a, Math.max(b,0.0001));
        }
        return null;
    }

    public static Double getCFVar(int scale, double[] returns, Double zScore){
        Double meanReturn = getMean(returns);
        Double std = getStandardDeviation(returns);
        Double skew = getSkew(returns);
        Double kurtosis = getKurtosis(returns);

        if(skew == null){
            return null;
        }
        if(kurtosis == null){
            return null;
        }

        meanReturn = new BigDecimal(meanReturn).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        std = new BigDecimal(std).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        skew = new BigDecimal(skew).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        kurtosis = new BigDecimal(kurtosis).setScale(scale, RoundingMode.HALF_UP).doubleValue();


        Double part1 = divide(scale, multiply(scale, subtract(scale, Math.pow(zScore, 2), 1.0), skew), 6.0);
        Double part2 = divide(scale, multiply(scale, subtract(scale, Math.pow(zScore, 3), multiply(scale, 3.0, zScore)), kurtosis), 24.0);
        Double part3 = divide(scale, multiply(scale, subtract(scale, multiply(2.0, Math.pow(zScore, 3)), multiply(scale, 5.0, zScore)), Math.pow(skew, 2)), -36.0);
        Double cfScore = add(scale, part1, part2, part3, zScore);

        return add(scale, meanReturn, multiply(scale, cfScore, multiply(scale, std, sqrt(new BigDecimal(12.0), scale).doubleValue())));
    }

    public static Double getCFVar2(int scale, double[] returns, Double zScore){
        Double meanReturn = getMean(returns);
        Double std = getStandardDeviation(returns);
        Double skew = getSkew(returns);
        Double kurtosis = getKurtosis(returns);

        if(skew == null){
            return null;
        }
        if(kurtosis == null){
            return null;
        }

        meanReturn = new BigDecimal(meanReturn).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        std = new BigDecimal(std).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        skew = new BigDecimal(skew).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        kurtosis = new BigDecimal(kurtosis).setScale(scale, RoundingMode.HALF_UP).doubleValue();


        Double part1 = divide(scale, multiply(scale, subtract(scale, Math.pow(zScore, 2), 1.0), skew), 6.0);
        Double part2 = divide(scale, multiply(scale, subtract(scale, Math.pow(zScore, 3), multiply(scale, 3.0, zScore)), kurtosis), 24.0);
        Double part3 = divide(scale, multiply(scale, subtract(scale, multiply(2.0, Math.pow(zScore, 3)), multiply(scale, 5.0, zScore)), Math.pow(skew, 2)), -36.0);
        Double cfScore = add(scale, part1, part2, part3, zScore);

        //return subtract(scale, meanReturn, multiply(scale, cfScore, std));
        return add(scale, meanReturn, multiply(scale, cfScore, std));
    }

    public static Double getSkew(double[] values){
        Skewness skewness = new Skewness();
        Double skew = skewness.evaluate(values);
        return skew.isNaN() ?  null : skew;
    }

    public static Double getStandardDeviation(double[] values){
        StandardDeviation standardDeviation = new StandardDeviation(false);
        return standardDeviation.evaluate(values);
    }

    public static Double getStandardDeviation(double[] values, boolean isBiasCorrected){
        StandardDeviation standardDeviation = new StandardDeviation(isBiasCorrected);
        return standardDeviation.evaluate(values);
    }

    public static Double getAnnualizedDownsideDeviation(int scale, double[] values){
        if(values == null || values.length == 0){
            return null;
        }
//        List<Double> negatives = new ArrayList<>();
//        for(int i = 0; i < values.length; i++){
//            if(values[i] < 0){
//                negatives.add(values[i]);
//            }
//        }
        double[] returns = new double[values.length];
        for(int i = 0; i < returns.length; i++){
            returns[i] = values[i] < 0 ? values[i] : 0.0;
        }
        return MathUtils.multiply(scale, getStandardDeviation(returns, true), Math.sqrt(12));
    }

    public static Double getMean(double[] values){
        Mean mean = new Mean();
        return mean.evaluate(values);
    }

    public static Double getKurtosis(double[] values){
        Kurtosis kurtosis = new Kurtosis();
        Double kurtosisValue = kurtosis.evaluate(values);
        return kurtosisValue.isNaN() ? null : kurtosisValue;
    }

    public static Double getPercentile(double[] values, double p){
        Percentile percentile = new Percentile();
        Double value = percentile.evaluate(values, p);
        return value.isNaN()? null : value;
    }
    public static Double getPercentileExcel(double[] values, double p){
        Percentile percentile = new PercentileExcel();
        Double value = percentile.evaluate(values, p);
        return value.isNaN()? null : value;
    }

    private static class PercentileExcel extends Percentile {
        public PercentileExcel() throws MathIllegalArgumentException {

            super(50.0,
                    EstimationType.R_7, // use excel style interpolation
                    NaNStrategy.REMOVED,
                    new KthSelector(new MedianOf3PivotingStrategy()));
        }
    }

    public static Double getDouble(String value){
        try{
           return Double.parseDouble(value);
        }catch (Exception ex){
            return null;
        }
    }

    public static Double[] getCumulativeReturns(int scale, Double[] returns){
        if(returns == null){
            return null;
        }
        Double[] cumulativeReturns = new Double[returns.length];
        for(int i = 0; i < returns.length; i++){
            if(i == 0){
                cumulativeReturns[i] = returns[i];
            }else{
                double value = MathUtils.subtract(scale, cumulativeReturns[i - 1], 1.0);
                value = MathUtils.multiply(scale, MathUtils.add(scale, returns[i], 1.0), value);
                cumulativeReturns[i] = MathUtils.subtract(scale, value, 1.0);
            }
        }
        return cumulativeReturns;
    }

    public static double[] getCumulativeReturnsFromInitial(int scale, double[] returns, Double initial){
        if(returns == null){
            return null;
        }
        double[] cumulativeReturns = new double[returns.length];
        Double previous = initial;
        for(int i = 0; i < returns.length; i++){
            Double value = MathUtils.multiply(scale, MathUtils.add(scale, returns[i], 1.0), previous);
            cumulativeReturns[i] = value;

            previous = value;
        }
        return cumulativeReturns;
    }

    public static double[] getCumulativeReturnsFromInitial(double[] returns, Double initial){
        return getCumulativeReturnsFromInitial(returns, initial);
    }


    public static Double[] getCumulativeReturns(Double[] returns){
        return getCumulativeReturns(2, returns);
    }

    public static Double getCumulativeReturn(int scale, Double previousCumulative, Double currentValue){
        if(previousCumulative == null || previousCumulative.doubleValue() == 0.0){
            return currentValue;
        }
        double value = MathUtils.add(scale, previousCumulative, 1.0);
        value = MathUtils.multiply(scale, MathUtils.add(scale, currentValue, 1.0), value);
        return MathUtils.subtract(scale, value, 1.0);
    }

    public static Double getCumulativeReturn(int scale, Double[] returns){
        if(returns == null || returns.length == 0){
            return null;
        }
        Double cumulative = 1.0;
        for(Double r: returns){
            cumulative = MathUtils.multiply(scale, cumulative, MathUtils.add(scale, 1.0, r));
        }
        return MathUtils.subtract(scale, cumulative, 1.0);

    }

    public static Double getCumulativeReturn(Double[] returns){
        return getCumulativeReturn(2, returns);
    }

    public static Double getCumulativeReturn(Double previousCumulative, Double currentValue){
        return getCumulativeReturn(2, previousCumulative, currentValue);
    }

    public static WorstDrawdownDto getWorstDrawdown(int scale, double[] cumulativeReturns){
        WorstDrawdownDto worstDDDto = new WorstDrawdownDto();
        double[] calculatedValues = new double[cumulativeReturns.length];
        if(cumulativeReturns != null && cumulativeReturns.length > 0) {
            Double worstDrawdown = null;
            Double drawdownMin = null;
            int drowdownIndex = -1;
            for (int i = 0; i < cumulativeReturns.length; i++) {
                Double minValue = null;
                for (int j = i; j < cumulativeReturns.length; j++) {
                    if(minValue == null || cumulativeReturns[j] < minValue.doubleValue()){
                        minValue = cumulativeReturns[j];
                    }
                }
                if(minValue != null && i > 0) {
                    Double value = MathUtils.subtract(scale, MathUtils.divide(scale, minValue, cumulativeReturns[i - 1]), 1.0);
                    calculatedValues[i] = value;
                    //System.out.println(cumulativeReturns[i] + "," + minValue + "," + value);
                    if (worstDrawdown == null || value.doubleValue() < worstDrawdown.doubleValue()) {
                        worstDrawdown = value;
                        drawdownMin = minValue;
                        drowdownIndex = i;
                    }
                }else{
                    calculatedValues[i] = 0.0;
                }
            }
            worstDDDto.setWorstDDValue(worstDrawdown);

            if(drowdownIndex >= 0) {
                Integer drawdownPeriod = null;
                Double minValue = null;
                int minValueIndex = -1;
                for (int i = drowdownIndex; i < cumulativeReturns.length; i++) {
                    if (minValue == null || cumulativeReturns[i] < minValue.doubleValue()) {
                        minValue = cumulativeReturns[i];
                        minValueIndex = i;
                    }
                }
                if(minValueIndex >= 0){
                    drawdownPeriod = minValueIndex - drowdownIndex + 1;
                }
                worstDDDto.setWorstDDPeriod(drawdownPeriod);
            }
            if(drawdownMin != null && drowdownIndex > 0){
                int recoveryMonth = 0;
                boolean startRecovery = false;
                for(int i = 0; i < cumulativeReturns.length; i++){
                    if(startRecovery){
                        recoveryMonth++;
                        if(cumulativeReturns[i] > cumulativeReturns[drowdownIndex - 1]){
                            break;
                        }
                    }else if(cumulativeReturns[i] == drawdownMin.doubleValue()){
                        startRecovery = true;
                    }
                }
                worstDDDto.setRecoveryMonths(recoveryMonth);
            }

            return worstDDDto;

        }
        return null;
    }

//    public static Integer getRecoveryMonths(int scale, double[] returns){
//        if(returns != null && returns.length > 0) {
//        }
//        return null;
//    }

    public static Double abs(Double value){
        return value != null ? Math.abs(value) : null;
    }


    public static void main (String[] args){
        testSlope1();
        testSlope2();
    }

    private static void testSlope1(){
        // 0.03
        double[][] data = {
                {1.05,0.66},
                {-2.10,1.11},
                {1.97,2.48},
                {-6.26,0.71},
                {-2.64,-0.91},
                {8.30,0.12},
                {0.05,0.38},
                { -1.75,1.37},
                {-5.07,-1.84},
                {-0.41, -4.59},
                {6.60,-0.89},
                {0.27,1.69}
        };
        System.out.println(calculateSlope(data));
    }

    private static void testSlope2(){
        // 0.11
        double[][] data = {
                {1.05, 1.51},
                {-2.10, -1.00},
                {1.97, -1.61},
                {-6.26, -0.60},
                {-2.64, -3.49},
                {8.30, -1.45},
                {0.05, 1.01},
                { -1.75, -0.67},
                {-5.07, -3.37},
                {-0.41, -2.05},
                {6.60, -0.05},
                {0.27, 2.30}};
        System.out.println(calculateSlope(data));
    }
}
