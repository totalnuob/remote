package kz.nicnbk.service.dto.pe;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundDataForOnePagerDto {

    private List<PEOnePagerDescriptionsDto> descriptionsDtoList;
    private List<PEFundManagementTeamDto> managementTeamDtoList;

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
