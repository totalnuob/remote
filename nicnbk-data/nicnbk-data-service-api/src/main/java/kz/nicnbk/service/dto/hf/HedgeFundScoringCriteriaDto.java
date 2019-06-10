package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.MathUtils;

/**
 * Created by magzumov on 30.05.2019.
 */
@Deprecated
public class HedgeFundScoringCriteriaDto implements BaseDto {
    Double minAnnualizedReturn = null;
    Double maxAnnualizedReturn = null;
    Double minSortino = null;
    Double maxSortino = null;
    Double minBeta = null;
    Double maxBeta = null;
    Double minAlpha = null;
    Double maxAlpha = null;
    Double maxOmega = null;
    Double minOmega = null;
    Double minCFVar = null;
    Double maxCFVar = null;

    public Double getMinAnnualizedReturn() {
        return minAnnualizedReturn;
    }

    public void setMinAnnualizedReturn(Double minAnnualizedReturn) {
        this.minAnnualizedReturn = minAnnualizedReturn;
    }

    public Double getMaxAnnualizedReturn() {
        return maxAnnualizedReturn;
    }

    public void setMaxAnnualizedReturn(Double maxAnnualizedReturn) {
        this.maxAnnualizedReturn = maxAnnualizedReturn;
    }

    public Double getMinSortino() {
        return minSortino;
    }

    public void setMinSortino(Double minSortino) {
        this.minSortino = minSortino;
    }

    public Double getMaxSortino() {
        return maxSortino;
    }

    public void setMaxSortino(Double maxSortino) {
        this.maxSortino = maxSortino;
    }

    public Double getMinBeta() {
        return minBeta;
    }

    public void setMinBeta(Double minBeta) {
        this.minBeta = minBeta;
    }

    public Double getMaxBeta() {
        return maxBeta;
    }

    public void setMaxBeta(Double maxBeta) {
        this.maxBeta = maxBeta;
    }

    public Double getMinAlpha() {
        return minAlpha;
    }

    public void setMinAlpha(Double minAlpha) {
        this.minAlpha = minAlpha;
    }

    public Double getMaxAlpha() {
        return maxAlpha;
    }

    public void setMaxAlpha(Double maxAlpha) {
        this.maxAlpha = maxAlpha;
    }

    public Double getMaxOmega() {
        return maxOmega;
    }

    public void setMaxOmega(Double maxOmega) {
        this.maxOmega = maxOmega;
    }

    public Double getMinOmega() {
        return minOmega;
    }

    public void setMinOmega(Double minOmega) {
        this.minOmega = minOmega;
    }

    public Double getMinCFVar() {
        return minCFVar;
    }

    public void setMinCFVar(Double minCFVar) {
        this.minCFVar = minCFVar;
    }

    public Double getMaxCFVar() {
        return maxCFVar;
    }

    public void setMaxCFVar(Double maxCFVar) {
        this.maxCFVar = maxCFVar;
    }

    public int getPercentileRatioAnnReturn(Double value){
        return getPercentileRatio(value, this.maxAnnualizedReturn, this.minAnnualizedReturn);
    }

    public int getPercentileRatioSortino(Double value){
        return getPercentileRatio(value, this.maxSortino, this.minSortino);
    }

    public int getPercentileRatioBeta(Double value){
        return getPercentileRatio(value, this.maxBeta, this.minBeta);
    }

    public int getPercentileRatioAlpha(Double value) {
        return getPercentileRatio(value, this.maxAlpha, this.minAlpha);
    }

    public int getPercentileRatioOmega(Double value){
        return getPercentileRatio(value, this.maxOmega, this.minOmega);
    }

    public int getPercentileRatioCFVar(Double value){
        return getPercentileRatio(value, this.maxCFVar, this.minCFVar);
    }

    private int getPercentileRatio(Double value, Double max, Double min){
        double step = MathUtils.divide(MathUtils.subtract(max, min), 10.0);
        double current = min;
        int ratio = 1;
        boolean stop = false;
        while(!stop){
            if(value < current + step){
                return ratio;
            }else{
                ratio ++;
                current += step;
            }
        }
        return 0;
    }
}
