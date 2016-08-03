package kz.nicnbk.repo.model.m2s2;

import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Currency;

import javax.persistence.*;

/**
 * Created by magzumov on 04.07.2016.
 */

@MappedSuperclass
public class FundMeetingMemo extends MeetingMemo{

//    private String fundName;
    private Boolean currentlyFundRaising;
    private String closingSchedule;
    private Boolean openingSoon;
    private String openingSchedule;
    private Double fundSize;
    private Currency fundSizeCurrency;
    private Double preInvested;
    private Boolean suitable;
    private String nonsuitableReason;

    @Column(name="currently_fund_raising")
    public Boolean getCurrentlyFundRaising() {
        return currentlyFundRaising;
    }

    public void setCurrentlyFundRaising(Boolean currentlyFundRaising) {
        this.currentlyFundRaising = currentlyFundRaising;
    }

    @Column(name="closing_schedule", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getClosingSchedule() {
        return closingSchedule;
    }

    public void setClosingSchedule(String closingSchedule) {
        this.closingSchedule = closingSchedule;
    }

    @Column(name="opening_soon")
    public Boolean getOpeningSoon() {
        return openingSoon;
    }

    public void setOpeningSoon(Boolean openingSoon) {
        this.openingSoon = openingSoon;
    }

    @Column(name="opening_schedule", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getOpeningSchedule() {
        return openingSchedule;
    }

    public void setOpeningSchedule(String openingSchedule) {
        this.openingSchedule = openingSchedule;
    }

    @Column(name="fund_size")
    public Double getFundSize() {
        return fundSize;
    }

    public void setFundSize(Double fundSize) {
        this.fundSize = fundSize;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_size_currency_id")
    public Currency getFundSizeCurrency() {
        return fundSizeCurrency;
    }

    public void setFundSizeCurrency(Currency fundSizeCurrency) {
        this.fundSizeCurrency = fundSizeCurrency;
    }

    @Column(name="preinvested")
    public Double getPreInvested() {
        return preInvested;
    }

    public void setPreInvested(Double preInvested) {
        this.preInvested = preInvested;
    }

    public Boolean getSuitable() {
        return suitable;
    }

    public void setSuitable(Boolean suitable) {
        this.suitable = suitable;
    }

    @Column(name="nonsuitable_desc", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getNonsuitableReason() {
        return nonsuitableReason;
    }

    public void setNonsuitableReason(String nonsuitableReason) {
        this.nonsuitableReason = nonsuitableReason;
    }
}
