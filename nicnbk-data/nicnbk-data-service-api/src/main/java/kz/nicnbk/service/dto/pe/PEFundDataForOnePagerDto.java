package kz.nicnbk.service.dto.pe;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundDataForOnePagerDto {

    private List<PEOnePagerDescriptionsDto> onePagerDescriptions;
    private List<PEFundManagementTeamDto> managementTeam;

    public List<PEOnePagerDescriptionsDto> getOnePagerDescriptions() {
        return onePagerDescriptions;
    }

    public void setOnePagerDescriptions(List<PEOnePagerDescriptionsDto> onePagerDescriptions) {
        this.onePagerDescriptions = onePagerDescriptions;
    }

    public List<PEFundManagementTeamDto> getManagementTeam() {
        return managementTeam;
    }

    public void setManagementTeam(List<PEFundManagementTeamDto> managementTeam) {
        this.managementTeam = managementTeam;
    }
}
