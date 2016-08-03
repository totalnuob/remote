package kz.nicnbk.repo.model.m2s2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by magzumov on 04.07.2016.
 */


@Entity
@Table(name="gen_meeting_memo")
//@DiscriminatorValue(value = "1")
public class GeneralMeetingMemo extends MeetingMemo {

    private String topic1;
    private String topic2;
    private String topic3;
    private String otherNotes;
    private String nicFollowups;
    private String otherPartyFollowups;

    public GeneralMeetingMemo(){
        setMemoType(GENERAL_DISCRIMINATOR);
    }

    @Column(name="topic1_notes", columnDefinition = "TEXT")
    public String getTopic1() {
        return topic1;
    }

    public void setTopic1(String topic1) {
        this.topic1 = topic1;
    }

    @Column(name="topic2_notes", columnDefinition = "TEXT")
    public String getTopic2() {
        return topic2;
    }

    public void setTopic2(String topic2) {
        this.topic2 = topic2;
    }

    @Column(name="topic3_notes", columnDefinition = "TEXT")
    public String getTopic3() {
        return topic3;
    }

    public void setTopic3(String topic3) {
        this.topic3 = topic3;
    }

    @Column(name="other_notes", columnDefinition = "TEXT")
    public String getOtherNotes() {
        return otherNotes;
    }

    public void setOtherNotes(String otherNotes) {
        this.otherNotes = otherNotes;
    }

    @Column(name="nic_followups", columnDefinition = "TEXT")
    public String getNicFollowups() {
        return nicFollowups;
    }

    public void setNicFollowups(String nicFollowups) {
        this.nicFollowups = nicFollowups;
    }

    @Column(name="other_followups", columnDefinition = "TEXT")
    public String getOtherPartyFollowups() {
        return otherPartyFollowups;
    }

    public void setOtherPartyFollowups(String otherPartyFollowups) {
        this.otherPartyFollowups = otherPartyFollowups;
    }
}
