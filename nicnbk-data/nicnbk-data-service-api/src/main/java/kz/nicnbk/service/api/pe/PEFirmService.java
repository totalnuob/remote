package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEPagedSearchResult;
import kz.nicnbk.service.dto.pe.PESearchParams;


/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PEFirmService extends BaseService {
    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    Long save(PEFirmDto firmDto);

    PEFirmDto get(Long id);

    PEPagedSearchResult findByName(PESearchParams searchParams);
}
