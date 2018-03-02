package kz.nicnbk.service.impl.pe;

import kz.nicnbk.repo.api.pe.PEOnePagerDescriptionsRepository;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEOnePagerDescriptions;
import kz.nicnbk.service.api.pe.PEOnePagerDescriptionsService;
import kz.nicnbk.service.converter.pe.PEOnePagerDescriptionsConverter;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsDto;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
    }
}
