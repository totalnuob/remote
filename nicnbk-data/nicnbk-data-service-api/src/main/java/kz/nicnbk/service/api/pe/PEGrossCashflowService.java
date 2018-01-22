package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowResultDto;
import kz.nicnbk.service.dto.pe.PEIrrResultDto;
import kz.nicnbk.service.dto.pe.PEPortfolioInfoDto;

import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public interface PEGrossCashflowService {

    Long save(PEGrossCashflowDto cashflowDto, Long fundId);

    PEGrossCashflowResultDto saveList(List<PEGrossCashflowDto> cashflowDtoList, Long fundId);

    PEGrossCashflowResultDto uploadGrossCF(Set<FilesDto> filesDtoSet);

    List<PEGrossCashflowDto> findByFundId(Long fundId);

    List<PEGrossCashflowDto> findByFundIdSortedByDate(Long fundId);

    List<PEGrossCashflowDto> findByFundIdAndCompanyName(Long fundId, String companyName);

    List<PEGrossCashflowDto> findByFundIdAndPortfolioInfo(Long fundId, PEPortfolioInfoDto portfolioInfoDto);

    PEIrrResultDto calculateIRR(PEPortfolioInfoDto portfolioInfoDto, Long fundId);

//    boolean deleteByFundId(Long fundId);
}