package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PESearchParams;

import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PEFirmService extends BaseService {

    Long save(PEFirmDto firmDto);

    PEFirmDto get(Long id);

    Set<PEFirmDto> findByName(PESearchParams searchParams);
}
