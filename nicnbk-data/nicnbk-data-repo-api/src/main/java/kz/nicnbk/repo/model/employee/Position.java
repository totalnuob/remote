package kz.nicnbk.repo.model.employee;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;
import javax.persistence.*;

/**
 * Created by magzumov on 24.02.2017.
 */

@Entity
@Table(name="position")
public class Position extends BaseTypeEntityImpl {

    private Department department;
    private Boolean head;
    private String nameRuPossessive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "is_head")
    public Boolean isHead() {
        return head;
    }

    public void setHead(Boolean head) {
        this.head = head;
    }

    @Column(name = "name_ru_possessive")
    public String getNameRuPossessive() {
        return nameRuPossessive;
    }

    public void setNameRuPossessive(String nameRuPossessive) {
        this.nameRuPossessive = nameRuPossessive;
    }
}
