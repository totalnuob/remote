package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import kz.nicnbk.service.dto.common.EmployeeApproveDto;
import kz.nicnbk.service.dto.employee.DepartmentDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.files.NamedFilesDto;

import java.util.List;
import java.util.Set;

/**
 * Created by magzumov.
 */
public class ICMeetingTopicUpdateDto extends CreateUpdateBaseEntityDto<ICMeetingTopic> {

    private String nameUpd;
    private String decisionUpd;

    private List<NamedFilesDto> uploadMaterialsUpd;

    private Boolean publishedUpd;

    public String getNameUpd() {
        return nameUpd;
    }

    public void setNameUpd(String nameUpd) {
        this.nameUpd = nameUpd;
    }

    public String getDecisionUpd() {
        return decisionUpd;
    }

    public void setDecisionUpd(String decisionUpd) {
        this.decisionUpd = decisionUpd;
    }

    public List<NamedFilesDto> getUploadMaterialsUpd() {
        return uploadMaterialsUpd;
    }

    public void setUploadMaterialsUpd(List<NamedFilesDto> uploadMaterialsUpd) {
        this.uploadMaterialsUpd = uploadMaterialsUpd;
    }

    public Boolean getPublishedUpd() {
        return publishedUpd;
    }

    public void setPublishedUpd(Boolean publishedUpd) {
        this.publishedUpd = publishedUpd;
    }
}

