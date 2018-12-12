package kz.nicnbk.service.dto.reporting.hedgefunds;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 20.10.2017.
 */
public class ExcludeSingularityRecordDto implements BaseDto {

    private Long recordId;
    private String name;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
