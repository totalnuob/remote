package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_re_balance_type")
public class REBalanceType extends BaseTypeEntityImpl {

    private REBalanceType parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public REBalanceType getParent() {
        return parent;
    }

    public void setParent(REBalanceType parent) {
        this.parent = parent;
    }
}
