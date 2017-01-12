package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by timur on 27.10.2016.
 */

@Entity
@Table(name = "hf_management")
public class HFManagement extends BaseEntity {
    private String name;
    private String description;
    private String started;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column (name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column (name = "started")
    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }
}
