package kz.nicnbk.service.dto.reporting;

import java.util.List;

/**
 * Created by magzumov on 15.05.2017.
 */
@Deprecated
public class StatementAssetsLiabilitiesDto extends InputFileReportDataDto{

    private List<ConsolidatedStatementRecordDto> balance;

    private List<ConsolidatedStatementRecordDto> operations;

    public List<ConsolidatedStatementRecordDto> getBalance() {
        return balance;
    }

    public void setBalance(List<ConsolidatedStatementRecordDto> balance) {
        this.balance = balance;
    }

    public List<ConsolidatedStatementRecordDto> getOperations() {
        return operations;
    }

    public void setOperations(List<ConsolidatedStatementRecordDto> operations) {
        this.operations = operations;
    }

    public void print(){
        System.out.println("Consolidated Statement of Assets, Liabilities and Partners' Capital:");
        if(this.balance != null){
            for(ConsolidatedStatementRecordDto dto: this.balance){
                System.out.println(dto.getName() + " | " + dto.getType() + " | " + dto.getValues()[0]
                        + " | " + dto.getValues()[1] + " | " + dto.getValues()[2] + " | " + dto.getValues()[3]
                        + " | " + dto.getValues()[4] + " | " + dto.getValues()[5] + " | " + dto.getValues()[6]);
            }
        }

        System.out.println("\nConsolidated Statement of Operations:");
        if(this.operations != null){
            for(ConsolidatedStatementRecordDto dto: this.operations){
                System.out.println(dto.getName() + " | " + dto.getType() + " | " + dto.getValues()[0]
                        + " | " + dto.getValues()[1] + " | " + dto.getValues()[2] + " | " + dto.getValues()[3]
                        + " | " + dto.getValues()[4] + " | " + dto.getValues()[5] + " | " + dto.getValues()[6]);
            }
        }
        System.out.println("**************************************************");
    }
}
