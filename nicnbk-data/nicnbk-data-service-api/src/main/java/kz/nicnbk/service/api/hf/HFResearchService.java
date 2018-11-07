package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.dto.hf.HFResearchDto;

/**
 * Created by zhambyl on 06/11/2018.
 */
public interface HFResearchService {

    Long save(HFResearchDto researchDto);

    HFResearchDto get(Long id);
}
