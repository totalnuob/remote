package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 02/03/2018.
 */
public class PEOnePagerDescriptionsResultDto extends StatusResultDto {

    private List<PEOnePagerDescriptionsDto> descriptionsDtoList;

    public PEOnePagerDescriptionsResultDto (List<PEOnePagerDescriptionsDto> descriptionsDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.descriptionsDtoList = descriptionsDtoList;
    }

    public List<PEOnePagerDescriptionsDto> getDescriptionsDtoList() {
        return descriptionsDtoList;
    }

    public void setDescriptionsDtoList(List<PEOnePagerDescriptionsDto> descriptionsDtoList) {
        this.descriptionsDtoList = descriptionsDtoList;
    }
}
