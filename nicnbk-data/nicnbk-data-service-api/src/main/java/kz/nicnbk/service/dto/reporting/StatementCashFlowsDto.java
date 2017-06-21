package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.service.dto.reporting.ConsolidatedStatementRecordDto;
import kz.nicnbk.service.dto.reporting.InputFileReportDataDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 12.06.2017.
 */
public class StatementCashFlowsDto extends ConsolidatedStatementRecordHolderDto {

    public void print(){
        if(super.getRecords() != null){
            for(ConsolidatedStatementRecordDto recordDto: super.getRecords()){
                System.out.println(recordDto.getName() + " - " + recordDto.getValues()[0] +
                        " - " + recordDto.getValues()[1] + " - " + recordDto.getValues()[2]);
            }
        }
    }
}
