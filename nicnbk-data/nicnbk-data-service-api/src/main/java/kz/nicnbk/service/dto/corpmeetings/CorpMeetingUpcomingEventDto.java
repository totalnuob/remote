package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

public class CorpMeetingUpcomingEventDto implements BaseDto {

    private Date date;
    private String name;
    private String description;
    private String alertType;

    public CorpMeetingUpcomingEventDto(){}

    public CorpMeetingUpcomingEventDto(String name, String description){
        this.name = name;
        this.description = description;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
}
