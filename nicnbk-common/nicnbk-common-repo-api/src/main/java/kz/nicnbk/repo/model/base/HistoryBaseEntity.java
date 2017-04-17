package kz.nicnbk.repo.model.base;

import kz.nicnbk.repo.model.markers.CreationDate;
import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class HistoryBaseEntity extends BaseEntity implements CreationDate {

    private Date creationDate;

    @Column(name = "Inserted"/*, nullable = false*/)
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @PrePersist
    private void updateCreationDate() {
        setCreationDate(new Date());
    }

}
