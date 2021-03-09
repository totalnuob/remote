package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.service.dto.employee.DepartmentDto;

import java.util.List;


public class ICMeetingTopicAssignmentDto extends BaseEntityDto {

    private ICMeetingTopicDto icMeetingTopic;
    private String name;
    private String dueDate;
    private String status;
    private boolean closed;
    private List<DepartmentDto> departments;

    public ICMeetingTopicDto getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopicDto icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public List<DepartmentDto> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentDto> departments) {
        this.departments = departments;
    }

    public String getDepartmentsText(){
        if(this.departments != null){
            String departmentsText = "";
            for(DepartmentDto departmentDto: this.departments){
                departmentsText += (departmentsText.length() > 0 ? ",": "") + departmentDto.getShortNameRu();
            }
            return departmentsText;
        }
        return "";
    }
}

