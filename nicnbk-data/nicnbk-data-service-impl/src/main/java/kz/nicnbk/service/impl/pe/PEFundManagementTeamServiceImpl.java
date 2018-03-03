package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEFundManagementTeamRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEFundManagementTeam;
import kz.nicnbk.service.api.pe.PEFundManagementTeamService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.converter.pe.PEFundManagementTeamConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.PEFundManagementTeamDto;
import kz.nicnbk.service.dto.pe.PEFundManagementTeamResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pak on 03/03/2018.
 */
public class PEFundManagementTeamServiceImpl implements PEFundManagementTeamService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEFundManagementTeamConverter converter;

    @Autowired
    private PEFundManagementTeamRepository repository;

    @Autowired
    private PEFundService peFundService;

    public Long save(PEFundManagementTeamDto managementTeamDto, Long fundId) {
        try {
            PEFundManagementTeam entity = this.converter.assemble(managementTeamDto);
            entity.setFund(new PEFund(fundId));
            return this.repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's Management Team: " + fundId, ex);
        }
        return null;
    }

    public PEFundManagementTeamResultDto saveList(List<PEFundManagementTeamDto> managementTeamDtoList, Long fundId) {
        try {
            if (managementTeamDtoList == null || fundId == null) {
                return new PEFundManagementTeamResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send NULL!", "");
            }

            for (PEFundManagementTeamDto managementTeamDto : managementTeamDtoList) {
                if (managementTeamDto.getName() != null) {
                    managementTeamDto.setName(managementTeamDto.getName().trim());
                }
                if (managementTeamDto.getPosition() != null) {
                    managementTeamDto.setPosition(managementTeamDto.getPosition().trim());
                }
                if (managementTeamDto.getExperience() != null) {
                    managementTeamDto.setExperience(managementTeamDto.getExperience().trim());
                }
                if (managementTeamDto.getEducation() != null) {
                    managementTeamDto.setEducation(managementTeamDto.getEducation().trim());
                }
                if (managementTeamDto.getName() == null || managementTeamDto.getName().equals("")) {
                    return new PEFundManagementTeamResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send null or empty name!", "");
                }
            }

            if (this.peFundService.get(fundId) == null) {
                return new PEFundManagementTeamResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            for (PEFundManagementTeam managementTeam : this.repository.getEntitiesByFundId(fundId)) {
                int i = 0;
                for (PEFundManagementTeamDto managementTeamDto : managementTeamDtoList) {
                    if (managementTeam.getId().equals(managementTeamDto.getId())) {
                        i++;
                        break;
                    }
                }
                if (i == 0) {
                    this.repository.delete(managementTeam);
                }
            }

            for (PEFundManagementTeamDto managementTeamDto : managementTeamDtoList) {
                Long id = save(managementTeamDto, fundId);
                if (id == null) {
                    return new PEFundManagementTeamResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's Management Team", "");
                } else {
                    managementTeamDto.setId(id);
                }
            }

            return new PEFundManagementTeamResultDto(managementTeamDtoList, StatusResultType.SUCCESS, "", "Successfully saved PE fund's Management Team", "");
        } catch (Exception ex) {
            logger.error("Error saving PE fund's Management Team: " + fundId, ex);
            return new PEFundManagementTeamResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's Management Team", "");
        }
    }

    public List<PEFundManagementTeamDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's Management Team: " + fundId, ex);
        }
        return null;
    }
}
