package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public interface PEGrossCashflowService {

    Long save(PEGrossCashflowDto cashflowDto, Long fundId);

    PEGrossCashflowResultDto saveList(List<PEGrossCashflowDto> cashflowDtoList, Long fundId);

    PEGrossCashflowResultDto uploadGrossCF(MultipartFile file);

    List<PEGrossCashflowDto> findByFundId(Long fundId);

    List<PEGrossCashflowDto> findByFundIdSortedByDate(Long fundId);

    List<PEGrossCashflowDto> findByFundIdAndCompanyName(Long fundId, String companyName);

    boolean deleteByFundId(Long fundId);
}