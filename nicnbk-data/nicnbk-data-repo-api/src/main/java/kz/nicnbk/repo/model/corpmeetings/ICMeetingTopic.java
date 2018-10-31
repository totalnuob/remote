package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.tag.Tag;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity
@Table(name = "ic_meeting_topic")
public class ICMeetingTopic extends CreateUpdateBaseEntity{

    private ICMeeting icMeeting;
    private String shortName;
    private String longDescription;
    private String decision;
    private ICMeetingTopicType type;

    private Boolean deleted;

    private List<Tag> tags;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ic_meeting_id")
    public ICMeeting getIcMeeting() {
        return icMeeting;
    }

    public void setIcMeeting(ICMeeting icMeeting) {
        this.icMeeting = icMeeting;
    }

    @Column(name="short_name", columnDefinition="TEXT")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name="long_description", columnDefinition="TEXT")
    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Column(name="decision", columnDefinition="TEXT")
    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "icmeetingtopic_tag",
            joinColumns = @JoinColumn(name = "meeting_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id",
                    referencedColumnName = "id"))
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    public ICMeetingTopicType getType() {
        return type;
    }

    public void setType(ICMeetingTopicType type) {
        this.type = type;
    }
}