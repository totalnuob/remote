package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 30.06.2017.
 */
public class ConsolidatedReportRecordHolderDto implements BaseDto {

    private List<ConsolidatedReportRecordDto> trancheA;
    private List<ConsolidatedReportRecordDto> trancheB;

    public List<ConsolidatedReportRecordDto> getTrancheA() {
        return trancheA;
    }

    public void setTrancheA(List<ConsolidatedReportRecordDto> trancheA) {
        this.trancheA = trancheA;
    }

    public List<ConsolidatedReportRecordDto> getTrancheB() {
        return trancheB;
    }

    public void setTrancheB(List<ConsolidatedReportRecordDto> trancheB) {
        this.trancheB = trancheB;
    }
}
