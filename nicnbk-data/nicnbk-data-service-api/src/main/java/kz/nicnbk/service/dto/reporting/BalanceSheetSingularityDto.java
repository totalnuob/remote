package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 12.06.2017.
 */
public class BalanceSheetSingularityDto extends ConsolidatedStatementRecordHolderDto {

    public void print(){
        if(super.getRecords() != null){
            for(ConsolidatedStatementRecordDto recordDto: super.getRecords()){
                System.out.println(recordDto.getName() +  " - " + recordDto.getType() +
                        " - " + recordDto.getCategory() +  " - " + recordDto.getValues()[0]);
            }
        }
    }

}
