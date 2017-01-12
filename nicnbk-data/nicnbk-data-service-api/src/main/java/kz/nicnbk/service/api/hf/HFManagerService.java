package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.HFFirmDto;
import kz.nicnbk.service.dto.hf.HFManagerDto;

/**
 * Created by timur on 19.10.2016.
 */
public interface HFManagerService extends BaseService {

    Long save(HFFirmDto firmDto);

    HFFirmDto get(Long id);
}
