package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PEFundManagementTeamDto;
import kz.nicnbk.service.dto.pe.PEFundManagementTeamResultDto;

import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public interface PEFundManagementTeamService {

    Long save(PEFundManagementTeamDto managementTeamDto, Long fundId);

    PEFundManagementTeamResultDto saveList(List<PEFundManagementTeamDto> managementTeamDtoList, Long fundId);

    List<PEFundManagementTeamDto> findByFundId(Long fundId);
}
