package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.reporting.NICKMFReportingData;
import kz.nicnbk.repo.model.reporting.ReportOtherInfo;

import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */
public class NICKMFReportingDataDto extends CreateUpdateBaseEntityDto<NICKMFReportingData> {

    private String nicChartOfAccountsCode;
    private String nbChartOfAccountsCode;
    private Double accountBalance;

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
}
