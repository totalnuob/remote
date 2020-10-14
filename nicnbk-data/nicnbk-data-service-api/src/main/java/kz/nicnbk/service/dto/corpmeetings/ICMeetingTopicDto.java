package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import kz.nicnbk.service.dto.common.EmployeeApproveDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.files.NamedFilesDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * Created by magzumov.
 */
public class ICMeetingTopicDto extends CreateUpdateBaseEntityDto<ICMeetingTopic> {

    private ICMeetingDto icMeeting;
    private String name;
    private String description;
    private String decision;
    private Set<EmployeeApproveDto> decisionApproveList;

    private Set<NamedFilesDto> materials;
    private Set<FilesDto> explanatoryNote;

    private List<String> tags;

    @Deprecated
    private String type;

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

    public Set<NamedFilesDto> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<NamedFilesDto> materials) {
        this.materials = materials;
    }

    public Set<FilesDto> getExplanatoryNote() {
        return explanatoryNote;
    }

    public void setExplanatoryNote(Set<FilesDto> explanatoryNote) {
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

    public Set<EmployeeApproveDto> getDecisionApproveList() {
        return decisionApproveList;
    }

    public void setDecisionApproveList(Set<EmployeeApproveDto> decisionApproveList) {
        this.decisionApproveList = decisionApproveList;
    }
}

