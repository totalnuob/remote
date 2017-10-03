package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_hf_fin_statement_type")
public class HFFinancialStatementType extends BaseTypeEntityImpl {

    private HFFinancialStatementType parent;

    public void setParent(HFFinancialStatementType parent) {
        this.parent = parent;
    }
}
