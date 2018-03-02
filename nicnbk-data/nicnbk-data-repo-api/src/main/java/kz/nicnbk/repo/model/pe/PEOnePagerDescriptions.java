package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by Pak on 02/03/2018.
 */
@Entity(name = "pe_one_pager_descriptions")
public class PEOnePagerDescriptions extends BaseEntity {

    private String description;
    private int type;
    private PEFund fund;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fund_id", nullable = false)
    public PEFund getFund() {
        return fund;
    }

    public void setFund(PEFund fund) {
        this.fund = fund;
    }
}
