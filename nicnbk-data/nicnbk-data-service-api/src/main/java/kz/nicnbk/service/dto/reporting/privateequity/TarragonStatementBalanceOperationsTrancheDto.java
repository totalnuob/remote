package kz.nicnbk.service.dto.reporting.privateequity;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;

import java.util.List;

public class TarragonStatementBalanceOperationsTrancheDto implements BaseDto {

    private BaseDictionaryDto trancheType;
    private List<ConsolidatedReportRecordDto> records;

    public BaseDictionaryDto getTrancheType() {
        return trancheType;
    }

    public void setTrancheType(BaseDictionaryDto trancheType) {
        this.trancheType = trancheType;
    }

    public List<ConsolidatedReportRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<ConsolidatedReportRecordDto> records) {
        this.records = records;
    }
}
