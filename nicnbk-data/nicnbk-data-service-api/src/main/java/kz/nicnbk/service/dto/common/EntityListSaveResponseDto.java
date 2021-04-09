package kz.nicnbk.service.dto.common;

import java.util.List;

/**
 * Dto class for entity list save response.
 *
 * Created by magzumov.
 */
public class EntityListSaveResponseDto extends ResponseDto {
    List records;

    public List getRecords() {
        return records;
    }

    public void setRecords(List records) {
        this.records = records;
    }
}
