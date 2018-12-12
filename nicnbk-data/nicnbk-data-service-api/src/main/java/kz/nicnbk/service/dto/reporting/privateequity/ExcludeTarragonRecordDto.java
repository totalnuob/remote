package kz.nicnbk.service.dto.reporting.privateequity;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 20.10.2017.
 */
public class ExcludeTarragonRecordDto implements BaseDto {

    private Long recordId;
    private String type;
    private String name;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
