package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_fin_statement_type")
public class FinancialStatementCategory extends BaseTypeEntityImpl {

    private FinancialStatementCategory parent;

    public void setParent(FinancialStatementCategory parent) {
        this.parent = parent;
    }
}
