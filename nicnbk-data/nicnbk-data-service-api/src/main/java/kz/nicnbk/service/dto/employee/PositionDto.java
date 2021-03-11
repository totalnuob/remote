package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;

/**
 * Created by magzumov on 08.07.2016.
 */
public class PositionDto extends BaseDictionaryDto {

    private DepartmentDto department;
    private Boolean head;

    public PositionDto(){}

    public PositionDto(Integer id, String code, String nameRu, String nameEn, String nameKz, DepartmentDto department){
        setId(id);
        setCode(code);
        setNameEn(nameEn);
        setNameRu(nameRu);
        setNameKz(nameKz);
        setDepartment(department);
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDto department) {
        this.department = department;
    }

    public Boolean isHead() {
        return head;
    }

    public void setHead(Boolean head) {
        this.head = head;
    }
}
