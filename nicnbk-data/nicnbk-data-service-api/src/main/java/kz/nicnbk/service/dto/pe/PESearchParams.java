package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseParams;

/**
 * Created by zhambyl on 22-Nov-16.
 */
public class PESearchParams implements BaseParams {

    private String name;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
