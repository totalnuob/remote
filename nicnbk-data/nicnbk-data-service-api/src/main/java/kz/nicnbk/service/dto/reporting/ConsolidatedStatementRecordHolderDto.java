package kz.nicnbk.service.dto.reporting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 12.06.2017.
 */
public class ConsolidatedStatementRecordHolderDto extends InputFileReportDataDto {

    private List<ConsolidatedStatementRecordDto> records;

    public List<ConsolidatedStatementRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<ConsolidatedStatementRecordDto> records) {
        this.records = records;
    }

    public void addRecord(ConsolidatedStatementRecordDto recordDto){
        if(this.records == null){
            this.records = new ArrayList<>();
        }
        this.records.add(recordDto);
    }
}
