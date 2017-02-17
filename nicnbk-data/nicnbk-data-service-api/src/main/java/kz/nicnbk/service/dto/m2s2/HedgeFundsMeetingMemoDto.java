package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.hf.HedgeFundDto;

/**
 * Created by magzumov on 11.07.2016.
 */
public class HedgeFundsMeetingMemoDto extends FundMeetingMemoDto {

    private String managementAndTeamNotes;
    private Short managementAndTeamScore;
    private String portfolioNotes;
    private Short portfolioScore;
    private String strategyNotes;
    private Short strategyScore;
    private String otherNotes;
    private String nicFollowups;
    private String otherPartyFollowups;
    private Short conviction;

    private HFManagerDto manager;
    private HedgeFundDto fund;

    public String getManagementAndTeamNotes() {
        return managementAndTeamNotes;
    }

    public void setManagementAndTeamNotes(String managementAndTeamNotes) {
        this.managementAndTeamNotes = managementAndTeamNotes;
    }

    public Short getManagementAndTeamScore() {
        return managementAndTeamScore;
    }

    public void setManagementAndTeamScore(Short managementAndTeamScore) {
        this.managementAndTeamScore = managementAndTeamScore;
    }

    public String getPortfolioNotes() {
        return portfolioNotes;
    }

    public void setPortfolioNotes(String portfolioNotes) {
        this.portfolioNotes = portfolioNotes;
    }

    public Short getPortfolioScore() {
        return portfolioScore;
    }

    public void setPortfolioScore(Short portfolioScore) {
        this.portfolioScore = portfolioScore;
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

    public HFManagerDto getManager() {
        return manager;
    }

    public void setManager(HFManagerDto manager) {
        this.manager = manager;
    }

    public HedgeFundDto getFund() {
        return fund;
    }

    public void setFund(HedgeFundDto fund) {
        this.fund = fund;
    }
}
