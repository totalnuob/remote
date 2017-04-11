package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.HedgeFundDto;
import kz.nicnbk.service.dto.hf.HedgeFundPagedSearchResult;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;

import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
public interface HedgeFundService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    Long save(HedgeFundDto hedgeFundDto, String username);

    HedgeFundDto get(Long id);

    List<HedgeFundDto> loadManagerFunds(Long managerId);

    HedgeFundPagedSearchResult findByName(HedgeFundSearchParams searchParams);
}
