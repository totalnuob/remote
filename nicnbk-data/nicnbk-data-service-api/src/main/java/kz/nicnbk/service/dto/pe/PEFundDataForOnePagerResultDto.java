package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundDataForOnePagerResultDto extends StatusResultDto {

    private List<PEOnePagerDescriptionsDto> descriptionsDtoList;
    private List<PEFundManagementTeamDto> managementTeamDtoList;

    public PEFundDataForOnePagerResultDto (List<PEOnePagerDescriptionsDto> descriptionsDtoList, List<PEFundManagementTeamDto> managementTeamDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.descriptionsDtoList = descriptionsDtoList;
        this.managementTeamDtoList = managementTeamDtoList;
    }

    public List<PEOnePagerDescriptionsDto> getDescriptionsDtoList() {
        return descriptionsDtoList;
    }

    public void setDescriptionsDtoList(List<PEOnePagerDescriptionsDto> descriptionsDtoList) {
        this.descriptionsDtoList = descriptionsDtoList;
    }

    public List<PEFundManagementTeamDto> getManagementTeamDtoList() {
        return managementTeamDtoList;
    }

    public void setManagementTeamDtoList(List<PEFundManagementTeamDto> managementTeamDtoList) {
        this.managementTeamDtoList = managementTeamDtoList;
    }
}
