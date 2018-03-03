package kz.nicnbk.service.dto.pe;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundDataForOnePagerDto {

    private PEOnePagerDescriptionsDto descriptionsDto;
    private PEFundManagementTeamDto managementTeamDto;

    public PEOnePagerDescriptionsDto getDescriptionsDto() {
        return descriptionsDto;
    }

    public void setDescriptionsDto(PEOnePagerDescriptionsDto descriptionsDto) {
        this.descriptionsDto = descriptionsDto;
    }

    public PEFundManagementTeamDto getManagementTeamDto() {
        return managementTeamDto;
    }

    public void setManagementTeamDto(PEFundManagementTeamDto managementTeamDto) {
        this.managementTeamDto = managementTeamDto;
    }
}
