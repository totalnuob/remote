package kz.nicnbk.repo.model.m2s2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="hf_meeting_memo")
//@DiscriminatorValue(value = "4")
public class HedgeFundsMeetingMemo extends FundMeetingMemo {

    private String managementAndTeamNotes;
    private Short managementAndTeamScore;
    private String portfolioNotes;
    private Short portfolioScore;
    private String strategyNotes;
    private Short strategyScore;
    private String otherNotes;
    private String NICFollowups;
    private String otherPartyFollowups;
    private Short conviction;

    public HedgeFundsMeetingMemo(){
        setMemoType(HF_DISCRIMINATOR);
    }

    @Column(name="team_notes", columnDefinition = "TEXT")
    public String getManagementAndTeamNotes() {
        return managementAndTeamNotes;
    }

    public void setManagementAndTeamNotes(String managementAndTeamNotes) {
        this.managementAndTeamNotes = managementAndTeamNotes;
    }

    @Column(name="team_score")
    public Short getManagementAndTeamScore() {
        return managementAndTeamScore;
    }

    public void setManagementAndTeamScore(Short managementAndTeamScore) {
        this.managementAndTeamScore = managementAndTeamScore;
    }

    @Column(name="portfolio_notes", columnDefinition = "TEXT")
    public String getPortfolioNotes() {
        return portfolioNotes;
    }

    public void setPortfolioNotes(String portfolioNotes) {
        this.portfolioNotes = portfolioNotes;
    }

    @Column(name="portfolio_score")
    public Short getPortfolioScore() {
        return portfolioScore;
    }

    public void setPortfolioScore(Short portfolioScore) {
        this.portfolioScore = portfolioScore;
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
