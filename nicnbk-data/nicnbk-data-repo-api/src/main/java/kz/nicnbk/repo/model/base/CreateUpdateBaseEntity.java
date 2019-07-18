package kz.nicnbk.repo.model.base;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.markers.UpdateDate;
import kz.nicnbk.repo.model.markers.Updater;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class CreateUpdateBaseEntity extends CreatorBaseEntity implements UpdateDate, Updater<Employee> {

	private Date updateDate;
    private Employee updater;

    @Column(name = "Updated")
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    @JoinColumn(name = "UpdatedBy"/*, nullable = false*/)
    @ManyToOne(fetch = FetchType.LAZY)
    public Employee getUpdater() {
        return updater;
    }

    @Override
    public void setUpdater(Employee updater) {
        this.updater = updater;
    }

}
