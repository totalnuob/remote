package kz.nicnbk.service.api.m2s2;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.m2s2.InfrastructureMeetingMemoDto;

/**
 * Created by magzumov on 19.07.2016.
 */
public interface INFRMeetingMemoService extends BaseService {

    Long save(InfrastructureMeetingMemoDto memoDto, String updater);

    InfrastructureMeetingMemoDto get(Long id);
}
