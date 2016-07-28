package kz.nicnbk.service.dto.m2s2;

/**
 * Created by magzumov on 11.07.2016.
 */
public class RealEstateMeetingMemoDto extends FundMeetingMemoDto {

    private String GPAndTeamNotes;
    private Short GPAndTeamScore;
    private String trackRecordNotes;
    private Short trackRecordScore;
    private String strategyNotes;
    private Short strategyScore;
    private String otherNotes;
    private String NICFollowups;
    private String otherPartyFollowups;
    private Short conviction;

    public String getGPAndTeamNotes() {
        return GPAndTeamNotes;
    }

    public void setGPAndTeamNotes(String GPAndTeamNotes) {
        this.GPAndTeamNotes = GPAndTeamNotes;
    }

    public Short getGPAndTeamScore() {
        return GPAndTeamScore;
    }

    public void setGPAndTeamScore(Short GPAndTeamScore) {
        this.GPAndTeamScore = GPAndTeamScore;
    }

    public String getTrackRecordNotes() {
        return trackRecordNotes;
    }

    public void setTrackRecordNotes(String trackRecordNotes) {
        this.trackRecordNotes = trackRecordNotes;
    }

    public Short getTrackRecordScore() {
        return trackRecordScore;
    }

    public void setTrackRecordScore(Short trackRecordScore) {
        this.trackRecordScore = trackRecordScore;
    }

    public String getStrategyNotes() {
        return strategyNotes;
    }

    public void setStrategyNotes(String strategyNotes) {
        this.strategyNotes = strategyNotes;
    }

    public Short getStrategyScore() {
        return strategyScore;
    }

    public void setStrategyScore(Short strategyScore) {
        this.strategyScore = strategyScore;
    }

    public String getOtherNotes() {
        return otherNotes;
    }

    public void setOtherNotes(String otherNotes) {
        this.otherNotes = otherNotes;
    }

    public String getNICFollowups() {
        return NICFollowups;
    }

    public void setNICFollowups(String NICFollowups) {
        this.NICFollowups = NICFollowups;
    }

    public String getOtherPartyFollowups() {
        return otherPartyFollowups;
    }

    public void setOtherPartyFollowups(String otherPartyFollowups) {
        this.otherPartyFollowups = otherPartyFollowups;
    }

    public Short getConviction() {
        return conviction;
    }

    public void setConviction(Short conviction) {
        this.conviction = conviction;
    }
}
