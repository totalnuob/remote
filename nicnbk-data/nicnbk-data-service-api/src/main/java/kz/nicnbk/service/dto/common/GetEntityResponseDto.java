package kz.nicnbk.service.dto.common;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 07.11.2017.
 */
public class GetEntityResponseDto extends ResponseDto {

    BaseDto entity;

    public BaseDto getEntity() {
        return entity;
    }

    public void setEntity(BaseDto entity) {
        this.entity = entity;
    }
}
