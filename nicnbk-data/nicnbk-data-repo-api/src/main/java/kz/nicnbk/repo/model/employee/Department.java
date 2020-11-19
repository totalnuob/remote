package kz.nicnbk.repo.model.employee;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 24.02.2017.
 */

@Entity
@Table(name="department")
public class Department extends BaseTypeEntityImpl{

    private String nameUsedWithPositionRu;

    public Department(){}

    public Department(int id){
        setId(id);
    }

    @Column(name="name_used_with_position_ru")
    public String getNameUsedWithPositionRu() {
        return nameUsedWithPositionRu;
    }

    public void setNameUsedWithPositionRu(String nameUsedWithPositionRu) {
        this.nameUsedWithPositionRu = nameUsedWithPositionRu;
    }
}
