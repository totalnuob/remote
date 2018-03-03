package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEFundManagementTeamRepository;
import kz.nicnbk.service.api.pe.PEFundManagementTeamService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.converter.pe.PEFundManagementTeamConverter;
import kz.nicnbk.service.dto.pe.PEFundManagementTeamDto;
import kz.nicnbk.service.dto.pe.PEFundManagementTeamResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
            PEOnePagerDescriptions entity = this.converter.assemble(descriptionsDto);
            entity.setFund(new PEFund(fundId));
            return this.repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's one pager descriptions: " + fundId, ex);
        }
        return null;
    }

    public PEFundManagementTeamResultDto saveList(List<PEFundManagementTeamDto> managementTeamDtoList, Long fundId) {
        return null;
    }

    public List<PEFundManagementTeamDto> findByFundId(Long fundId) {
        return null;
    }
}
