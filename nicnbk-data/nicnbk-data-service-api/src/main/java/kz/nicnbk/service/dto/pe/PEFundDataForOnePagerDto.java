package kz.nicnbk.service.dto.pe;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundDataForOnePagerDto {

    private List<PEOnePagerDescriptionsDto> onePagerDescriptionsDto;
    private List<PEFundManagementTeamDto> managementTeam;

    public List<PEOnePagerDescriptionsDto> getOnePagerDescriptionsDto() {
        return onePagerDescriptionsDto;
    }

    public void setOnePagerDescriptionsDto(List<PEOnePagerDescriptionsDto> onePagerDescriptionsDto) {
        this.onePagerDescriptionsDto = onePagerDescriptionsDto;
    }

    public List<PEFundManagementTeamDto> getManagementTeam() {
        return managementTeam;
    }

    public void setManagementTeam(List<PEFundManagementTeamDto> managementTeam) {
        this.managementTeam = managementTeam;
    }
}
