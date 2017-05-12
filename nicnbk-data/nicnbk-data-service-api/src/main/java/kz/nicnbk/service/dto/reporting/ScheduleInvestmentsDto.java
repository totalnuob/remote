package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 05.05.2017.
 */
public class ScheduleInvestmentsDto extends InputFileReportDataDto{

    private String investmentName;
    private Double capitalCommitments;
    private Double netCost;
    private Double fairValue;
    private String description;
    private String strategy;
    private InvestmentType investmentType; // fund investment, co-investment, etc
    private Boolean totalStrategy;
    private Boolean totalStrategyByCurrency;
    private Boolean totalInvestment;
    private String currency;

    public ScheduleInvestmentsDto(){}

    public ScheduleInvestmentsDto(String investmentName, String currency, Double capitalCommitments, Double netCost, Double fairValue,
                                  String description, String strategy, InvestmentType investmentType,
                                  Boolean totalStrategyByCurrency, Boolean totalStrategy, Boolean totalInvestment){
        this.investmentName = investmentName;
        this.currency = currency;
        this.capitalCommitments = capitalCommitments;
        this.netCost = netCost;
        this.fairValue = fairValue;
        this.description = description;
        this.strategy = strategy;
        this.investmentType = investmentType;
        this.totalStrategy = totalStrategy;
        this.totalStrategyByCurrency = totalStrategyByCurrency;
        this.totalInvestment = totalInvestment;

    }

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public Double getCapitalCommitments() {
        return capitalCommitments;
    }

    public void setCapitalCommitments(Double capitalCommitments) {
        this.capitalCommitments = capitalCommitments;
    }

    public Double getNetCost() {
        return netCost;
    }

    public void setNetCost(Double netCost) {
        this.netCost = netCost;
    }

    public Double getFairValue() {
        return fairValue;
    }

    public void setFairValue(Double fairValue) {
        this.fairValue = fairValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public InvestmentType getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(InvestmentType investmentType) {
        this.investmentType = investmentType;
    }

    public Boolean getTotalStrategyByCurrency() {
        return totalStrategyByCurrency;
    }

    public void setTotalStrategyByCurrency(Boolean totalStrategyByCurrency) {
        this.totalStrategyByCurrency = totalStrategyByCurrency;
    }

    public Boolean getTotalStrategy() {
        return totalStrategy;
    }

    public void setTotalStrategy(Boolean totalStrategy) {
        this.totalStrategy = totalStrategy;
    }

    public Boolean getTotalInvestment() {
        return totalInvestment;
    }

    public void setTotalInvestment(Boolean totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void print(){

        System.out.println(this.investmentName + " - " + this.currency + " - " + this.capitalCommitments + " - " +
                this.netCost + " - " + this.fairValue + " - " + this.description + " - " + this.strategy + " - "
                + this.investmentType.getCode() + " - " + this.totalStrategyByCurrency + " - " + this.totalStrategy + " - " + this.totalInvestment);

        System.out.println("**************************************************");
//        System.out.println("description=" + description);
//        System.out.println("strategy=" + strategy);
//        System.out.println("substrategy=" + substrategy);
//        System.out.println("investmentName=" + investmentType);
//        System.out.println("investmentName=" + totalSubstrategy);
//        System.out.println("investmentName=" + totalStrategy);
//        System.out.println("investmentName=" + totalInvestment);
//        System.out.println("investmentName=" + currency);
    }
}
