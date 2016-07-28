package kz.nicnbk.common.service.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility class for decimal numbers.
 *
 * Created by magzumov on 14.06.2016.
 */
public class DecimalUtils {

    /**
     * Returns decimal number string representation.
     * Format: 1 decimal point with '.' separator
     * E.g. 1.0, 12.2
     *
     * @param value - double value
     * @return
     */
    public static String format(Double value){
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.#", otherSymbols);
        return df.format(value);
    }
}
