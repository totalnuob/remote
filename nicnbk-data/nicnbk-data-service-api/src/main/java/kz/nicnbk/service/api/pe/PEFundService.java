package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PEFundDto;

import java.util.List;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PEFundService extends BaseService {

    Long save(PEFundDto fundDto);

    PEFundDto get(Long id);

    List<PEFundDto> loadFirmFunds(Long firmId, String name);
}