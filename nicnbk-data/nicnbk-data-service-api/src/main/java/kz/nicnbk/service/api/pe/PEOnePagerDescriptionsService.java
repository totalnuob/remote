package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsDto;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsResultDto;

import java.util.List;

/**
 * Created by Pak on 02/03/2018.
 */
public interface PEOnePagerDescriptionsService {

    Long save(PEOnePagerDescriptionsDto descriptionsDto, Long fundId);

    PEOnePagerDescriptionsResultDto saveList(List<PEOnePagerDescriptionsDto> descriptionsDtoList, Long fundId);

    List<PEOnePagerDescriptionsDto> findByFundId(Long fundId);

    List<PEOnePagerDescriptionsDto> findByFundIdAndType(Long fundId, int type);
}
