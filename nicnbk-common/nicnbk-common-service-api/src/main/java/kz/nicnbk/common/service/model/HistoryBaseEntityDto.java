package kz.nicnbk.common.service.model;

import kz.nicnbk.repo.model.base.HistoryBaseEntity;

import java.util.Date;

/**
 * Created by timur on 23.10.2016.
 */
public abstract class HistoryBaseEntityDto<T extends HistoryBaseEntity> extends BaseEntityDto{

    private Date creationDate;

    private Date updateDate;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
