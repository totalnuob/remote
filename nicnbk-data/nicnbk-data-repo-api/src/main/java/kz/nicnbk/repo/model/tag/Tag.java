package kz.nicnbk.repo.model.tag;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.TagType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity
@Table(name = "tag")
public class Tag extends BaseEntity {

    private String name;
    private TagType type;

    public Tag(){}

    public Tag(Long id){
        this.setId(id);
    }

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    public TagType getType() {
        return type;
    }

    public void setType(TagType type) {
        this.type = type;
    }
}