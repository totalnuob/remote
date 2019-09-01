package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;

/**
 * Created by magzumov on 08.07.2016.
 */
public class PositionDto extends BaseDictionaryDto {

    private BaseDictionaryDto department;

    public PositionDto(){}

    public PositionDto(Integer id, String code, String nameRu, String nameEn, String nameKz, BaseDictionaryDto department){
        setId(id);
        setCode(code);
        setNameEn(nameEn);
        setNameRu(nameRu);
        setNameKz(nameKz);
        setDepartment(department);
    }

    public BaseDictionaryDto getDepartment() {
        return department;
    }

    public void setDepartment(BaseDictionaryDto department) {
        this.department = department;
    }
}
