package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.*;

/**
 * Created by magzumov on 04-Aug-16.
 */

@Entity(name = "ic_mmeting_topic_files")
public class ICMeetingTopicFiles extends BaseEntity {

    private ICMeetingTopic icMeetingTopic;
    private Files file;
    private String customName;
    //private boolean update;
    private Integer topicOrder;

    public ICMeetingTopicFiles(){}

    public ICMeetingTopicFiles(Long icMeetingTopicId, Long fileId, String customName/*, boolean update*/){
        ICMeetingTopic icMeetingTopic = new ICMeetingTopic();
        icMeetingTopic.setId(icMeetingTopicId);
        Files file = new Files();
        file.setId(fileId);
        this.icMeetingTopic = icMeetingTopic;
        this.file = file;
        this.customName = customName;
        //this.update = update;
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

    @Column(name="custom_name", columnDefinition="TEXT")
    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

//    @Column(name="update", nullable = false)
//    public boolean isUpdate() {
//        return update;
//    }
//
//    public void setUpdate(boolean update) {
//        this.update = update;
//    }

    public Integer getTopicOrder() {
        return topicOrder;
    }

    public void setTopicOrder(Integer topicOrder) {
        this.topicOrder = topicOrder;
    }
}
