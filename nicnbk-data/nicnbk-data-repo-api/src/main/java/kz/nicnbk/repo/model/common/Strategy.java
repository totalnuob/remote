package kz.nicnbk.repo.model.common;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

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

    private Strategy parent;

    @Column(name="group_type")
    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public Strategy getParent() {
        return parent;
    }

    public void setParent(Strategy parent) {
        this.parent = parent;
    }
}
