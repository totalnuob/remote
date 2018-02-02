package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 18.07.2017.
 */



@Entity
@Table(name = "rep_nic_chart_of_accounts")
public class NICReportingChartOfAccounts extends BaseTypeEntityImpl {

    private NBChartOfAccounts nbChartOfAccounts;

    @ManyToOne
    @JoinColumn(name = "nb_chart_accounts_id")
    public NBChartOfAccounts getNbChartOfAccounts() {
        return nbChartOfAccounts;
    }

    public void setNbChartOfAccounts(NBChartOfAccounts nbChartOfAccounts) {
        this.nbChartOfAccounts = nbChartOfAccounts;
    }

}
