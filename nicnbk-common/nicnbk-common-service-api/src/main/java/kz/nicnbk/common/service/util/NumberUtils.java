package kz.nicnbk.common.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility class for number operations.
 *
 * Created by magzumov on 14.06.2016.
 */
public class NumberUtils {

    static final long MILLION = 1000000L;
    static final long BILLION = 1000000000L;
    static final long TRILLION = 1000000000000L;

    public static String truncateNumber(double x, int scale) {
        double value = 0;
        if(x < MILLION){
            value = x;
        }else if(x < BILLION){
            value = x / MILLION;
            return getBigDecimal(value).setScale(scale, RoundingMode.HALF_UP).doubleValue() + " M";
        }else if(x < TRILLION){
            value = x / BILLION;
            return getBigDecimal(value).setScale(scale, RoundingMode.HALF_UP).doubleValue() + " B";
        }else {
            value = x / TRILLION;
            return getBigDecimal(value).setScale(scale, RoundingMode.HALF_UP).doubleValue() + " T";
        }
        return "";
    }

    public static double getDouble(Double value){
        return value != null ? value.doubleValue() : 0.0;
    }

    public static double sumDouble(Double a, Double b){
        return getDouble(a) + getDouble(b);
    }

    public static boolean isEqualValues(Double a, Double b){
        return a == b || (a != null && b != null && a.doubleValue() == b.doubleValue());
    }

    public static boolean isNotEqualValues(Double a, Double b){
        return !isEqualValues(a, b);
    }

    /**
     * Returns BigDecimal instance from the given double value.
     * If value is null, then returns Big Decimal of 0.
     * Sets scale=2 and ROUND_HALF_UP rounding.
     *
     * @param value - double value
     * @return - BigDecimal
     */
    public static BigDecimal getBigDecimal(Double value){
        return new BigDecimal(NumberUtils.getDouble(value)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
