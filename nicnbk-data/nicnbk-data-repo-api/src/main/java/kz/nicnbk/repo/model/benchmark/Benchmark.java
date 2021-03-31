package kz.nicnbk.repo.model.benchmark;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 05.01.2017.
 */

@Entity
@Table(name = "benchmark")
public class Benchmark extends BaseTypeEntityImpl {

    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
