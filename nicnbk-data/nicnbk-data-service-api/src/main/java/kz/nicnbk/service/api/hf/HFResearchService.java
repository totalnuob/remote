package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.HFResearchDto;

/**
 * Created by zhambyl on 06/11/2018.
 */
public interface HFResearchService extends BaseService {

    Long save(HFResearchDto researchDto, String updater);

    HFResearchDto get(Long id);
}
