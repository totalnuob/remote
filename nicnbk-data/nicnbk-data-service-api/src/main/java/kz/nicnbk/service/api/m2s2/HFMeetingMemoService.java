package kz.nicnbk.service.api.m2s2;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.m2s2.HedgeFundsMeetingMemoDto;

/**
 * Created by magzumov on 19.07.2016.
 */
public interface HFMeetingMemoService extends BaseService {

    Long save(HedgeFundsMeetingMemoDto memoDto);

    HedgeFundsMeetingMemoDto get(Long id);
}
