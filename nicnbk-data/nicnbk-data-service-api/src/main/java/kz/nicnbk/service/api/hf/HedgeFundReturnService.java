package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.ReturnDto;

import java.util.List;

/**
 * Created by magzumov on 15.12.2016.
 */
public interface HedgeFundReturnService extends BaseService {

    Long save(ReturnDto dto);

    boolean deleteByFundId(Long fundId);

    ReturnDto get(Long id);

    List<ReturnDto> findByFundId(Long id);
}
