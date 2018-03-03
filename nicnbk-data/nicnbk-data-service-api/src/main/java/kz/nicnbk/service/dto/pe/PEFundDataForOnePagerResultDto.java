package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundDataForOnePagerResultDto extends StatusResultDto {

    private List<PEOnePagerDescriptionsDto> onePagerDescriptions;
    private List<PEFundManagementTeamDto> managementTeam;

    public PEFundDataForOnePagerResultDto (List<PEOnePagerDescriptionsDto> onePagerDescriptions, List<PEFundManagementTeamDto> managementTeam, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.onePagerDescriptions = onePagerDescriptions;
        this.managementTeam = managementTeam;
    }

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
