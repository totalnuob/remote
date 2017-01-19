package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PENetCashflowDto;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
public interface PENetCashflowService extends BaseService {
    Long save(PENetCashflowDto dto);

    boolean deleteByFundId(Long fundId);

    PENetCashflowDto get(Long id);

    List<PENetCashflowDto> findByFundId(Long id);
}