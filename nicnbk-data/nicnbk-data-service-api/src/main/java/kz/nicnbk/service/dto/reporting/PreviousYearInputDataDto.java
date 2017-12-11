package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 27.10.2017.
 */
public class PreviousYearInputDataDto implements BaseDto {
    private NICReportingChartOfAccountsDto chartOfAccounts;
    private Double accountBalance;
    private Double accountBalanceKZT;
    private PeriodicReportDto report;

    public NICReportingChartOfAccountsDto getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(NICReportingChartOfAccountsDto chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public Double getAccountBalanceKZT() {
        return accountBalanceKZT;
    }

    public void setAccountBalanceKZT(Double accountBalanceKZT) {
        this.accountBalanceKZT = accountBalanceKZT;
    }
}
