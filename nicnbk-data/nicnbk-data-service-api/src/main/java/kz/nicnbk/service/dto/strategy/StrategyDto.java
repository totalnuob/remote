package kz.nicnbk.service.dto.strategy;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.common.Strategy;


public class StrategyDto extends BaseDictionaryDto {

    private int groupType;

    public StrategyDto(){}

    public StrategyDto(BaseDictionaryDto dto, int groupType){
        setId(dto.getId());
        setCode(dto.getCode());
        setNameEn(dto.getNameEn());
        setNameRu(dto.getNameRu());
        setNameKz(dto.getNameKz());
        setParent(dto.getParent());
        setGroupType(groupType);
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }
}
