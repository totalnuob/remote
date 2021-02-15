package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import kz.nicnbk.service.dto.common.EmployeeApproveDto;
import kz.nicnbk.service.dto.employee.DepartmentDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.files.NamedFilesDto;

import javax.persistence.Column;
import java.util.List;
import java.util.Set;

/**
 * Created by magzumov.
 */
public class ICMeetingTopicDto extends CreateUpdateBaseEntityDto<ICMeetingTopic> implements Comparable {

    public static final String DRAFT = "DRAFT";
    public static final String READY = "READY";
    public static final String UNDER_REVIEW = "UNDER REVIEW";
    public static final String  APPROVED = "APPROVED";
    public static final String LOCKED_FOR_IC = "LOCKED FOR IC";
    public static final String TO_BE_FINALIZED = "TO BE FINALIZED";
    public static final String FINALIZED = "FINALIZED";
    public static final String CLOSED = "CLOSED";

    public static final String DELETED = "DELETED";

    private ICMeetingDto icMeeting;
    private String name;
    //private String nameUpd;
    private String description;
    private String decision;
   // private String decisionUpd;
    private Set<EmployeeApproveDto> approveList;

    private Boolean published;
    private boolean toPublish;

    private Boolean deleted;

    private DepartmentDto department;

    private List<NamedFilesDto> materials;
    private Integer materialsCount;
    private List<NamedFilesDto> materialsUpd;
    private Set<NamedFilesDto> uploadMaterials;
    private FilesDto explanatoryNote;
    private FilesDto explanatoryNoteUpd;
    private EmployeeDto speaker;
    private EmployeeDto executor;

    private Boolean publishedUpd;

    private List<String> tags;

    private List<ICMeetingTopicsVoteDto> votes;

    private Integer icOrder;

    @Deprecated
    private String type;

    public String getStatus(){
        if(this.deleted != null && this.deleted.booleanValue()){
            return DELETED;
        }else if(this.icMeeting != null && this.icMeeting.getClosed() != null && this.icMeeting.getClosed().booleanValue()){
            // ic meeting closed
            return CLOSED;
        }else if(this.published == null || !this.published.booleanValue()){
            return DRAFT;
        } else{
            if(this.icMeeting != null) {
                if(this.icMeeting.isLockedByDeadline()) {
                    // Check if sent to IC
                    if (this.icMeeting.getUnlockedForFinalize() == null || !this.icMeeting.getUnlockedForFinalize().booleanValue()) {
                        return LOCKED_FOR_IC;
                    }else {
                        if (this.publishedUpd != null && this.publishedUpd.booleanValue()) {
                            return FINALIZED;
                        } else {
                            return TO_BE_FINALIZED;
                        }
                    }
                }else {
                    if (this.approveList == null || this.approveList.isEmpty()) {
                        return READY;
                    } else {
                        for (EmployeeApproveDto approveDto : this.approveList) {
                            if (!approveDto.isApproved()) {
                                return UNDER_REVIEW;
                            }
                        }
                        // Approved
                        return APPROVED;
                    }
                }
            }else{
                // IMPOSSIBLE
                return DRAFT;
            }
        }
    }

    public ICMeetingDto getIcMeeting() {
        return icMeeting;
    }

    public void setIcMeeting(ICMeetingDto icMeeting) {
        this.icMeeting = icMeeting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<NamedFilesDto> getMaterials() {
        return materials;
    }

    public void setMaterials(List<NamedFilesDto> materials) {
        this.materials = materials;
    }

    public FilesDto getExplanatoryNote() {
        return explanatoryNote;
    }

    public void setExplanatoryNote(FilesDto explanatoryNote) {
        this.explanatoryNote = explanatoryNote;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<EmployeeApproveDto> getApproveList() {
        return approveList;
    }

    public void setApproveList(Set<EmployeeApproveDto> approveList) {
        this.approveList = approveList;
    }

    public Set<NamedFilesDto> getUploadMaterials() {
        return uploadMaterials;
    }

    public void setUploadMaterials(Set<NamedFilesDto> uploadMaterials) {
        this.uploadMaterials = uploadMaterials;
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDto department) {
        this.department = department;
    }

    public EmployeeDto getSpeaker() {
        return speaker;
    }

    public void setSpeaker(EmployeeDto speaker) {
        this.speaker = speaker;
    }

    public EmployeeDto getExecutor() {
        return executor;
    }

    public void setExecutor(EmployeeDto executor) {
        this.executor = executor;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

//    public Boolean getClosed() {
//        return closed;
//    }
//
//    public void setClosed(Boolean closed) {
//        this.closed = closed;
//    }

    public boolean isToPublish() {
        return toPublish;
    }

    public void setToPublish(boolean toPublish) {
        this.toPublish = toPublish;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

//    public String getNameUpd() {
//        return nameUpd;
//    }
//
//    public void setNameUpd(String nameUpd) {
//        this.nameUpd = nameUpd;
//    }
//
//    public String getDecisionUpd() {
//        return decisionUpd;
//    }
//
//    public void setDecisionUpd(String decisionUpd) {
//        this.decisionUpd = decisionUpd;
//    }

    public FilesDto getExplanatoryNoteUpd() {
        return explanatoryNoteUpd;
    }

    public void setExplanatoryNoteUpd(FilesDto explanatoryNoteUpd) {
        this.explanatoryNoteUpd = explanatoryNoteUpd;
    }

    public List<NamedFilesDto> getMaterialsUpd() {
        return materialsUpd;
    }

    public void setMaterialsUpd(List<NamedFilesDto> materialsUpd) {
        this.materialsUpd = materialsUpd;
    }

    public Boolean getPublishedUpd() {
        return publishedUpd;
    }

    public void setPublishedUpd(Boolean publishedUpd) {
        this.publishedUpd = publishedUpd;
    }

//    public String getPublishedNameUpd(){
//        if(this.nameUpd != null && StringUtils.isNotEmpty(this.nameUpd) && this.publishedUpd != null && this.publishedUpd.booleanValue()){
//            return nameUpd;
//        }
//        return this.name;
//    }
//
//    public String getPublishedDecisionUpd(){
//        if(this.decisionUpd != null && StringUtils.isNotEmpty(this.decisionUpd) && this.publishedUpd != null && this.publishedUpd.booleanValue()){
//            return decisionUpd;
//        }
//        return this.decision;
//    }

    public Integer getMaterialsCount() {
        return materialsCount;
    }

    public void setMaterialsCount(Integer materialsCount) {
        this.materialsCount = materialsCount;
    }

    public List<ICMeetingTopicsVoteDto> getVotes() {
        return votes;
    }

    public void setVotes(List<ICMeetingTopicsVoteDto> votes) {
        this.votes = votes;
    }

    public Integer getIcOrder() {
        return icOrder;
    }

    public void setIcOrder(Integer icOrder) {
        this.icOrder = icOrder;
    }

    @Override
    public int compareTo(Object o) {
        if(this.icOrder != null){
            if(((ICMeetingTopicDto)o).icOrder != null){
                return this.icOrder - ((ICMeetingTopicDto)o).icOrder;
            }else{
                return 1;
            }
        }else if(this.getId() != null){
            if(((ICMeetingTopicDto)o).getId() != null){
                return this.getId().intValue() - ((ICMeetingTopicDto)o).getId().intValue();
            }else{
                return 1;
            }
        }else{
            return 0;
        }
    }
}

