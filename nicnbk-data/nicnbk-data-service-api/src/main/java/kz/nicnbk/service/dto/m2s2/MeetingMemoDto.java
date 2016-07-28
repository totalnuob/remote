package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;

import java.util.Date;
import java.util.Set;

/**
 * Created by magzumov on 08.07.2016.
 */
public class MeetingMemoDto extends BaseEntityDto<MeetingMemo> {

    //private String memoType;
    private int memoType;

    private String meetingType;
    private String otherPartyName;
    private Date meetingDate;
    private String meetingTime;
    private String arrangedBy;
    private String arrangedByDescription;
    private String meetingLocation;
    private String purpose;
    private Set<EmployeeDto> attendeesNIC;
    private String attendeesNICDescription;
    private String attendeesOtherDescription;
    private Set<FilesDto> files;

    // TODO: TEMP in place of authentication
    private String author;

    // TODO: refactor?
    private Set<BaseDictionaryDto> strategies;
    private Set<BaseDictionaryDto> geographies;

    public int getMemoType() {
        return memoType;
    }

    public void setMemoType(int memoType) {
        this.memoType = memoType;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getOtherPartyName() {
        return otherPartyName;
    }

    public void setOtherPartyName(String otherPartyName) {
        this.otherPartyName = otherPartyName;
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getArrangedBy() {
        return arrangedBy;
    }

    public void setArrangedBy(String arrangedBy) {
        this.arrangedBy = arrangedBy;
    }

    public String getArrangedByDescription() {
        return arrangedByDescription;
    }

    public void setArrangedByDescription(String arrangedByDescription) {
        this.arrangedByDescription = arrangedByDescription;
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Set<EmployeeDto> getAttendeesNIC() {
        return attendeesNIC;
    }

    public void setAttendeesNIC(Set<EmployeeDto> attendeesNIC) {
        this.attendeesNIC = attendeesNIC;
    }

    public String getAttendeesNICDescription() {
        return attendeesNICDescription;
    }

    public void setAttendeesNICDescription(String attendeesNICDescription) {
        this.attendeesNICDescription = attendeesNICDescription;
    }

    public String getAttendeesOtherDescription() {
        return attendeesOtherDescription;
    }

    public void setAttendeesOtherDescription(String attendeesOtherDescription) {
        this.attendeesOtherDescription = attendeesOtherDescription;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<BaseDictionaryDto> getStrategies() {
        return strategies;
    }

    public void setStrategies(Set<BaseDictionaryDto> strategies) {
        this.strategies = strategies;
    }

    public Set<BaseDictionaryDto> getGeographies() {
        return geographies;
    }

    public void setGeographies(Set<BaseDictionaryDto> geographies) {
        this.geographies = geographies;
    }

    public Set<FilesDto> getFiles() {
        return files;
    }

    public void setFiles(Set<FilesDto> files) {
        this.files = files;
    }
}
