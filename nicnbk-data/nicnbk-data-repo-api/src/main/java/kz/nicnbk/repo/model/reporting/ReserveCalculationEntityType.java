package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_reserve_calc_entity_type")
public class ReserveCalculationEntityType extends BaseTypeEntityImpl {
    private boolean deleted;

    @Column(name="deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
