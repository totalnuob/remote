package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundManagementTeamResultDto extends StatusResultDto {

    private List<PEFundManagementTeamDto> managementTeamDtoList;

    public PEFundManagementTeamResultDto (List<PEFundManagementTeamDto> managementTeamDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.managementTeamDtoList = managementTeamDtoList;
    }

    public List<PEFundManagementTeamDto> getManagementTeamDtoList() {
        return managementTeamDtoList;
    }

    public void setManagementTeamDtoList(List<PEFundManagementTeamDto> managementTeamDtoList) {
        this.managementTeamDtoList = managementTeamDtoList;
    }
}
