package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PeNetCashflowDto;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
public interface PeNetCashflowService extends BaseService {
    Long save(PeNetCashflowDto dto);

    boolean deleteByFundId(Long fundId);

    PeNetCashflowDto get(Long id);

    List<PeNetCashflowDto> findByFundId(Long id);
}
