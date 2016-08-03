package kz.nicnbk.service.dto.m2s2;

/**
 * Created by magzumov on 11.07.2016.
 */
public class FundMeetingMemoDto extends MeetingMemoDto {

    //private String fundName;
    private Boolean currentlyFundRaising;
    private String closingSchedule;
    private Boolean openingSoon;
    private String openingSchedule;
    private Double fundSize;
    private String fundSizeCurrency;
    private Double preInvested;
    private Boolean suitable;
    private String nonsuitableReason;

//    public String getFundName() {
//        return fundName;
//    }
//
//    public void setFundName(String fundName) {
//        this.fundName = fundName;
//    }

    public Boolean getCurrentlyFundRaising() {
        return currentlyFundRaising;
    }

    public void setCurrentlyFundRaising(Boolean currentlyFundRaising) {
        this.currentlyFundRaising = currentlyFundRaising;
    }

    public String getClosingSchedule() {
        return closingSchedule;
    }

    public void setClosingSchedule(String closingSchedule) {
        this.closingSchedule = closingSchedule;
    }

    public Boolean getOpeningSoon() {
        return openingSoon;
    }

    public void setOpeningSoon(Boolean openingSoon) {
        this.openingSoon = openingSoon;
    }

    public String getOpeningSchedule() {
        return openingSchedule;
    }

    public void setOpeningSchedule(String openingSchedule) {
        this.openingSchedule = openingSchedule;
    }

    public Double getFundSize() {
        return fundSize;
    }

    public void setFundSize(Double fundSize) {
        this.fundSize = fundSize;
    }

    public String getFundSizeCurrency() {
        return fundSizeCurrency;
    }

    public void setFundSizeCurrency(String fundSizeCurrency) {
        this.fundSizeCurrency = fundSizeCurrency;
    }

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

    public String getNonsuitableReason() {
        return nonsuitableReason;
    }

    public void setNonsuitableReason(String nonsuitableReason) {
        this.nonsuitableReason = nonsuitableReason;
    }
}
