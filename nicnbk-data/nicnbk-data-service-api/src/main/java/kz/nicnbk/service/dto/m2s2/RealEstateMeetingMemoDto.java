package kz.nicnbk.service.dto.m2s2;

/**
 * Created by magzumov on 11.07.2016.
 */
public class RealEstateMeetingMemoDto extends FundMeetingMemoDto {

    private String teamNotes;
    private Short teamScore;
    private String trackRecordNotes;
    private Short trackRecordScore;
    private String strategyNotes;
    private Short strategyScore;
    private String otherNotes;
    private String nicFollowups;
    private String otherPartyFollowups;
    private Short conviction;

    public String getTeamNotes() {
        return teamNotes;
    }

    public void setTeamNotes(String teamNotes) {
        this.teamNotes = teamNotes;
    }

    public Short getTeamScore() {
        return teamScore;
    }

    public void setTeamScore(Short teamScore) {
        this.teamScore = teamScore;
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

    public String getNicFollowups() {
        return nicFollowups;
    }

    public void setNicFollowups(String nicFollowups) {
        this.nicFollowups = nicFollowups;
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
