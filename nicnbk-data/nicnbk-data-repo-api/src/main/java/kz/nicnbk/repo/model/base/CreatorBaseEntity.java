package kz.nicnbk.repo.model.base;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.markers.Creator;

import javax.persistence.*;

@MappedSuperclass
public abstract class CreatorBaseEntity extends HistoryBaseEntity implements Creator<Employee> {

    // TODO: User
    //private Long creator;

    private Employee creator;

    // TODO: User
    @Override
    @JoinColumn(name = "InsertedBy"/*, nullable = false*/)
    @ManyToOne(fetch = FetchType.LAZY)
    public Employee getCreator() {
        return creator;
    }

    @Override
    public void setCreator(Employee creator) {
        this.creator = creator;
    }


//    @Override
//    @Column(name = "InsertedBy")
//    public Long getCreator() {
//        return creator;
//    }
//
//    @Override
//    public void setCreator(Long creator) {
//        this.creator = creator;
//    }

}
