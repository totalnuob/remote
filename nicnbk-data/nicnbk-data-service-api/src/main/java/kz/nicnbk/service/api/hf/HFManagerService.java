package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.hf.HedgeFundManagerPagedSearchResult;
import kz.nicnbk.service.dto.hf.HedgeFundSearchParams;

import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */
public interface HFManagerService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    Long save(HFManagerDto firmDto, String updater);

    HFManagerDto get(Long id);

    HedgeFundManagerPagedSearchResult findByName(HedgeFundSearchParams searchParams);

    List<HFManagerDto> findAll();

    List<HFManagerDto> findInvestedInBFunds();

}
