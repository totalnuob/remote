package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowResultDto;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public interface PEGrossCashflowService {

    Long save(PEGrossCashflowDto cashflowDto, Long fundId);

    PEGrossCashflowResultDto saveList(List<PEGrossCashflowDto> cashflowDtoList, Long fundId);

    List<PEGrossCashflowDto> findByFundId(Long fundId);

    boolean deleteByFundId(Long fundId);
}