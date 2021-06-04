package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_parsed_params_data")
public class HedgeFundScreeningParsedParamData extends BaseEntity {

    private HedgeFundScreening screening;

    private String fundName;
    private Double omega;
    private Double annualizedReturn;
    private Double sortino;
    private Double alpha;
    private Double beta;
    private Double cfVar;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="screening_id")
    public HedgeFundScreening getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreening screening) {
        this.screening = screening;
    }

    @Column(name="fund_name", nullable = false)
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name="omega")
    public Double getOmega() {
        return omega;
    }

    public void setOmega(Double omega) {
        this.omega = omega;
    }

    @Column(name="ann_return")
    public Double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(Double annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    public Double getSortino() {
        return sortino;
    }

    public void setSortino(Double sortino) {
        this.sortino = sortino;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    @Column(name="cf_var")
    public Double getCfVar() {
        return cfVar;
    }

    public void setCfVar(Double cfVar) {
        this.cfVar = cfVar;
    }
}
