package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_pe_operations_type")
public class PEOperationsType extends BaseTypeEntityImpl {

    private PEOperationsType parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public PEOperationsType getParent() {
        return parent;
    }

    public void setParent(PEOperationsType parent) {
        this.parent = parent;
    }
}
