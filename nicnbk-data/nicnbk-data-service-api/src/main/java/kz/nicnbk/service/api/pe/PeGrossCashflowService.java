package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PeGrossCashflowDto;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public interface PeGrossCashflowService extends BaseService {
    Long save(PeGrossCashflowDto dto);

    boolean deleteByFundId(Long fundId);

    PeGrossCashflowDto get(Long id);

    List<PeGrossCashflowDto> findByFundId(Long id);
}
