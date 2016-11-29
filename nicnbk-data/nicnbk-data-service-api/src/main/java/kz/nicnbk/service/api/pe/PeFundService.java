package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PeFundDto;

import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PeFundService extends BaseService {

    Long save(PeFundDto fundDto);

    PeFundDto get(Long id);

    Set<PeFundDto> loadFirmFunds(Long firmId);
}
