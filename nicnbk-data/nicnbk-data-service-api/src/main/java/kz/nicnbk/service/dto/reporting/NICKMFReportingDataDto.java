package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.reporting.NICKMFReportingData;

/**
 * Created by magzumov on 20.04.2017.
 */
public class NICKMFReportingDataDto extends CreateUpdateBaseEntityDto<NICKMFReportingData> {

    private String nicChartOfAccountsCode;
    private String nicChartOfAccountsName;
    private String nbChartOfAccountsCode;
    private String nbChartOfAccountsName;
    private Double accountBalance;

    private Double calculatedAccountBalance;
    private String calculatedAccountBalanceFormula;

    public String getNicChartOfAccountsCode() {
        return nicChartOfAccountsCode;
    }

    public void setNicChartOfAccountsCode(String nicChartOfAccountsCode) {
        this.nicChartOfAccountsCode = nicChartOfAccountsCode;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getNbChartOfAccountsCode() {
        return nbChartOfAccountsCode;
    }

    public void setNbChartOfAccountsCode(String nbChartOfAccountsCode) {
        this.nbChartOfAccountsCode = nbChartOfAccountsCode;
    }

    public String getNicChartOfAccountsName() {
        return nicChartOfAccountsName;
    }

    public void setNicChartOfAccountsName(String nicChartOfAccountsName) {
        this.nicChartOfAccountsName = nicChartOfAccountsName;
    }

    public String getNbChartOfAccountsName() {
        return nbChartOfAccountsName;
    }

    public void setNbChartOfAccountsName(String nbChartOfAccountsName) {
        this.nbChartOfAccountsName = nbChartOfAccountsName;
    }

    public Double getCalculatedAccountBalance() {
        return calculatedAccountBalance;
    }

    public void setCalculatedAccountBalance(Double calculatedAccountBalance) {
        this.calculatedAccountBalance = calculatedAccountBalance;
    }

    public String getCalculatedAccountBalanceFormula() {
        return calculatedAccountBalanceFormula;
    }

    public void setCalculatedAccountBalanceFormula(String calculatedAccountBalanceFormula) {
        this.calculatedAccountBalanceFormula = calculatedAccountBalanceFormula;
    }
}
