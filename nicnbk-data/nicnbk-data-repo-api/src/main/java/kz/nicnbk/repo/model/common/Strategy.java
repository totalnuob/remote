package kz.nicnbk.repo.model.common;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "strategy")
public class Strategy extends BaseTypeEntityImpl {

    // TODO: refactor as lookup/ entity
    public static final int TYPE_PRIVATE_EQUITY = 1;
    public static final int TYPE_HEDGE_FUNDS = 2;
    public static final int TYPE_REAL_ESTATE = 3;

    // TODO: typed entity (lookup)
    private Integer groupType;

    @Column(name="group_type")
    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }
}
