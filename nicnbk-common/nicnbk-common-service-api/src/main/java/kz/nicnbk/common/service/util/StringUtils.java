package kz.nicnbk.common.service.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class StringUtils {
    // TODO: refactor from here
    public static final String VALUE_NONE = "NONE";

    /**
     * Returns true if string is null or empty, false otherwise.
     *
     * @param value - string
     * @return - true/false
     */
    public static boolean isEmpty(String value){
        return value == null || value.trim().equals("");
    }

    /**
     * Returns true if string is null or empty, false otherwise.
     *
     * @param value - string
     * @return - true/false
     */
    public static boolean isNotEmpty(String value){
        return !isEmpty(value);
    }

    /**
     * Returns false is string is empty or value is VALUE_NONE, true otherwise.
     *
     * @param value - string
     * @return
     */
    public static boolean isValue(String value){
        return isNotEmpty(value) && !value.equals(StringUtils.VALUE_NONE);
    }

    /**
     * Returns a list of strings from the string array.
     * E.g. ["a", "b", "c"] into java.util.List object containing three strings in the original order.
     * @param listAsText
     * @return
     */
    public static List<String> assembleStringList(String listAsText){
        List<String> textAsList = new ArrayList<>();
        if(listAsText != null && listAsText.length() > 0){
            listAsText = listAsText.replace("[", "");
            listAsText = listAsText.replace("]", "");
            String[] parts = listAsText.split(",");
            for(int i = 0; i < parts.length; i++){
                textAsList.add(parts[i].trim());
            }
        }
        return textAsList;
    }
}
