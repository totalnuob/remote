package kz.nicnbk.repo.model.m2s2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="infr_meeting_memo")
//@DiscriminatorValue(value = "5")
public class InfrastructureMeetingMemo extends FundMeetingMemo {

    private String teamNotes;
    private Short teamScore;
    private String trackRecordNotes;
    private Short trackRecordScore;
    private String strategyNotes;
    private Short strategyScore;
    private String otherNotes;
    private String NICFollowups;
    private String otherPartyFollowups;
    private Short conviction;

    public InfrastructureMeetingMemo(){
        setMemoType(INFR_DISCRIMINATOR);
    }

    @Column(name="team_notes", columnDefinition = "TEXT")
    public String getTeamNotes() {
        return teamNotes;
    }

    public void setTeamNotes(String teamNotes) {
        this.teamNotes = teamNotes;
    }

    @Column(name="team_score")
    public Short getTeamScore() {
        return teamScore;
    }

    public void setTeamScore(Short teamScore) {
        this.teamScore = teamScore;
    }

    @Column(name="track_record_notes", columnDefinition = "TEXT")
    public String getTrackRecordNotes() {
        return trackRecordNotes;
    }

    public void setTrackRecordNotes(String trackRecordNotes) {
        this.trackRecordNotes = trackRecordNotes;
    }

    @Column(name="track_record_score")
    public Short getTrackRecordScore() {
        return trackRecordScore;
    }

    public void setTrackRecordScore(Short trackRecordScore) {
        this.trackRecordScore = trackRecordScore;
    }

    @Column(name="strategy_notes", columnDefinition = "TEXT")
    public String getStrategyNotes() {
        return strategyNotes;
    }

    public void setStrategyNotes(String strategyNotes) {
        this.strategyNotes = strategyNotes;
    }

    @Column(name="strategy_score")
    public Short getStrategyScore() {
        return strategyScore;
    }

    public void setStrategyScore(Short strategyScore) {
        this.strategyScore = strategyScore;
    }

    @Column(name="other_notes", columnDefinition = "TEXT")
    public String getOtherNotes() {
        return otherNotes;
    }

    public void setOtherNotes(String otherNotes) {
        this.otherNotes = otherNotes;
    }

    @Column(name="nic_followups", columnDefinition = "TEXT")
    public String getNICFollowups() {
        return NICFollowups;
    }

    public void setNICFollowups(String NICFollowups) {
        this.NICFollowups = NICFollowups;
    }

    @Column(name="other_followups", columnDefinition = "TEXT")
    public String getOtherPartyFollowups() {
        return otherPartyFollowups;
    }

    public void setOtherPartyFollowups(String otherPartyFollowups) {
        this.otherPartyFollowups = otherPartyFollowups;
    }

    @Column(name="conviction")
    public Short getConviction() {
        return conviction;
    }

    public void setConviction(Short conviction) {
        this.conviction = conviction;
    }
}
