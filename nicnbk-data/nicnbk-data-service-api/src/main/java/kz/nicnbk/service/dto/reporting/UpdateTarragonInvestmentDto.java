package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 20.10.2017.
 */
public class UpdateTarragonInvestmentDto implements BaseDto {

    private Long reportId;
    private String fundName;
    private int tranche;
    private String trancheTypeNameEn;
    private Double accountBalance;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getTrancheTypeNameEn() {
        return trancheTypeNameEn;
    }

    public void setTrancheTypeNameEn(String trancheTypeNameEn) {
        this.trancheTypeNameEn = trancheTypeNameEn;
    }
}
