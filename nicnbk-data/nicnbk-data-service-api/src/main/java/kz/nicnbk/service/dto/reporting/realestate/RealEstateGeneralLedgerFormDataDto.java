package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEGeneralLedgerFormData;
import kz.nicnbk.repo.model.reporting.realestate.RealEstateGeneralLedgerFormData;

/**
 * Created by magzumov on 20.04.2017.
 */
public class RealEstateGeneralLedgerFormDataDto extends CreateUpdateBaseEntityDto<RealEstateGeneralLedgerFormData> {

    private int tranche;
    private String trancheType;
    private String financialStatementCategory;
    private String terraNICChartOfAccountsName;
    private String nbAccountNumber;
    private String nicAccountName;
    private Double GLAccountBalance;
    private String entityName;
    private PeriodicReport report;

    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
    }

    public String getTrancheType() {
        return trancheType;
    }

    public void setTrancheType(String trancheType) {
        this.trancheType = trancheType;
    }

    public String getFinancialStatementCategory() {
        return financialStatementCategory;
    }

    public void setFinancialStatementCategory(String financialStatementCategory) {
        this.financialStatementCategory = financialStatementCategory;
    }

    public String getTerraNICChartOfAccountsName() {
        return terraNICChartOfAccountsName;
    }

    public void setTerraNICChartOfAccountsName(String terraNICChartOfAccountsName) {
        this.terraNICChartOfAccountsName = terraNICChartOfAccountsName;
    }

    public Double getGLAccountBalance() {
        return GLAccountBalance;
    }

    public void setGLAccountBalance(Double GLAccountBalance) {
        this.GLAccountBalance = GLAccountBalance;
    }

    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    public String getNbAccountNumber() {
        return nbAccountNumber;
    }

    public void setNbAccountNumber(String nbAccountNumber) {
        this.nbAccountNumber = nbAccountNumber;
    }

    public String getNicAccountName() {
        return nicAccountName;
    }

    public void setNicAccountName(String nicAccountName) {
        this.nicAccountName = nicAccountName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
