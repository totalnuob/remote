package kz.nicnbk.common.service.model;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import java.util.Date;

/**
 * Created by timur on 23.10.2016.
 */
public abstract class CreateUpdateBaseEntityDto<T extends CreateUpdateBaseEntity> extends HistoryBaseEntityDto{

    private Date updateDate;

    private String updater;

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}
