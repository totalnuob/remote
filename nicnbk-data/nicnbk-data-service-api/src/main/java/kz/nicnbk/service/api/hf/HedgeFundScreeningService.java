package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;

import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */
public interface HedgeFundScreeningService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    Long save(HedgeFundScreeningDto screeningDto, String username);

    HedgeFundScreeningDto get(Long id);

    List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedReturns(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto);

    List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedAUMS(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto);

    HedgeFundScreeningPagedSearchResult search(HedgeFundScreeningSearchParams searchParams);

    FileUploadResultDto saveAttachmentDataFile(Long screeningId, FilesDto filesDto, String username);

    boolean removeFileAndData(Long fileId, Long screeningId, String username);

    List<HedgeFundScreeningFilteredResultDto> getFilteredResultsByScreeningId(Long screeningId);

    Long saveFilteredResult(HedgeFundScreeningFilteredResultDto screeningDto, String username);

    HedgeFundScreeningFilteredResultDto getFilteredResult(Long id);

    HedgeFundScreeningFilteredResultStatisticsDto getFilteredResultStatistics(HedgeFundScreeningFilteredResultDto params);

    List<HedgeFundScreeningParsedDataDto> getFilteredResultQualifiedFundList( HedgeFundScreeningFilteredResultDto params);
}
