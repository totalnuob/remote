package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.pe.PEOnePagerDescriptions;

/**
 * Created by Pak on 02/03/2018.
 */
public class PEOnePagerDescriptionsDto extends BaseEntityDto<PEOnePagerDescriptions> {

    private String descriptionBold;
    private String description;
    private int type;

    public String getDescriptionBold() {
        return descriptionBold;
    }

    public void setDescriptionBold(String descriptionBold) {
        this.descriptionBold = descriptionBold;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
