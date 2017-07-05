package kz.nicnbk.common.service.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class ArrayUtils {

    public static void addArrayValues(double[] sourceArray, double[] values){
        if(sourceArray == null || values == null || sourceArray.length != values.length){
            throw new IllegalArgumentException("Arrays addition is impossible: array is null or array size is different");
        }
        for(int i = 0; i < sourceArray.length; i++){
            sourceArray[i] += values[i];
        }
    }

    public static void addArrayValues(Double[] sourceArray, Double[] values){
        if(sourceArray == null || values == null || sourceArray.length != values.length){
            throw new IllegalArgumentException("Arrays addition is impossible: array is null or array size is different");
        }
        for(int i = 0; i < sourceArray.length; i++){
            if(sourceArray[i] != null && values[i] != null){
                sourceArray[i] += values[i];
            }
        }
    }
}
