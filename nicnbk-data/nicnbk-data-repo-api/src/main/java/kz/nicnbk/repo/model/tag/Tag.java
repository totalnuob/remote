package kz.nicnbk.repo.model.tag;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity
@Table(name = "tag")
public class Tag extends BaseEntity {

    private String name;

    public Tag(){}

    public Tag(Long id, String name){
        this.setId(id);
        this.name = name;
    }

    @Column(name="name", length = DataConstraints.C_COUNTRY_SHORT_NAME, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}