package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by magzumov on 15.12.2016.
 */
@Entity(name="hf_investor_base")
public class InvestorBase extends BaseEntity {

    private String category;
    private String fund;

    @Column(name="category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Column(name="fund")
    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }
}
