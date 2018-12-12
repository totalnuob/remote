package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 03.10.2017.
 */
public class GeneratedGeneralLedgerFormDto extends GeneralLedgerBalanceRecordDto implements Comparable {


    // TODO: rename
    private String subscriptionRedemptionEntity;
    private String nbAccountNumber;
    private String nicAccountName;

    private boolean added;
    private Long addedRecordId;
    private boolean editable;

    public GeneratedGeneralLedgerFormDto(){}

    public GeneratedGeneralLedgerFormDto(GeneratedGeneralLedgerFormDto other){
        this.subscriptionRedemptionEntity = other.subscriptionRedemptionEntity;
        this.nbAccountNumber = other.nbAccountNumber;
        this.nicAccountName = other.nicAccountName;

        this.setAcronym(other.getAcronym());
        this.setBalanceDate(other.getBalanceDate());
        this.setFinancialStatementCategory(other.getFinancialStatementCategory());
        this.setGLAccount(other.getGLAccount());
        this.setFinancialStatementCategoryDescription(other.getFinancialStatementCategoryDescription());
        this.setChartAccountsDescription(other.getChartAccountsDescription());
        this.setChartAccountsLongDescription(other.getChartAccountsLongDescription());
        this.setGLAccountBalance(other.getGLAccountBalance());
        this.setSegValCCY(other.getSegValCCY());
        this.setFundCCY(other.getFundCCY());

    }

    public String getSubscriptionRedemptionEntity() {
        return subscriptionRedemptionEntity;
    }

    public void setSubscriptionRedemptionEntity(String subscriptionRedemptionEntity) {
        this.subscriptionRedemptionEntity = subscriptionRedemptionEntity;
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

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public Long getAddedRecordId() {
        return addedRecordId;
    }

    public void setAddedRecordId(Long addedRecordId) {
        this.addedRecordId = addedRecordId;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public int compareTo(Object o) {
        if(getSortingScore(this) < getSortingScore((GeneratedGeneralLedgerFormDto)o)){
            return -1;
        }else if(getSortingScore(this) > getSortingScore((GeneratedGeneralLedgerFormDto)o)){
            return 1;
        }else if(this.getNbAccountNumber() != null && ((GeneratedGeneralLedgerFormDto) o).getNbAccountNumber() != null){
            return this.getNbAccountNumber().compareTo(((GeneratedGeneralLedgerFormDto) o).getNbAccountNumber());
        }else {
            return 0;
        }
    }

    private int getSortingScore(GeneratedGeneralLedgerFormDto dto){
        int result = dto.getAcronym() != null && dto.getAcronym().endsWith(" B") ? 1000 : 10;
        if(dto.getFinancialStatementCategory() == null){
            return result;
        }else if(dto.getFinancialStatementCategory().equalsIgnoreCase("A")){
            result += 1;
        }else if(dto.getFinancialStatementCategory().equalsIgnoreCase("L")){
            result += 2;
        }else if(dto.getFinancialStatementCategory().equalsIgnoreCase("E")){
            result += 3;
        }if(dto.getFinancialStatementCategory().equalsIgnoreCase("I")){
            result += 4;
        }if(dto.getFinancialStatementCategory().equalsIgnoreCase("X")){
            result += 5;
        }

        return result;
    }
}
