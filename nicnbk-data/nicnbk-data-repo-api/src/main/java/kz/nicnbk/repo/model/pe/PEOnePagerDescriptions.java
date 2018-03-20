package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;

/**
 * Created by Pak on 02/03/2018.
 */
@Entity(name = "pe_one_pager_descriptions")
public class PEOnePagerDescriptions extends BaseEntity {

    private String descriptionBold;
    private String description;
    private int type;
    private PEFund fund;

    @Column(columnDefinition = "TEXT")
    public String getDescriptionBold() {
        return descriptionBold;
    }

    public void setDescriptionBold(String descriptionBold) {
        this.descriptionBold = descriptionBold;
    }

    @Column(columnDefinition = "TEXT")
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
