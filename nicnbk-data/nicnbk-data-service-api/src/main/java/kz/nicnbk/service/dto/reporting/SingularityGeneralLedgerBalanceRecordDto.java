package kz.nicnbk.service.dto.reporting;

import java.util.Date;

/**
 * Created by magzumov on 25.08.2017.
 */
public class SingularityGeneralLedgerBalanceRecordDto extends GeneralLedgerBalanceRecordDto {
    private String acronym;
    private Date balanceDate;
    private String financialStatementCategory;
    private String GLAccount;
    private String financialStatementCategoryDescription;
    private String chartAccountsDescription;
    private String chartAccountsLongDescription;
    private String shortName;

    private String segVal1;
//    private String segVal2;
//    private String segVal3;
//    private String segVal4;
    private Double GLAccountBalance;
    private String segValCCY;
    private String fundCCY;

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Date getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(Date balanceDate) {
        this.balanceDate = balanceDate;
    }

    public String getFinancialStatementCategory() {
        return financialStatementCategory;
    }

    public void setFinancialStatementCategory(String financialStatementCategory) {
        this.financialStatementCategory = financialStatementCategory;
    }

    public String getGLAccount() {
        return GLAccount;
    }

    public void setGLAccount(String GLAccount) {
        this.GLAccount = GLAccount;
    }

    public String getFinancialStatementCategoryDescription() {
        return financialStatementCategoryDescription;
    }

    public void setFinancialStatementCategoryDescription(String financialStatementCategoryDescription) {
        this.financialStatementCategoryDescription = financialStatementCategoryDescription;
    }

    public String getChartAccountsDescription() {
        return chartAccountsDescription;
    }

    public void setChartAccountsDescription(String chartAccountsDescription) {
        this.chartAccountsDescription = chartAccountsDescription;
    }

    public String getChartAccountsLongDescription() {
        return chartAccountsLongDescription;
    }

    public void setChartAccountsLongDescription(String chartAccountsLongDescription) {
        this.chartAccountsLongDescription = chartAccountsLongDescription;
    }

    public Double getGLAccountBalance() {
        return GLAccountBalance;
    }

    public void setGLAccountBalance(Double GLAccountBalance) {
        this.GLAccountBalance = GLAccountBalance;
    }

    public String getSegValCCY() {
        return segValCCY;
    }

    public void setSegValCCY(String segValCCY) {
        this.segValCCY = segValCCY;
    }

    public String getFundCCY() {
        return fundCCY;
    }

    public void setFundCCY(String fundCCY) {
        this.fundCCY = fundCCY;
    }

    public String getSegVal1() {
        return segVal1;
    }

    public void setSegVal1(String segVal1) {
        this.segVal1 = segVal1;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
