package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "pe_balance_type")
public class PEBalanceType extends BaseTypeEntityImpl {

    private PEBalanceType parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public PEBalanceType getParent() {
        return parent;
    }

    public void setParent(PEBalanceType parent) {
        this.parent = parent;
    }
}
