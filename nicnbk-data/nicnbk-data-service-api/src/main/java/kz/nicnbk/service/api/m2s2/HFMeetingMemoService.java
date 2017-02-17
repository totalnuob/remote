package kz.nicnbk.service.api.m2s2;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.m2s2.HedgeFundsMeetingMemoDto;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParams;

/**
 * Created by magzumov on 19.07.2016.
 */
public interface HFMeetingMemoService extends BaseService {

    Long save(HedgeFundsMeetingMemoDto memoDto);

    HedgeFundsMeetingMemoDto get(Long id);

    MemoPagedSearchResult search(MemoSearchParams searchParams);
}
