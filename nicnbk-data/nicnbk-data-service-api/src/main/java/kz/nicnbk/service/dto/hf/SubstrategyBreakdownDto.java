package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 14.12.2016.
 */
public class SubstrategyBreakdownDto implements BaseDto {
    private String code;
    private Double value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
