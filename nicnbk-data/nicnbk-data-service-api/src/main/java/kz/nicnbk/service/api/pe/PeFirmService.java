package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PeFirmDto;
import kz.nicnbk.service.dto.pe.PeSearchParams;

import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PeFirmService extends BaseService {

    Long save(PeFirmDto firmDto);

    PeFirmDto get(Long id);

    Set<PeFirmDto> findByName(PeSearchParams searchParams);
}
