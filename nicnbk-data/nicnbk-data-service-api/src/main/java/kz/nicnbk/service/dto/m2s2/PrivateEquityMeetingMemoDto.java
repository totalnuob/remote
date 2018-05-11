package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.repo.model.pe.PEFirm;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEFundDto;

/**
 * Created by magzumov on 11.07.2016.
 */
public class PrivateEquityMeetingMemoDto extends FundMeetingMemoDto {

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

    private String memoSummary;

    private PEFirmDto firm;
    private PEFundDto fund;

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

    public PEFirmDto getFirm() {
        return firm;
    }

    public void setFirm(PEFirmDto firm) {
        this.firm = firm;
    }

    public PEFundDto getFund() {
        return fund;
    }

    public void setFund(PEFundDto fund) {
        this.fund = fund;
    }

    public String getMemoSummary() {
        return memoSummary;
    }

    public void setMemoSummary(String memoSummary) {
        this.memoSummary = memoSummary;
    }
}
