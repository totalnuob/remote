package kz.nicnbk.service.dto.bloomberg;

import java.util.ArrayList;
import java.util.List;

public class SecurityDataDto {
    private String security;
    private List<FieldDataDto> fieldDataDtoList = new ArrayList<>();

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public List<FieldDataDto> getFieldDataDtoList() {
        return fieldDataDtoList;
    }

    public void setFieldDataDtoList(List<FieldDataDto> fieldDataDtoList) {
        this.fieldDataDtoList = fieldDataDtoList;
    }

    public void add(FieldDataDto fieldDataDto) {
        this.fieldDataDtoList.add(fieldDataDto);
    }
}
