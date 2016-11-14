package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.HedgeFundDto;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
public interface HedgeFundService extends BaseService {

    Long save(HedgeFundDto hedgeFundDto);

    HedgeFundDto get(Long id);

    Set<HedgeFundDto> loadManagerFunds(Long managerId);

    Set<HedgeFundDto> findByName(HedgeFundSearchParams searchParams);


}
