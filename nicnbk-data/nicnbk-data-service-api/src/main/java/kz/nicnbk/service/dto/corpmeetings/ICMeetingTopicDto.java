package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.corpmeetings.ICMeeting;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by magzumov.
 */
public class ICMeetingTopicDto extends CreateUpdateBaseEntityDto<ICMeetingTopic> {

    private ICMeetingDto icMeeting;
    private String shortName;
    private String longDescription;
    private String decision;
    private String type;

    private Set<FilesDto> materials;

    private List<String> tags;

    public ICMeetingDto getIcMeeting() {
        return icMeeting;
    }

    public void setIcMeeting(ICMeetingDto icMeeting) {
        this.icMeeting = icMeeting;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Set<FilesDto> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<FilesDto> materials) {
        this.materials = materials;
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
}

