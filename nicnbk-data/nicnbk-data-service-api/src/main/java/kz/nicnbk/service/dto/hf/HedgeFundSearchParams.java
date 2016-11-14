package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseParams;

/**
 * Created by timur on 25.10.2016.
 */
public class HedgeFundSearchParams implements BaseParams {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
