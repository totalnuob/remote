package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.hf.*;

import java.util.List;

/**
 * Created by magzumov on 27.05.2019.
 */
public interface HedgeFundScoringService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;


    HedgeFundScoringDto getScoring(Long id);
    HedgeFundScoringPagedSearchResult searchScoring(HedgeFundScoringSearchParams searchParams);
    Long save(HedgeFundScoringDto scoringDto, String username);

    //List<HedgeFundScreeningParsedDataDto> etCalculatedScoring(HedgeFundScoringFundParamsDto params);

    /*List<HedgeFundScreeningParsedDataDto>*/ ListResponseDto getCalculatedScoring(List<HedgeFundScreeningParsedDataDto> screeningList, HedgeFundScoringFundParamsDto scoringParams);

    ListResponseDto getCalculatedScoringAlternative(List<HedgeFundScreeningParsedDataDto> screeningList, HedgeFundScoringFundParamsDto scoringParams);
}
