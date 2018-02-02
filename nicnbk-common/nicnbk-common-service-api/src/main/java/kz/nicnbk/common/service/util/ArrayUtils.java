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
                sourceArray[i] += values[i].doubleValue();
            }
        }
    }

    public static Double sumArray(Double[] values){
        if(values == null){
            return null;
        }
        Double sum = 0.0;
        for(Double value: values){
            sum += value != null ? value.doubleValue() : 0.0;
        }
        return sum;
    }

    public static Double sumArray(Double[] values, int from, int to){
        if(values == null){
            return null;
        }else if(from < 0 || to < 0 || from > values.length - 1 || to >= values.length){
            throw new IllegalArgumentException("Arrays addition is impossible: array indices are invalid: " + from + ", " + to);
        }
        Double sum = 0.0;
        for(int i = from; i <= to; i++){
            Double value = values[i];
            sum += value != null ? value.doubleValue() : 0.0;
        }
        return sum;
    }
}
