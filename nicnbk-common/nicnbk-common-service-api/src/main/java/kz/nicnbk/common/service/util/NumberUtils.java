package kz.nicnbk.common.service.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility class for number operations.
 *
 * Created by magzumov on 14.06.2016.
 */
public class NumberUtils {

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
}
