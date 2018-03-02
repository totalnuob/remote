package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEOnePagerDescriptionsRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEOnePagerDescriptions;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEOnePagerDescriptionsService;
import kz.nicnbk.service.converter.pe.PEOnePagerDescriptionsConverter;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsDto;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pak on 02/03/2018.
 */
@Service
public class PEOnePagerDescriptionsServiceImpl implements PEOnePagerDescriptionsService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEOnePagerDescriptionsConverter converter;

    @Autowired
    private PEOnePagerDescriptionsRepository repository;

    @Autowired
    private PEFundService peFundService;

    @Override
    public Long save(PEOnePagerDescriptionsDto descriptionsDto, Long fundId) {
        try {
            PEOnePagerDescriptions entity = this.converter.assemble(descriptionsDto);
            entity.setFund(new PEFund(fundId));
            return this.repository.save(entity).getId();
        } catch (Exception ex) {
            logger.error("Error saving PE fund's one pager descriptions: " + fundId, ex);
        }
        return null;
    }

    @Override
    public PEOnePagerDescriptionsResultDto saveList(List<PEOnePagerDescriptionsDto> descriptionsDtoList, Long fundId){
        try {
            if (descriptionsDtoList == null || fundId == null) {
                return new PEOnePagerDescriptionsResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send NULL!", "");
            }

            for (PEOnePagerDescriptionsDto descriptionsDto : descriptionsDtoList) {
                if (descriptionsDto.getDescriptionBold() != null) {
                    descriptionsDto.setDescriptionBold(descriptionsDto.getDescriptionBold().trim());
                }
                if (descriptionsDto.getDescription() != null) {
                    descriptionsDto.setDescription(descriptionsDto.getDescription().trim());
                }
                if ((descriptionsDto.getDescriptionBold() == null || descriptionsDto.getDescriptionBold().equals("")) &&
                        (descriptionsDto.getDescription() == null || descriptionsDto.getDescription().equals(""))) {
                    return new PEOnePagerDescriptionsResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Don't send null or empty descriptions!", "");
                }
            }

            if (this.peFundService.get(fundId) == null) {
                return new PEOnePagerDescriptionsResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Fund doesn't exist!", "");
            }

            for (PEOnePagerDescriptions descriptions : this.repository.getEntitiesByFundId(fundId)) {
                int i = 0;
                for (PEOnePagerDescriptionsDto descriptionsDto : descriptionsDtoList) {
                    if (descriptions.getId().equals(descriptionsDto.getId())) {
                        i++;
                        break;
                    }
                }
                if (i == 0) {
                    this.repository.delete(descriptions);
                }
            }

            for (PEOnePagerDescriptionsDto descriptionsDto : descriptionsDtoList) {
                Long id = save(descriptionsDto, fundId);
                if (id == null) {
                    return new PEOnePagerDescriptionsResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's one pager descriptions", "");
                } else {
                    descriptionsDto.setId(id);
                }
            }

            return new PEOnePagerDescriptionsResultDto(descriptionsDtoList, StatusResultType.SUCCESS, "", "Successfully saved PE fund's one pager descriptions", "");
        } catch (Exception ex) {
            logger.error("Error saving PE fund's one pager descriptions: " + fundId, ex);
            return new PEOnePagerDescriptionsResultDto(new ArrayList<>(), StatusResultType.FAIL, "", "Error saving PE fund's one pager descriptions", "");
        }
    }

    @Override
    public List<PEOnePagerDescriptionsDto> findByFundId(Long fundId) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundId(fundId));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's one pager descriptions: " + fundId, ex);
        }
        return null;
    }

    @Override
    public List<PEOnePagerDescriptionsDto> findByFundIdAndType(Long fundId, int type) {
        try {
            return this.converter.disassembleList(this.repository.getEntitiesByFundIdAndType(fundId, type));
        } catch (Exception ex) {
            logger.error("Error loading PE fund's one pager descriptions: " + fundId, ex);
        }
        return null;
    }
}
