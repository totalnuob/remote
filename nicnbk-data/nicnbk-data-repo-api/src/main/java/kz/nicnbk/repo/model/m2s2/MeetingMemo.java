package kz.nicnbk.repo.model.m2s2;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.employee.Employee;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "meeting_memo")
@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "memo_type", discriminatorType = DiscriminatorType.INTEGER)
public class MeetingMemo extends CreateUpdateBaseEntity{

    public static final int GENERAL_DISCRIMINATOR = 1;
    public static final int PE_DISCRIMINATOR = 2;
    public static final int HF_DISCRIMINATOR = 3;
    public static final int RE_DISCRIMINATOR = 4;


    //private MemoType memoType;
    private int memoType;

    private MeetingType meetingType;

    /* Meeting/call with whom */
    private String firmName;

    private String fundName;

    private Date meetingDate;

    private String meetingTime;

    private MeetingArrangedBy arrangedBy;

    private String arrangedByDescription;

    private String meetingLocation;

    private String purpose;

    private Set<Employee> attendeesNIC;

    private String attendeesNICOther;

    private String attendeesOther;

    // TODO: TEMP in place of authentication
    private String author;

    private String tags;

    private boolean isDeleted;

    // TODO: refactor?
    private Set<Strategy> strategies;
    private Set<Geography> geographies;

    @Column (name = "memo_type")
    public int getMemoType() {
        return memoType;
    }

    void setMemoType(int memoType) {
        this.memoType = memoType;
    }


    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memo_type")
//    public MemoType getMemoType() {
//        return memoType;
//    }
//
//    public void setMemoType(MemoType memoType) {
//        this.memoType = memoType;
//    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meeting_type_id")
    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

    @Column(name="other_party", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    @Column(name="meeting_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    @Column(name="meeting_time", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrangedby_id")
    public MeetingArrangedBy getArrangedBy() {
        return arrangedBy;
    }

    public void setArrangedBy(MeetingArrangedBy arrangedBy) {
        this.arrangedBy = arrangedBy;
    }

    @Column(name="arrangedby_desc", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getArrangedByDescription() {
        return arrangedByDescription;
    }

    public void setArrangedByDescription(String arrangedByDescription) {
        this.arrangedByDescription = arrangedByDescription;
    }

    @Column(name="meeting_location", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }


    @Column(name="purpose", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="memo_attendees_nic",
            joinColumns=
            @JoinColumn(name="memo_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="employee_id", referencedColumnName="ID")
    )
    public Set<Employee> getAttendeesNIC() {
        return attendeesNIC;
    }

    public void setAttendeesNIC(Set<Employee> attendeesNIC) {
        this.attendeesNIC = attendeesNIC;
    }

    @Column(name="attendees_nic_desc", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getAttendeesNICOther() {
        return attendeesNICOther;
    }

    public void setAttendeesNICOther(String attendeesNICOther) {
        this.attendeesNICOther = attendeesNICOther;
    }


    @Column(name="attendees_other_desc", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getAttendeesOther() {
        return attendeesOther;
    }

    public void setAttendeesOther(String attendeesOther) {
        this.attendeesOther = attendeesOther;
    }


    // TODO: TEMP in place of authentication
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="memo_fund_strategies",
            joinColumns=
            @JoinColumn(name="memo_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="strategy_id", referencedColumnName="ID")
    )
    public Set<Strategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(Set<Strategy> strategies) {
        this.strategies = strategies;
    }

    @ManyToMany(fetch = FetchType.LAZY  )
    @JoinTable(
            name="memo_fund_geographies",
            joinColumns=
            @JoinColumn(name="memo_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="geography_id", referencedColumnName="ID")
    )
    public Set<Geography> getGeographies() {
        return geographies;
    }

    public void setGeographies(Set<Geography> geographies) {
        this.geographies = geographies;
    }

    @Column(name="fund_name", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name="tags", columnDefinition="TEXT")
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
