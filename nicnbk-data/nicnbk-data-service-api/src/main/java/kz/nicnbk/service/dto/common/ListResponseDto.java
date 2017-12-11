package kz.nicnbk.service.dto.common;

import java.util.List;

/**
 * Created by magzumov on 07.11.2017.
 */
public class ListResponseDto extends StatusResultDto {

    List records;

    public List getRecords() {
        return records;
    }

    public void setRecords(List records) {
        this.records = records;
    }
}
