package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_re_profit_loss_type")
public class REProfitLossType extends BaseTypeEntityImpl {

    private REProfitLossType parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public REProfitLossType getParent() {
        return parent;
    }

    public void setParent(REProfitLossType parent) {
        this.parent = parent;
    }
}
