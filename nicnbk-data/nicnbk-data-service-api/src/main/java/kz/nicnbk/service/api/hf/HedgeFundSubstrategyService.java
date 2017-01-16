package kz.nicnbk.service.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundSubstrategy;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.HedgeFundSubstrategyDto;

import java.util.List;

/**
 * Created by magzumov on 14.12.2016.
 */
public interface HedgeFundSubstrategyService extends BaseService {

    Long save(HedgeFundSubstrategyDto dto);

    boolean deleteByFundId(Long fundId);

    HedgeFundSubstrategy get(Long id);

    List<HedgeFundSubstrategyDto> findByFundId(Long id);
}
