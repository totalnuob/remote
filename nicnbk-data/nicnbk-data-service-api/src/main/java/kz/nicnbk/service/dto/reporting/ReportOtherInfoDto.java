package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.ReportOtherInfo;

import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */
public class ReportOtherInfoDto extends CreateUpdateBaseEntityDto<ReportOtherInfo> {

    private Double closingBalance;
    private Date exchangeRateDate;
    private Double exchangeRate;
    private PeriodicReportDto report;

    private Long monthlyCashStatementFileId;
    private String monthlyCashStatementFileName;

    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public Date getExchangeRateDate() {
        return exchangeRateDate;
    }

    public void setExchangeRateDate(Date exchangeRateDate) {
        this.exchangeRateDate = exchangeRateDate;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public Long getMonthlyCashStatementFileId() {
        return monthlyCashStatementFileId;
    }

    public void setMonthlyCashStatementFileId(Long monthlyCashStatementFileId) {
        this.monthlyCashStatementFileId = monthlyCashStatementFileId;
    }

    public String getMonthlyCashStatementFileName() {
        return monthlyCashStatementFileName;
    }

    public void setMonthlyCashStatementFileName(String monthlyCashStatementFileName) {
        this.monthlyCashStatementFileName = monthlyCashStatementFileName;
    }
}
