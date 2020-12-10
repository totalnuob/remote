//package kz.nicnbk.repo.model.corpmeetings;
//
//import kz.nicnbk.repo.model.base.BaseEntity;
//import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
//import kz.nicnbk.repo.model.files.Files;
//import kz.nicnbk.repo.model.tag.Tag;
//
//import javax.persistence.*;
//
///**
// * Created by magzumov on 04-Aug-16.
// */
//
//@Entity(name = "ic_meeting_topic_tags")
//public class ICMeetingTopicTags extends CreateUpdateBaseEntity {
//
//    private ICMeetingTopic icMeetingTopic;
//    private Tag tag;
//
//    public ICMeetingTopicTags(){}
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="ic_meeting_topic_id", nullable = false)
//    public ICMeetingTopic getIcMeetingTopic() {
//        return icMeetingTopic;
//    }
//
//    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
//        this.icMeetingTopic = icMeetingTopic;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name="tag_id", nullable = false)
//    public Tag getTag() {
//        return tag;
//    }
//
//    public void setTag(Tag tag) {
//        this.tag = tag;
//    }
//}
