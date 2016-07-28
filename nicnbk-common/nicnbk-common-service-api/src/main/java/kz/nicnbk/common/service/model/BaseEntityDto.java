package kz.nicnbk.common.service.model;

import kz.nicnbk.repo.model.base.BaseEntity;

/**
 * Created by magzumov on 07.07.2016.
 */
public abstract class BaseEntityDto <T extends BaseEntity> implements BaseDto{
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
