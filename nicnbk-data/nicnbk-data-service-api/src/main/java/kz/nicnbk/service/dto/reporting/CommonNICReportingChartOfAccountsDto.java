package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 28.09.2017.
 */
public class CommonNICReportingChartOfAccountsDto implements BaseDto {

    private Long id;
    private String accountNumber;
    private String nameEn;
    private NICReportingChartOfAccountsDto NICChartOfAccounts;
    private Boolean addable;
    private Boolean editable;
    private Boolean negativeOnly;
    private Boolean positiveOnly;
    private Boolean deletable;
    private BaseDictionaryDto chartAccountsType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public NICReportingChartOfAccountsDto getNICChartOfAccounts() {
        return NICChartOfAccounts;
    }

    public void setNICChartOfAccounts(NICReportingChartOfAccountsDto NICChartOfAccounts) {
        this.NICChartOfAccounts = NICChartOfAccounts;
    }

    public Boolean getAddable() {
        return addable;
    }

    public void setAddable(Boolean addable) {
        this.addable = addable;
    }

    public Boolean getNegativeOnly() {
        return negativeOnly;
    }

    public void setNegativeOnly(Boolean negativeOnly) {
        this.negativeOnly = negativeOnly;
    }

    public Boolean getPositiveOnly() {
        return positiveOnly;
    }

    public void setPositiveOnly(Boolean positiveOnly) {
        this.positiveOnly = positiveOnly;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    public BaseDictionaryDto getChartAccountsType() {
        return chartAccountsType;
    }

    public void setChartAccountsType(BaseDictionaryDto chartAccountsType) {
        this.chartAccountsType = chartAccountsType;
    }
}
