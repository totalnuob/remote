package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_hf_chart_accounts_type")
public class HFChartOfAccountsType extends BaseTypeEntityImpl {

    private HFChartOfAccountsType parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public HFChartOfAccountsType getParent() {
        return parent;
    }

    public void setParent(HFChartOfAccountsType parent) {
        this.parent = parent;
    }
}
