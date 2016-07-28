package kz.nicnbk.repo.model.base;

import kz.nicnbk.repo.model.markers.Identity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Base entity with surrogate primary key.
 *
 * Created by magzumov on 29.06.2016.
 */
@MappedSuperclass
public class IdentifiableEntity implements Identity<Long>, Serializable {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
