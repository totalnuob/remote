package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundDataForOnePagerResultDto extends StatusResultDto {

    private List<PEOnePagerDescriptionsDto> onePagerDescriptionsDto;
    private List<PEFundManagementTeamDto> managementTeam;

    public PEFundDataForOnePagerResultDto (List<PEOnePagerDescriptionsDto> onePagerDescriptionsDto, List<PEFundManagementTeamDto> managementTeam, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.onePagerDescriptionsDto = onePagerDescriptionsDto;
        this.managementTeam = managementTeam;
    }

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
