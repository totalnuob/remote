package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;

import java.util.Date;
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
    List<HedgeFundScreeningDto> getAll();

    List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedReturns(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto);

    List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedAUMS(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto);

    List<HedgeFundScreeningParsedDataDateValueCombinedDto> searchParsedUcitsAUMS(HedgeFundScreeningParsedDataDateValueSearchParamsDto searchParamsDto);

    HedgeFundScreeningPagedSearchResult search(HedgeFundScreeningSearchParams searchParams);

    FileUploadResultDto saveAndParseAttachmentDataFile(Long screeningId, FilesDto filesDto, String username);

    FileUploadResultDto saveAttachmentUcitsFile(Long screeningId, FilesDto filesDto, String username);

    boolean removeFileAndData(Long fileId, Long screeningId, String username);

    boolean removeUcitsFileAndData(Long fileId, Long screeningId, String username);

    List<HedgeFundScreeningFilteredResultDto> getFilteredResultsByScreeningId(Long screeningId);

    Long saveFilteredResult(HedgeFundScreeningFilteredResultDto screeningDto, String username);

    HedgeFundScreeningFilteredResultDto getFilteredResultWithFundsInfo(Long id);

    HedgeFundScreeningFilteredResultDto getFilteredResultWithoutFundsInfo(Long id);

    HedgeFundScreeningFilteredResultStatisticsDto getFilteredResultStatistics(HedgeFundScreeningFilteredResultDto params);

    ListResponseDto getFilteredResultQualifiedFundList(HedgeFundScreeningFilteredResultDto params, boolean withScoring);

    List<HedgeFundScreeningParsedDataDto> getFilteredResultUnqualifiedFundList( HedgeFundScreeningFilteredResultDto params);

    List<HedgeFundScreeningParsedDataDto> getFilteredResultUndecidedFundList( HedgeFundScreeningFilteredResultDto params);

    boolean updateManagerAUM(List<HedgeFundScreeningParsedDataDto> fundList, String username);

    boolean updateFundInfo(HedgeFundScreeningParsedDataDto fund, String username);

    boolean deleteAddedFund(String fundName, Long screeningId, String username);

    boolean excludeParsedFund(Long filteredResultId, Long fundId, String excludeComment, boolean excludeFromStrategyAUM, String username);

    boolean includeParsedFund(Long filteredResultId, Long fundId, String username);

    double[] getAddedFundReturns(Long filteredResultId, String fundName, int trackRecord, Date dateFrom, Date dateTo);

    double[] getParsedFundReturns(Long screeningId, Long fundId, int trackRecord, Date dateFrom, Date dateTo);

    FilesDto getQualifiedFundListAsStream(Long filteredResultId, int lookbackAUM, int lookbackReturn);

    FilesDto getUnqualifiedFundListAsStream(Long filteredResultId, int lookbackAUM, int lookbackReturn);

    ResponseDto saveResults(HedgeFundScreeningSaveParamsDto saveParamsDto, String username);

    ResponseDto deleteSavedResultsById(Long id, String username);

    ResponseDto archiveSavedResultsById(Long id, String username);

    ResponseDto deleteFilteredResultById(Long id, String username);

    ResponseDto deleteScreeningById(Long id, String username);

}

