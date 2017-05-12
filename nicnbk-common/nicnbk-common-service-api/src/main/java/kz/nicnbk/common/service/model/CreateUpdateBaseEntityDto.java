package kz.nicnbk.common.service.model;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import java.util.Date;

/**
 * Created by timur on 23.10.2016.
 */
public abstract class CreateUpdateBaseEntityDto<T extends CreateUpdateBaseEntity> extends HistoryBaseEntityDto{

    private String creator;
    private String updater;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}
