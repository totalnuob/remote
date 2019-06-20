package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.monitoring.LiquidPortfolio;

import java.util.Date;

/**
 * Created by Pak on 20.06.2019.
 */

public class LiquidPortfolioDto extends BaseEntityDto<LiquidPortfolio> {

    private Date date;

    private Double totalFixed;
    private Double governmentsFixed;
    private Double corporates;
    private Double agencies;
    private Double supranationals;
    private Double currency;
    private Double options;
    private Double cashFixed;
    private Double cashBrokerAndFutures;

    private Double totalEquity;
    private Double cashEquity;
    private Double etf;

    private Double totalTransition;
    private Double cashTransition;
    private Double governmentsTransition;

    public LiquidPortfolioDto() {
    }

    public LiquidPortfolioDto(Date date, Double totalFixed, Double governmentsFixed, Double corporates, Double agencies, Double supranationals, Double currency, Double options, Double cashFixed, Double cashBrokerAndFutures, Double totalEquity, Double cashEquity, Double etf, Double totalTransition, Double cashTransition, Double governmentsTransition) {
        this.date = date;
        this.totalFixed = totalFixed;
        this.governmentsFixed = governmentsFixed;
        this.corporates = corporates;
        this.agencies = agencies;
        this.supranationals = supranationals;
        this.currency = currency;
        this.options = options;
        this.cashFixed = cashFixed;
        this.cashBrokerAndFutures = cashBrokerAndFutures;
        this.totalEquity = totalEquity;
        this.cashEquity = cashEquity;
        this.etf = etf;
        this.totalTransition = totalTransition;
        this.cashTransition = cashTransition;
        this.governmentsTransition = governmentsTransition;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getTotalFixed() {
        return totalFixed;
    }

    public void setTotalFixed(Double totalFixed) {
        this.totalFixed = totalFixed;
    }

    public Double getGovernmentsFixed() {
        return governmentsFixed;
    }

    public void setGovernmentsFixed(Double governmentsFixed) {
        this.governmentsFixed = governmentsFixed;
    }

    public Double getCorporates() {
        return corporates;
    }

    public void setCorporates(Double corporates) {
        this.corporates = corporates;
    }

    public Double getAgencies() {
        return agencies;
    }

    public void setAgencies(Double agencies) {
        this.agencies = agencies;
    }

    public Double getSupranationals() {
        return supranationals;
    }

    public void setSupranationals(Double supranationals) {
        this.supranationals = supranationals;
    }

    public Double getCurrency() {
        return currency;
    }

    public void setCurrency(Double currency) {
        this.currency = currency;
    }

    public Double getOptions() {
        return options;
    }

    public void setOptions(Double options) {
        this.options = options;
    }

    public Double getCashFixed() {
        return cashFixed;
    }

    public void setCashFixed(Double cashFixed) {
        this.cashFixed = cashFixed;
    }

    public Double getCashBrokerAndFutures() {
        return cashBrokerAndFutures;
    }

    public void setCashBrokerAndFutures(Double cashBrokerAndFutures) {
        this.cashBrokerAndFutures = cashBrokerAndFutures;
    }

    public Double getTotalEquity() {
        return totalEquity;
    }

    public void setTotalEquity(Double totalEquity) {
        this.totalEquity = totalEquity;
    }

    public Double getCashEquity() {
        return cashEquity;
    }

    public void setCashEquity(Double cashEquity) {
        this.cashEquity = cashEquity;
    }

    public Double getEtf() {
        return etf;
    }

    public void setEtf(Double etf) {
        this.etf = etf;
    }

    public Double getTotalTransition() {
        return totalTransition;
    }

    public void setTotalTransition(Double totalTransition) {
        this.totalTransition = totalTransition;
    }

    public Double getCashTransition() {
        return cashTransition;
    }

    public void setCashTransition(Double cashTransition) {
        this.cashTransition = cashTransition;
    }

    public Double getGovernmentsTransition() {
        return governmentsTransition;
    }

    public void setGovernmentsTransition(Double governmentsTransition) {
        this.governmentsTransition = governmentsTransition;
    }
}
