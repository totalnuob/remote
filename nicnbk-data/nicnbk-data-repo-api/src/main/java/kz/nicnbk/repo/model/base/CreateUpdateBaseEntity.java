package kz.nicnbk.repo.model.base;

import kz.nicnbk.repo.model.markers.UpdateDate;
import kz.nicnbk.repo.model.markers.Updater;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
public abstract class CreateUpdateBaseEntity extends CreatorBaseEntity implements UpdateDate, Updater<Long> {

	private Date updateDate;

    // TODO: User
    private Long updater;

    @Column(name = "Updated")
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    @Column(name = "UpdatedBy")
    public Long getUpdater() {
        return updater;
    }

    @Override
    public void setUpdater(Long updater) {
        this.updater = updater;
    }

    // TODO: User
//    @ManyToOne(fetch= FetchType.LAZY)
//    @JoinColumn(name = "UpdatedBy")
//    public User getUpdater() {
//        return updater;
//    }
//
//    public void setUpdater(User updater) {
//        this.updater = updater;
//    }

}
