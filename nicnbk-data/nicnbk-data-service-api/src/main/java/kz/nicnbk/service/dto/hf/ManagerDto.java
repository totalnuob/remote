package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by timur on 27.10.2016.
 */
public class ManagerDto implements BaseDto {
    private String name;
    private String description;
    private String started;

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

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }
}
