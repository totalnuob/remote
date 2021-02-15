package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.repo.model.corpmeetings.ICMeeting;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04-Aug-16.
 */
public class ICMeetingDto extends CreateUpdateBaseEntityDto<ICMeeting> implements Comparable {

    public static final String OPEN = "OPEN";
    public static final String LOCKED_FOR_IC = "LOCKED FOR IC";
    public static final String TO_BE_FINALIZED = "TO BE FINALIZED";
    public static final String FINALIZED = "FINALIZED";
    public static final String CLOSED = "CLOSED";
    public static final String DELETED = "DELETED";

    private String number;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;
    private String time;
    private String place;

    private List<ICMeetingAttendeesDto> attendees;
    private List<EmployeeDto> invitees;
    private List<ICMeetingTopicDto> topics;
    private FilesDto agenda;
    private FilesDto protocol;
    private FilesDto bulletin;

    private Boolean unlockedForFinalize;
    private Boolean closed;
    private Boolean deleted;

    private boolean closeable;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<ICMeetingAttendeesDto> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<ICMeetingAttendeesDto> attendees) {
        this.attendees = attendees;
    }

    public List<EmployeeDto> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<EmployeeDto> invitees) {
        this.invitees = invitees;
    }

    //    public Long getProtocolFileId() {
//        return protocolFileId;
//    }
//
//    public void setProtocolFileId(Long protocolFileId) {
//        this.protocolFileId = protocolFileId;
//    }
//
//    public String getProtocolFileName() {
//        return protocolFileName;
//    }
//
//    public void setProtocolFileName(String protocolFileName) {
//        this.protocolFileName = protocolFileName;
//    }


    public List<ICMeetingTopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<ICMeetingTopicDto> topics) {
        this.topics = topics;
    }

    public FilesDto getAgenda() {
        return agenda;
    }

    public void setAgenda(FilesDto agenda) {
        this.agenda = agenda;
    }

    public FilesDto getProtocol() {
        return protocol;
    }

    public void setProtocol(FilesDto protocol) {
        this.protocol = protocol;
    }

    public FilesDto getBulletin() {
        return bulletin;
    }

    public void setBulletin(FilesDto bulletin) {
        this.bulletin = bulletin;
    }

    @Override
    public int compareTo(Object o) {
        ICMeetingDto other = ((ICMeetingDto) o);
        if(this.number != null && other.number != null){
            return Integer.parseInt(other.number) - Integer.parseInt(this.number);
        }else{
            return 0;
        }
    }

    public boolean isLockedByDeadline() {
        return isICMeetingLockedByDeadline(this.date);
    }

    public boolean isUpdateLockedByDeadline(){
        return isICMeetingUpdateLockedByDeadline(this.date,this.time);
    }

    public static boolean isICMeetingLockedByDeadline(Date icDate){
        if(icDate != null){
            Date deadlineDate = DateUtils.moveDateByDays(icDate, -CorpMeetingService.IC_MEETING_DEADLINE_DAYS, true);
            if(deadlineDate != null) {
                Date deadLine = DateUtils.getDateWithTime(deadlineDate, CorpMeetingService.IC_MEETING_DEADLINE_HOURS);
                if(deadLine != null){
                    if (deadLine.before(new Date())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isICMeetingUpdateLockedByDeadline(Date icDate, String icTime){
        if(icDate != null){
            String time = icTime != null ? icTime : "9:00";
            Date icDateWithTime = DateUtils.getDateWithTime(icDate, time);
            if(icDateWithTime != null){
                if(DateUtils.moveDateByDays(icDateWithTime, 1, true).before(new Date())){
                    return true;
                }
            }

        }
        return false;
    }

    public String getStatus(){
        if(this.deleted != null && this.deleted.booleanValue()){
            return DELETED;
        }else if(this.closed != null && this.closed.booleanValue()){
            return CLOSED;
        }else{
            // Check if sent to IC
            if(isLockedByDeadline() && (this.unlockedForFinalize == null || !this.unlockedForFinalize.booleanValue())){
                return LOCKED_FOR_IC;
            }
            // Check if to be finalized after IC
            if(isLockedByDeadline() && this.unlockedForFinalize != null && this.unlockedForFinalize.booleanValue()){
                boolean notFinalized = false;
                if(this.topics != null && !this.topics.isEmpty()){
                    for(ICMeetingTopicDto topic: this.topics){
                        if(topic.getStatus() == null || !topic.getStatus().equalsIgnoreCase(FINALIZED)){
                            notFinalized = true;
                            break;
                        }
                    }
                }
                if(notFinalized) {
                    return TO_BE_FINALIZED;
                }else{
                    return FINALIZED;
                }
            }
            return OPEN;
        }
    }

    public Boolean getUnlockedForFinalize() {
        return unlockedForFinalize;
    }

    public void setUnlockedForFinalize(Boolean unlockedForFinalize) {
        this.unlockedForFinalize = unlockedForFinalize;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }
}

