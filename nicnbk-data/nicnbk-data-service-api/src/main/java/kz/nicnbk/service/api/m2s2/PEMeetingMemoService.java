package kz.nicnbk.service.api.m2s2;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.m2s2.MemoPagedSearchResult;
import kz.nicnbk.service.dto.m2s2.MemoSearchParams;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;

/**
 * Created by magzumov on 19.07.2016.
 */
public interface PEMeetingMemoService extends BaseService {

    Long save(PrivateEquityMeetingMemoDto memoDto, String updater);

    PrivateEquityMeetingMemoDto get(Long id);

    MemoPagedSearchResult search(MemoSearchParams searchParams);
}
