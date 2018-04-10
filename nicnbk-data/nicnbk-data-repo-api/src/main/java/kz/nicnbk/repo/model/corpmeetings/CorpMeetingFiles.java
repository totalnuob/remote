package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.tripmemo.TripMemo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity(name = "corp_mmeting_files")
public class CorpMeetingFiles extends BaseEntity {

    private CorpMeeting corpMeeting;
    private Files file;

    public CorpMeetingFiles(){}

    public CorpMeetingFiles(Long meetingId, Long fileId){
        CorpMeeting meeting = new CorpMeeting();
        meeting.setId(meetingId);
        Files file = new Files();
        file.setId(fileId);
        this.corpMeeting = meeting;
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="corp_meeting_id", nullable = false)
    public CorpMeeting getCorpMeeting() {
        return corpMeeting;
    }

    public void setCorpMeeting(CorpMeeting corpMeeting) {
        this.corpMeeting = corpMeeting;
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
