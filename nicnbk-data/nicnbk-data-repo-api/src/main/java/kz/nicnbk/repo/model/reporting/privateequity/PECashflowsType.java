package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_pe_cashflows_type")
public class PECashflowsType extends BaseTypeEntityImpl {

    private PECashflowsType parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public PECashflowsType getParent() {
        return parent;
    }

    public void setParent(PECashflowsType parent) {
        this.parent = parent;
    }
}
