package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.employee.DepartmentDto;

import java.util.List;


public class ICMeetingTopicDecisionDto implements BaseDto, Comparable {

    private int order;
    private String name;
    private String type;
    private List<DepartmentDto> departments;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DepartmentDto> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentDto> departments) {
        this.departments = departments;
    }

    @Override
    public int compareTo(Object o) {
        ICMeetingTopicDecisionDto other = (ICMeetingTopicDecisionDto) o;
        return this.order - other.order;
    }

    public boolean isAssignment(){
        // TODO: refactor to enum
        return this.type != null && this.type.equalsIgnoreCase("ASSIGN");
    }
}

