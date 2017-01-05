package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.HedgeFundDto2;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;
import kz.nicnbk.service.dto.hf.HedgeFundSubstrategyDto;

import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
public interface HedgeFundService extends BaseService {

    Long save(HedgeFundDto2 hedgeFundDto);

    HedgeFundDto2 get(Long id);

    List<HedgeFundDto2> loadManagerFunds(Long managerId);

    Set<HedgeFundDto2> findByName(HedgeFundSearchParams searchParams);
}
