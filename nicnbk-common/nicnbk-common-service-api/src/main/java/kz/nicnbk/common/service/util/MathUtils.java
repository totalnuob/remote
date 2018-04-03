package kz.nicnbk.common.service.util;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Created by magzumov on 05.01.2017.
 */
public class MathUtils {

    public static final int DEFAULT_SCALE = MathUtils.SCALE_50;
    public static final int SCALE_50 = 50;
    public static final int SCALE_100 = 100;

    public static double calculateSlope(double[][] data){
        SimpleRegression simpleRegression = new SimpleRegression();
        simpleRegression.addData(data);
        return simpleRegression.getSlope();
    }

    public static BigDecimal divide(BigDecimal a, BigDecimal b){
        return a.divide(b, DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(Double a, Double b){
        return divide(new BigDecimal(a).setScale(2, RoundingMode.HALF_UP),
                new BigDecimal(b).setScale(2, RoundingMode.HALF_UP));
    }

    public static Double multiply(Double value1, Double value2){
        if(value1 == null || value2 == null){
            return null;
        }
        if(value1 == 0.0 || value2 == 0.0){
            return 0.0;
        }

        BigDecimal calculated = new BigDecimal(value1).setScale(2, RoundingMode.HALF_UP);
        calculated = calculated.multiply(new BigDecimal(value2).setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        return calculated.doubleValue();
    }

    public static BigDecimal sqrt(BigDecimal number){
        return sqrt(number, DEFAULT_SCALE);
    }

    public static BigDecimal sqrt(BigDecimal number, final int SCALE) {
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = new BigDecimal(Math.sqrt(number.doubleValue()));
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = number.divide(x0, SCALE, BigDecimal.ROUND_HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(BigDecimal.valueOf(2), SCALE, BigDecimal.ROUND_HALF_UP);

        }
        return x1;
    }

    public static Double subtract(Double a, Double b){
        a = a != null ? a : 0;
        b = b != null ? b : 0;
        return new BigDecimal(a).setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(b).setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


//    public static Double add(Double a, Double b, Double c){
//        a = a != null ? a : 0;
//        b = b != null ? b : 0;
//        c = c != null ? c : 0;
//        return new BigDecimal(a).setScale(2, RoundingMode.HALF_UP).add(new BigDecimal(b).setScale(2, RoundingMode.HALF_UP))
//                .add(new BigDecimal(c).setScale(2, RoundingMode.HALF_UP)).doubleValue();
//    }

    public static Double add(Double... args){
        BigDecimal sum = new BigDecimal("0");
        for(Double arg: args){
            BigDecimal value = arg != null ? new BigDecimal(arg).setScale(2, RoundingMode.HALF_UP) : new BigDecimal("0");
            sum = add(sum ,value);
        }
        return sum.doubleValue();
    }

//    public static BigDecimal add(BigDecimal a, BigDecimal b){
//        return a.setScale(2, RoundingMode.HALF_UP).add(b.setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
//    }

    public static BigDecimal add(BigDecimal... args){
        BigDecimal sum = new BigDecimal("0");
        for(BigDecimal arg: args){
            sum = sum.add(arg.setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
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
