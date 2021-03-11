package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.employee.Department;

/**
 * Created by pak on 06.02.2020.
 */
public class DepartmentDto extends BaseDictionaryDto {

    private String nameUsedWithPositionRu;
    private String shortNameRu;

    public DepartmentDto(){}

    public DepartmentDto(Department entity){
        setId(entity.getId());
        setCode(entity.getCode());
        setNameRu(entity.getNameRu());
        setNameEn(entity.getNameEn());
        setNameUsedWithPositionRu(entity.getNameUsedWithPositionRu());
        setShortNameRu(entity.getShortNameRu());
    }

    public String getNameUsedWithPositionRu() {
        return nameUsedWithPositionRu;
    }

    public void setNameUsedWithPositionRu(String nameUsedWithPositionRu) {
        this.nameUsedWithPositionRu = nameUsedWithPositionRu;
    }

    public String getShortNameRu() {
        return shortNameRu;
    }

    public void setShortNameRu(String shortNameRu) {
        this.shortNameRu = shortNameRu;
    }
}
