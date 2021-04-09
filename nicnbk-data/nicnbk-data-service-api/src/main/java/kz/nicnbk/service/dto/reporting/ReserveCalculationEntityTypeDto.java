package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;

public class ReserveCalculationEntityTypeDto extends BaseDictionaryDto {
    private boolean deleted;

    public ReserveCalculationEntityTypeDto(){}

    public ReserveCalculationEntityTypeDto(BaseDictionaryDto dto){
        setId(dto.getId());
        setCode(dto.getCode());
        setNameEn(dto.getNameEn());
        setNameRu(dto.getNameRu());
        setNameKz(dto.getNameKz());
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
