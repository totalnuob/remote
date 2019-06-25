package kz.nicnbk.repo.model.monitoring;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Pak on 20.06.2019.
 */

@Entity(name = "monitoring_liquid_portfolio")
public class LiquidPortfolio extends BaseEntity {

    private Employee updaterFixed;
    private Employee updaterEquity;
    private Employee updaterTransition;

    private Files fileFixed;
    private Files fileEquity;
    private Files fileTransition;

    @Column(nullable = false)
    private Date date;

    private Double totalFixed;
    private Double totalFixedFlow;
    private Double governmentsFixed;
    private Double governmentsFixedFlow;
    private Double corporates;
    private Double corporatesFlow;
    private Double agencies;
    private Double agenciesFlow;
    private Double supranationals;
    private Double supranationalsFlow;
    private Double currency;
    private Double options;
    private Double cashFixed;
    private Double cashBrokerAndFutures;

    private Double totalEquity;
    private Double totalEquityFlow;
    private Double cashEquity;
    private Double etf;
    private Double etfFlow;

    private Double totalTransition;
    private Double totalTransitionFlow;
    private Double cashTransition;
    private Double governmentsTransition;
    private Double governmentsTransitionFlow;

    public LiquidPortfolio() {
    }

    public LiquidPortfolio(Employee updaterFixed, Employee updaterEquity, Employee updaterTransition, Files fileFixed, Files fileEquity, Files fileTransition, Date date, Double totalFixed, Double totalFixedFlow, Double governmentsFixed, Double governmentsFixedFlow, Double corporates, Double corporatesFlow, Double agencies, Double agenciesFlow, Double supranationals, Double supranationalsFlow, Double currency, Double options, Double cashFixed, Double cashBrokerAndFutures, Double totalEquity, Double totalEquityFlow, Double cashEquity, Double etf, Double etfFlow, Double totalTransition, Double totalTransitionFlow, Double cashTransition, Double governmentsTransition, Double governmentsTransitionFlow) {
        this.updaterFixed = updaterFixed;
        this.updaterEquity = updaterEquity;
        this.updaterTransition = updaterTransition;
        this.fileFixed = fileFixed;
        this.fileEquity = fileEquity;
        this.fileTransition = fileTransition;
        this.date = date;
        this.totalFixed = totalFixed;
        this.totalFixedFlow = totalFixedFlow;
        this.governmentsFixed = governmentsFixed;
        this.governmentsFixedFlow = governmentsFixedFlow;
        this.corporates = corporates;
        this.corporatesFlow = corporatesFlow;
        this.agencies = agencies;
        this.agenciesFlow = agenciesFlow;
        this.supranationals = supranationals;
        this.supranationalsFlow = supranationalsFlow;
        this.currency = currency;
        this.options = options;
        this.cashFixed = cashFixed;
        this.cashBrokerAndFutures = cashBrokerAndFutures;
        this.totalEquity = totalEquity;
        this.totalEquityFlow = totalEquityFlow;
        this.cashEquity = cashEquity;
        this.etf = etf;
        this.etfFlow = etfFlow;
        this.totalTransition = totalTransition;
        this.totalTransitionFlow = totalTransitionFlow;
        this.cashTransition = cashTransition;
        this.governmentsTransition = governmentsTransition;
        this.governmentsTransitionFlow = governmentsTransitionFlow;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedFixedBy")
    public Employee getUpdaterFixed() {
        return updaterFixed;
    }

    public void setUpdaterFixed(Employee updaterFixed) {
        this.updaterFixed = updaterFixed;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedEquityBy")
    public Employee getUpdaterEquity() {
        return updaterEquity;
    }

    public void setUpdaterEquity(Employee updaterEquity) {
        this.updaterEquity = updaterEquity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedTransitionBy")
    public Employee getUpdaterTransition() {
        return updaterTransition;
    }

    public void setUpdaterTransition(Employee updaterTransition) {
        this.updaterTransition = updaterTransition;
    }

    @ManyToOne
    @JoinColumn(name = "file_fixed_id")
    public Files getFileFixed() {
        return fileFixed;
    }

    public void setFileFixed(Files fileFixed) {
        this.fileFixed = fileFixed;
    }

    @ManyToOne
    @JoinColumn(name = "file_equity_id")
    public Files getFileEquity() {
        return fileEquity;
    }

    public void setFileEquity(Files fileEquity) {
        this.fileEquity = fileEquity;
    }

    @ManyToOne
    @JoinColumn(name = "file_transition_id")
    public Files getFileTransition() {
        return fileTransition;
    }

    public void setFileTransition(Files fileTransition) {
        this.fileTransition = fileTransition;
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

    public Double getTotalFixedFlow() {
        return totalFixedFlow;
    }

    public void setTotalFixedFlow(Double totalFixedFlow) {
        this.totalFixedFlow = totalFixedFlow;
    }

    public Double getGovernmentsFixed() {
        return governmentsFixed;
    }

    public void setGovernmentsFixed(Double governmentsFixed) {
        this.governmentsFixed = governmentsFixed;
    }

    public Double getGovernmentsFixedFlow() {
        return governmentsFixedFlow;
    }

    public void setGovernmentsFixedFlow(Double governmentsFixedFlow) {
        this.governmentsFixedFlow = governmentsFixedFlow;
    }

    public Double getCorporates() {
        return corporates;
    }

    public void setCorporates(Double corporates) {
        this.corporates = corporates;
    }

    public Double getCorporatesFlow() {
        return corporatesFlow;
    }

    public void setCorporatesFlow(Double corporatesFlow) {
        this.corporatesFlow = corporatesFlow;
    }

    public Double getAgencies() {
        return agencies;
    }

    public void setAgencies(Double agencies) {
        this.agencies = agencies;
    }

    public Double getAgenciesFlow() {
        return agenciesFlow;
    }

    public void setAgenciesFlow(Double agenciesFlow) {
        this.agenciesFlow = agenciesFlow;
    }

    public Double getSupranationals() {
        return supranationals;
    }

    public void setSupranationals(Double supranationals) {
        this.supranationals = supranationals;
    }

    public Double getSupranationalsFlow() {
        return supranationalsFlow;
    }

    public void setSupranationalsFlow(Double supranationalsFlow) {
        this.supranationalsFlow = supranationalsFlow;
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

    public Double getTotalEquityFlow() {
        return totalEquityFlow;
    }

    public void setTotalEquityFlow(Double totalEquityFlow) {
        this.totalEquityFlow = totalEquityFlow;
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

    public Double getEtfFlow() {
        return etfFlow;
    }

    public void setEtfFlow(Double etfFlow) {
        this.etfFlow = etfFlow;
    }

    public Double getTotalTransition() {
        return totalTransition;
    }

    public void setTotalTransition(Double totalTransition) {
        this.totalTransition = totalTransition;
    }

    public Double getTotalTransitionFlow() {
        return totalTransitionFlow;
    }

    public void setTotalTransitionFlow(Double totalTransitionFlow) {
        this.totalTransitionFlow = totalTransitionFlow;
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

    public Double getGovernmentsTransitionFlow() {
        return governmentsTransitionFlow;
    }

    public void setGovernmentsTransitionFlow(Double governmentsTransitionFlow) {
        this.governmentsTransitionFlow = governmentsTransitionFlow;
    }
}
