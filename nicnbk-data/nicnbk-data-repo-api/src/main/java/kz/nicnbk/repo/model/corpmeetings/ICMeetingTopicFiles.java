package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.tripmemo.TripMemo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by magzumov on 04-Aug-16.
 */

@Entity(name = "ic_mmeting_topic_files")
public class ICMeetingTopicFiles extends BaseEntity {

    private ICMeetingTopic icMeetingTopic;
    private Files file;

    public ICMeetingTopicFiles(){}

    public ICMeetingTopicFiles(Long icMeetingTopicId, Long fileId){
        ICMeetingTopic icMeetingTopic = new ICMeetingTopic();
        icMeetingTopic.setId(icMeetingTopicId);
        Files file = new Files();
        file.setId(fileId);
        this.icMeetingTopic = icMeetingTopic;
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_topic_id", nullable = false)
    public ICMeetingTopic getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
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
