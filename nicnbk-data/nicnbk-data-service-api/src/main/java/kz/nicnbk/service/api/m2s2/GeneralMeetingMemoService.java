package kz.nicnbk.service.api.m2s2;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.m2s2.GeneralMeetingMemoDto;

/**
 * Created by magzumov on 19.07.2016.
 */
public interface GeneralMeetingMemoService extends BaseService {

    Long save(GeneralMeetingMemoDto memoDto, String updater);

    GeneralMeetingMemoDto get(Long id);

    boolean checkOwner(String token, Long memoId);
}
