package kz.nicnbk.service.dto.riskmanagement;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 01.11.2016.
 */
public class TableColumnDto implements BaseDto{
    private String type;
    private String name;

    public TableColumnDto(){}

    public TableColumnDto(String type, String name){
        this.type = type;
        this.name = name;
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
