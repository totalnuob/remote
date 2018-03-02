package kz.nicnbk.service.impl.pe;

import kz.nicnbk.service.api.pe.PEOnePagerDescriptionsService;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsDto;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Pak on 02/03/2018.
 */
@Service
public class PEOnePagerDescriptionsServiceImpl implements PEOnePagerDescriptionsService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Override
    public Long save(PEOnePagerDescriptionsDto descriptionsDto, Long fundId) {
        return null;
    }

    @Override
    public PEOnePagerDescriptionsResultDto saveList(List<PEOnePagerDescriptionsDto> descriptionsDtoList, Long fundId){
        return null;
    }
}
