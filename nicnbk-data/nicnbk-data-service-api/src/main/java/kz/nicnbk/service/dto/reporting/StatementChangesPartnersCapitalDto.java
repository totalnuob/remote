package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 12.06.2017.
 */
public class StatementChangesPartnersCapitalDto extends ConsolidatedStatementRecordHolderDto {

    public void print(){
        if(super.getRecords() != null){
            for(ConsolidatedStatementRecordDto recordDto: super.getRecords()){
                System.out.println(recordDto.getName() + " - " + recordDto.getValues()[0] +
                        " - " + recordDto.getValues()[1] + " - " + recordDto.getValues()[2] +
                        " - " + recordDto.getValues()[3] + " - " + recordDto.getValues()[4] +
                        " - " + recordDto.getValues()[5] + " - " + recordDto.getValues()[6] +
                        " - " + recordDto.getValues()[7] + " - " + recordDto.getValues()[8] +
                        " - " + recordDto.getValues()[9] + " - " + recordDto.getValues()[10] +
                        " - " + recordDto.getValues()[11] + " - " + recordDto.getValues()[12] +
                        " - " + recordDto.getValues()[13] + " - " + recordDto.getValues()[14] +
                        " - " + recordDto.getValues()[15] + " - " + recordDto.getValues()[16] +
                        " - " + recordDto.getValues()[17] + " - " + recordDto.getValues()[18]);
            }
        }
    }
}
