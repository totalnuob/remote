package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by zhambyl on 04-Aug-16.
 */
@Entity(name = "ic_meeting_files")
public class ICMeetingFiles extends BaseEntity {

    private ICMeeting icMeeting;
    private Files file;

    public ICMeetingFiles(){}

    public ICMeetingFiles(Long meetingId, Long fileId){
        ICMeeting meeting = new ICMeeting();
        meeting.setId(meetingId);
        Files file = new Files();
        file.setId(fileId);
        this.icMeeting = meeting;
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_id", nullable = false)
    public ICMeeting getIcMeeting() {
        return icMeeting;
    }

    public void setIcMeeting(ICMeeting icMeeting) {
        this.icMeeting = icMeeting;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="file_id", nullable = false)
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }
}
