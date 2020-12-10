package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.tag.Tag;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ic_meeting_topic")
public class ICMeetingTopic extends CreateUpdateBaseEntity{

    private ICMeeting icMeeting;
    private String name;
    private String nameUpd;
    private String description;
    private String decision;
    private String decisionUpd;
//    @Deprecated
//    private ICMeetingTopicType type;

    private Boolean published;
    private Boolean publishedUpd;
    private Boolean closed;

    private Set<ICMeetingTopicApproval> approveList;

    private Department department;

    private Boolean deleted;
    private List<Tag> tags;
    private Files explanatoryNote;
    private Files explanatoryNoteUpd;
    private Employee speaker;
    private Employee executor;

    private Integer icOrder;

    public ICMeetingTopic(){}

    public ICMeetingTopic(Long id){
        this.setId(id);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ic_meeting_id")
    public ICMeeting getIcMeeting() {
        return icMeeting;
    }

    public void setIcMeeting(ICMeeting icMeeting) {
        this.icMeeting = icMeeting;
    }

    @Column(name="name", columnDefinition="TEXT")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="long_description", columnDefinition="TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


    //TODO: separate entity, repository + update services

    @ManyToMany//(cascade = CascadeType.ALL)
    @JoinTable(name = "ic_meeting_topic_tag",
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exp_note_id")
    public Files getExplanatoryNote() {
        return explanatoryNote;
    }

    public void setExplanatoryNote(Files explanatoryNote) {
        this.explanatoryNote = explanatoryNote;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @OneToMany(mappedBy = "icMeetingTopic", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ICMeetingTopicApproval> getApproveList() {
        return approveList;
    }

    public void setApproveList(Set<ICMeetingTopicApproval> approveList) {
        this.approveList = approveList;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "speaker_id")
    public Employee getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Employee speaker) {
        this.speaker = speaker;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "executor_id")
    public Employee getExecutor() {
        return executor;
    }

    public void setExecutor(Employee executor) {
        this.executor = executor;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    @Column(name="name_upd", columnDefinition="TEXT")
    public String getNameUpd() {
        return nameUpd;
    }

    public void setNameUpd(String nameUpd) {
        this.nameUpd = nameUpd;
    }

    @Column(name="decision_upd", columnDefinition="TEXT")
    public String getDecisionUpd() {
        return decisionUpd;
    }

    public void setDecisionUpd(String decisionUpd) {
        this.decisionUpd = decisionUpd;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exp_note_upd_id")
    public Files getExplanatoryNoteUpd() {
        return explanatoryNoteUpd;
    }

    public void setExplanatoryNoteUpd(Files explanatoryNoteUpd) {
        this.explanatoryNoteUpd = explanatoryNoteUpd;
    }

    @Column(name="published_upd")
    public Boolean getPublishedUpd() {
        return publishedUpd;
    }

    public void setPublishedUpd(Boolean publishedUpd) {
        this.publishedUpd = publishedUpd;
    }

    @Column(name="ic_order")
    public Integer getIcOrder() {
        return icOrder;
    }

    public void setIcOrder(Integer icOrder) {
        this.icOrder = icOrder;
    }
}