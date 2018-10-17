package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.employee.Employee;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity
@Table(name = "ic_meeting_topic")
public class ICMeetingTopic extends CreateUpdateBaseEntity{

    private ICMeeting icMeeting;
    private String shortName;
    private String longDescription;

    private Boolean deleted;


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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}