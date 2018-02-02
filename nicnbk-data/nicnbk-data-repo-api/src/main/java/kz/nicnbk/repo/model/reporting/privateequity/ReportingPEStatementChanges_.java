//package kz.nicnbk.repo.model.reporting.privateequity;
//
//import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
//import kz.nicnbk.repo.model.base.DataConstraints;
//import kz.nicnbk.repo.model.reporting.PeriodicReport;
//
//import javax.persistence.*;
//
///**
// * Created by magzumov on 20.04.2017.
// */
//
//@Entity
//@Table(name = "rep_pe_s_changes")
//public class ReportingPEStatementChanges_ extends CreateUpdateBaseEntity{
//
//    private String name;
//
//    private PeriodicReport report;
//
//    /*  Beginning Capital Balance Before Potential Carried Interest */
//    private Double beginningCapitalBalance;
//
//    /* Capital Contributions */
//    private Double capitalContributions;
//
//    /* Distributions */
//    private Double distributions;
//
//    /* Dividend and Interest Income, Net of Related Taxes */
//    private Double dividendAndInterestIncome;
//
//    /* Other Income */
//    private Double otherIncome;
//
//    /* Interest Expense - PEP's */
//    private Double interestExpense;
//
//    /* Administration Fees */
//    private Double administrationFee;
//
//    /* Management Fee */
//    private Double managementFee;
//
//    /* Audit and tax fees */
//    private Double auditTaxFee;
//
//    /* Organizational Costs */
//    private Double organizationalCosts;
//
//    /* License and Filing Fees */
//    private Double licenseFilingFee;
//
//    /* Other Expenses */
//    private Double otherExpenses;
//
//    /* Total */
//    private Double netIvestmentIncomeLossTotal;
//
//    /* Unrealized Gain (Loss) */
//    private Double unrealizedGainLoss;
//
//    /* Realized Gain (Loss) */
//    private Double realizedGainLoss;
//
//    /* Prior year - Potential carried interest allocated to general partner */
//    private Double priorYearPotentialInterest;
//
//    /* Current year - Potential carried interest allocated to general partner */
//    private Double currentYearPotentialInterest;
//
//
//
//    @Column(name = "name", length = DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "report_id", nullable = false)
//    public PeriodicReport getReport() {
//        return report;
//    }
//
//    public void setReport(PeriodicReport report) {
//        this.report = report;
//    }
//
//    @Column(name = "bgn_cap_balance")
//    public Double getBeginningCapitalBalance() {
//        return beginningCapitalBalance;
//    }
//
//    public void setBeginningCapitalBalance(Double beginningCapitalBalance) {
//        this.beginningCapitalBalance = beginningCapitalBalance;
//    }
//
//    @Column(name = "cap_contributions")
//    public Double getCapitalContributions() {
//        return capitalContributions;
//    }
//
//    public void setCapitalContributions(Double capitalContributions) {
//        this.capitalContributions = capitalContributions;
//    }
//
//    @Column(name = "distributions")
//    public Double getDistributions() {
//        return distributions;
//    }
//
//    public void setDistributions(Double distributions) {
//        this.distributions = distributions;
//    }
//
//    @Column(name = "dividend_interest_income")
//    public Double getDividendAndInterestIncome() {
//        return dividendAndInterestIncome;
//    }
//
//    public void setDividendAndInterestIncome(Double dividendAndInterestIncome) {
//        this.dividendAndInterestIncome = dividendAndInterestIncome;
//    }
//
//    @Column(name = "other_income")
//    public Double getOtherIncome() {
//        return otherIncome;
//    }
//
//    public void setOtherIncome(Double otherIncome) {
//        this.otherIncome = otherIncome;
//    }
//
//    @Column(name = "management_fee")
//    public Double getManagementFee() {
//        return managementFee;
//    }
//
//    public void setManagementFee(Double managementFee) {
//        this.managementFee = managementFee;
//    }
//
//    @Column(name = "administration_fee")
//    public Double getAdministrationFee() {
//        return administrationFee;
//    }
//
//    public void setAdministrationFee(Double administrationFee) {
//        this.administrationFee = administrationFee;
//    }
//
//    @Column(name = "audit_tax_fee")
//    public Double getAuditTaxFee() {
//        return auditTaxFee;
//    }
//
//    public void setAuditTaxFee(Double auditTaxFee) {
//        this.auditTaxFee = auditTaxFee;
//    }
//
//    @Column(name = "org_costs")
//    public Double getOrganizationalCosts() {
//        return organizationalCosts;
//    }
//
//    public void setOrganizationalCosts(Double organizationalCosts) {
//        this.organizationalCosts = organizationalCosts;
//    }
//
//    @Column(name = "interest_expense")
//    public Double getInterestExpense() {
//        return interestExpense;
//    }
//
//    public void setInterestExpense(Double interestExpense) {
//        this.interestExpense = interestExpense;
//    }
//
//    @Column(name = "license_filing_fee")
//    public Double getLicenseFilingFee() {
//        return licenseFilingFee;
//    }
//
//    public void setLicenseFilingFee(Double licenseFilingFee) {
//        this.licenseFilingFee = licenseFilingFee;
//    }
//
//    @Column(name = "other_expense")
//    public Double getOtherExpenses() {
//        return otherExpenses;
//    }
//
//    public void setOtherExpenses(Double otherExpenses) {
//        this.otherExpenses = otherExpenses;
//    }
//
//    @Column(name = "realized_gain_loss")
//    public Double getRealizedGainLoss() {
//        return realizedGainLoss;
//    }
//
//    public void setRealizedGainLoss(Double realizedGainLoss) {
//        this.realizedGainLoss = realizedGainLoss;
//    }
//
//    @Column(name = "unrealized_gain_loss")
//    public Double getUnrealizedGainLoss() {
//        return unrealizedGainLoss;
//    }
//
//    public void setUnrealizedGainLoss(Double unrealizedGainLoss) {
//        this.unrealizedGainLoss = unrealizedGainLoss;
//    }
//
//    @Column(name = "prior_potential_interest")
//    public Double getPriorYearPotentialInterest() {
//        return priorYearPotentialInterest;
//    }
//
//    public void setPriorYearPotentialInterest(Double priorYearPotentialInterest) {
//        this.priorYearPotentialInterest = priorYearPotentialInterest;
//    }
//
//    @Column(name = "current_potential_interest")
//    public Double getCurrentYearPotentialInterest() {
//        return currentYearPotentialInterest;
//    }
//
//    public void setCurrentYearPotentialInterest(Double currentYearPotentialInterest) {
//        this.currentYearPotentialInterest = currentYearPotentialInterest;
//    }
//}
