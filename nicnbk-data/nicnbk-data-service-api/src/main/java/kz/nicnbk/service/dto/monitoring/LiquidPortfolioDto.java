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
    private Double totalFixedMtd;
    private Double totalFixedQtd;
    private Double totalFixedYtd;
    private Double governmentsFixed;
    private Double governmentsFixedMtd;
    private Double governmentsFixedQtd;
    private Double governmentsFixedYtd;
    private Double corporates;
    private Double corporatesMtd;
    private Double corporatesQtd;
    private Double corporatesYtd;
    private Double agencies;
    private Double agenciesMtd;
    private Double agenciesQtd;
    private Double agenciesYtd;
    private Double supranationals;
    private Double supranationalsMtd;
    private Double supranationalsQtd;
    private Double supranationalsYtd;
    private Double currency;
    private Double options;
    private Double cashFixed;
    private Double cashBrokerAndFutures;
    private Double cashBrokerAndFuturesMtd;
    private Double cashBrokerAndFuturesQtd;
    private Double cashBrokerAndFuturesYtd;

    private Double totalEquity;
    private Double totalEquityMtd;
    private Double totalEquityQtd;
    private Double totalEquityYtd;
    private Double cashEquity;
    private Double etf;
    private Double etfMtd;
    private Double etfQtd;
    private Double etfYtd;

    private Double totalTransition;
    private Double totalTransitionMtd;
    private Double totalTransitionQtd;
    private Double totalTransitionYtd;
    private Double cashTransition;
    private Double governmentsTransition;
    private Double governmentsTransitionMtd;
    private Double governmentsTransitionQtd;
    private Double governmentsTransitionYtd;

    public LiquidPortfolioDto() {
    }

    public LiquidPortfolioDto(Date date, Double totalFixed, Double totalFixedMtd, Double totalFixedQtd, Double totalFixedYtd, Double governmentsFixed, Double governmentsFixedMtd, Double governmentsFixedQtd, Double governmentsFixedYtd, Double corporates, Double corporatesMtd, Double corporatesQtd, Double corporatesYtd, Double agencies, Double agenciesMtd, Double agenciesQtd, Double agenciesYtd, Double supranationals, Double supranationalsMtd, Double supranationalsQtd, Double supranationalsYtd, Double currency, Double options, Double cashFixed, Double cashBrokerAndFutures, Double cashBrokerAndFuturesMtd, Double cashBrokerAndFuturesQtd, Double cashBrokerAndFuturesYtd, Double totalEquity, Double totalEquityMtd, Double totalEquityQtd, Double totalEquityYtd, Double cashEquity, Double etf, Double etfMtd, Double etfQtd, Double etfYtd, Double totalTransition, Double totalTransitionMtd, Double totalTransitionQtd, Double totalTransitionYtd, Double cashTransition, Double governmentsTransition, Double governmentsTransitionMtd, Double governmentsTransitionQtd, Double governmentsTransitionYtd) {
        this.date = date;
        this.totalFixed = totalFixed;
        this.totalFixedMtd = totalFixedMtd;
        this.totalFixedQtd = totalFixedQtd;
        this.totalFixedYtd = totalFixedYtd;
        this.governmentsFixed = governmentsFixed;
        this.governmentsFixedMtd = governmentsFixedMtd;
        this.governmentsFixedQtd = governmentsFixedQtd;
        this.governmentsFixedYtd = governmentsFixedYtd;
        this.corporates = corporates;
        this.corporatesMtd = corporatesMtd;
        this.corporatesQtd = corporatesQtd;
        this.corporatesYtd = corporatesYtd;
        this.agencies = agencies;
        this.agenciesMtd = agenciesMtd;
        this.agenciesQtd = agenciesQtd;
        this.agenciesYtd = agenciesYtd;
        this.supranationals = supranationals;
        this.supranationalsMtd = supranationalsMtd;
        this.supranationalsQtd = supranationalsQtd;
        this.supranationalsYtd = supranationalsYtd;
        this.currency = currency;
        this.options = options;
        this.cashFixed = cashFixed;
        this.cashBrokerAndFutures = cashBrokerAndFutures;
        this.cashBrokerAndFuturesMtd = cashBrokerAndFuturesMtd;
        this.cashBrokerAndFuturesQtd = cashBrokerAndFuturesQtd;
        this.cashBrokerAndFuturesYtd = cashBrokerAndFuturesYtd;
        this.totalEquity = totalEquity;
        this.totalEquityMtd = totalEquityMtd;
        this.totalEquityQtd = totalEquityQtd;
        this.totalEquityYtd = totalEquityYtd;
        this.cashEquity = cashEquity;
        this.etf = etf;
        this.etfMtd = etfMtd;
        this.etfQtd = etfQtd;
        this.etfYtd = etfYtd;
        this.totalTransition = totalTransition;
        this.totalTransitionMtd = totalTransitionMtd;
        this.totalTransitionQtd = totalTransitionQtd;
        this.totalTransitionYtd = totalTransitionYtd;
        this.cashTransition = cashTransition;
        this.governmentsTransition = governmentsTransition;
        this.governmentsTransitionMtd = governmentsTransitionMtd;
        this.governmentsTransitionQtd = governmentsTransitionQtd;
        this.governmentsTransitionYtd = governmentsTransitionYtd;
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

    public Double getTotalFixedMtd() {
        return totalFixedMtd;
    }

    public void setTotalFixedMtd(Double totalFixedMtd) {
        this.totalFixedMtd = totalFixedMtd;
    }

    public Double getTotalFixedQtd() {
        return totalFixedQtd;
    }

    public void setTotalFixedQtd(Double totalFixedQtd) {
        this.totalFixedQtd = totalFixedQtd;
    }

    public Double getTotalFixedYtd() {
        return totalFixedYtd;
    }

    public void setTotalFixedYtd(Double totalFixedYtd) {
        this.totalFixedYtd = totalFixedYtd;
    }

    public Double getGovernmentsFixed() {
        return governmentsFixed;
    }

    public void setGovernmentsFixed(Double governmentsFixed) {
        this.governmentsFixed = governmentsFixed;
    }

    public Double getGovernmentsFixedMtd() {
        return governmentsFixedMtd;
    }

    public void setGovernmentsFixedMtd(Double governmentsFixedMtd) {
        this.governmentsFixedMtd = governmentsFixedMtd;
    }

    public Double getGovernmentsFixedQtd() {
        return governmentsFixedQtd;
    }

    public void setGovernmentsFixedQtd(Double governmentsFixedQtd) {
        this.governmentsFixedQtd = governmentsFixedQtd;
    }

    public Double getGovernmentsFixedYtd() {
        return governmentsFixedYtd;
    }

    public void setGovernmentsFixedYtd(Double governmentsFixedYtd) {
        this.governmentsFixedYtd = governmentsFixedYtd;
    }

    public Double getCorporates() {
        return corporates;
    }

    public void setCorporates(Double corporates) {
        this.corporates = corporates;
    }

    public Double getCorporatesMtd() {
        return corporatesMtd;
    }

    public void setCorporatesMtd(Double corporatesMtd) {
        this.corporatesMtd = corporatesMtd;
    }

    public Double getCorporatesQtd() {
        return corporatesQtd;
    }

    public void setCorporatesQtd(Double corporatesQtd) {
        this.corporatesQtd = corporatesQtd;
    }

    public Double getCorporatesYtd() {
        return corporatesYtd;
    }

    public void setCorporatesYtd(Double corporatesYtd) {
        this.corporatesYtd = corporatesYtd;
    }

    public Double getAgencies() {
        return agencies;
    }

    public void setAgencies(Double agencies) {
        this.agencies = agencies;
    }

    public Double getAgenciesMtd() {
        return agenciesMtd;
    }

    public void setAgenciesMtd(Double agenciesMtd) {
        this.agenciesMtd = agenciesMtd;
    }

    public Double getAgenciesQtd() {
        return agenciesQtd;
    }

    public void setAgenciesQtd(Double agenciesQtd) {
        this.agenciesQtd = agenciesQtd;
    }

    public Double getAgenciesYtd() {
        return agenciesYtd;
    }

    public void setAgenciesYtd(Double agenciesYtd) {
        this.agenciesYtd = agenciesYtd;
    }

    public Double getSupranationals() {
        return supranationals;
    }

    public void setSupranationals(Double supranationals) {
        this.supranationals = supranationals;
    }

    public Double getSupranationalsMtd() {
        return supranationalsMtd;
    }

    public void setSupranationalsMtd(Double supranationalsMtd) {
        this.supranationalsMtd = supranationalsMtd;
    }

    public Double getSupranationalsQtd() {
        return supranationalsQtd;
    }

    public void setSupranationalsQtd(Double supranationalsQtd) {
        this.supranationalsQtd = supranationalsQtd;
    }

    public Double getSupranationalsYtd() {
        return supranationalsYtd;
    }

    public void setSupranationalsYtd(Double supranationalsYtd) {
        this.supranationalsYtd = supranationalsYtd;
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

    public Double getCashBrokerAndFuturesMtd() {
        return cashBrokerAndFuturesMtd;
    }

    public void setCashBrokerAndFuturesMtd(Double cashBrokerAndFuturesMtd) {
        this.cashBrokerAndFuturesMtd = cashBrokerAndFuturesMtd;
    }

    public Double getCashBrokerAndFuturesQtd() {
        return cashBrokerAndFuturesQtd;
    }

    public void setCashBrokerAndFuturesQtd(Double cashBrokerAndFuturesQtd) {
        this.cashBrokerAndFuturesQtd = cashBrokerAndFuturesQtd;
    }

    public Double getCashBrokerAndFuturesYtd() {
        return cashBrokerAndFuturesYtd;
    }

    public void setCashBrokerAndFuturesYtd(Double cashBrokerAndFuturesYtd) {
        this.cashBrokerAndFuturesYtd = cashBrokerAndFuturesYtd;
    }

    public Double getTotalEquity() {
        return totalEquity;
    }

    public void setTotalEquity(Double totalEquity) {
        this.totalEquity = totalEquity;
    }

    public Double getTotalEquityMtd() {
        return totalEquityMtd;
    }

    public void setTotalEquityMtd(Double totalEquityMtd) {
        this.totalEquityMtd = totalEquityMtd;
    }

    public Double getTotalEquityQtd() {
        return totalEquityQtd;
    }

    public void setTotalEquityQtd(Double totalEquityQtd) {
        this.totalEquityQtd = totalEquityQtd;
    }

    public Double getTotalEquityYtd() {
        return totalEquityYtd;
    }

    public void setTotalEquityYtd(Double totalEquityYtd) {
        this.totalEquityYtd = totalEquityYtd;
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

    public Double getEtfMtd() {
        return etfMtd;
    }

    public void setEtfMtd(Double etfMtd) {
        this.etfMtd = etfMtd;
    }

    public Double getEtfQtd() {
        return etfQtd;
    }

    public void setEtfQtd(Double etfQtd) {
        this.etfQtd = etfQtd;
    }

    public Double getEtfYtd() {
        return etfYtd;
    }

    public void setEtfYtd(Double etfYtd) {
        this.etfYtd = etfYtd;
    }

    public Double getTotalTransition() {
        return totalTransition;
    }

    public void setTotalTransition(Double totalTransition) {
        this.totalTransition = totalTransition;
    }

    public Double getTotalTransitionMtd() {
        return totalTransitionMtd;
    }

    public void setTotalTransitionMtd(Double totalTransitionMtd) {
        this.totalTransitionMtd = totalTransitionMtd;
    }

    public Double getTotalTransitionQtd() {
        return totalTransitionQtd;
    }

    public void setTotalTransitionQtd(Double totalTransitionQtd) {
        this.totalTransitionQtd = totalTransitionQtd;
    }

    public Double getTotalTransitionYtd() {
        return totalTransitionYtd;
    }

    public void setTotalTransitionYtd(Double totalTransitionYtd) {
        this.totalTransitionYtd = totalTransitionYtd;
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

    public Double getGovernmentsTransitionMtd() {
        return governmentsTransitionMtd;
    }

    public void setGovernmentsTransitionMtd(Double governmentsTransitionMtd) {
        this.governmentsTransitionMtd = governmentsTransitionMtd;
    }

    public Double getGovernmentsTransitionQtd() {
        return governmentsTransitionQtd;
    }

    public void setGovernmentsTransitionQtd(Double governmentsTransitionQtd) {
        this.governmentsTransitionQtd = governmentsTransitionQtd;
    }

    public Double getGovernmentsTransitionYtd() {
        return governmentsTransitionYtd;
    }

    public void setGovernmentsTransitionYtd(Double governmentsTransitionYtd) {
        this.governmentsTransitionYtd = governmentsTransitionYtd;
    }
}
