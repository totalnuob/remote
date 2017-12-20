package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PENetCashflowDto;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
public interface PENetCashflowService extends BaseService {
    Long save(PENetCashflowDto dto);

    List<PENetCashflowDto> findByFundId(Long fundId);

    boolean deleteByFundId(Long fundId);
}