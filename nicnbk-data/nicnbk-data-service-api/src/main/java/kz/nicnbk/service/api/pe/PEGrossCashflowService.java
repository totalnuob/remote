package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public interface PEGrossCashflowService extends BaseService {

    Long save(PEGrossCashflowDto cashflowDto, Long fundId);

    String saveList(List<PEGrossCashflowDto> cashflowDtoList, Long fundId);

    List<PEGrossCashflowDto> findByFundId(Long fundId);

    boolean deleteByFundId(Long fundId);
}