package kz.nicnbk.repo.model.reporting.realestate;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Deprecated //
@Entity
@Table(name = "rep_re_chart_accounts_type")
public class REChartOfAccountsType extends BaseTypeEntityImpl {

    private REChartOfAccountsType parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    public REChartOfAccountsType getParent() {
        return parent;
    }

    public void setParent(REChartOfAccountsType parent) {
        this.parent = parent;
    }
}
