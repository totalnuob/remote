package kz.nicnbk.service.api.hr;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hr.HRNewsDto;

import java.util.List;

public interface HRNewsService extends BaseService{

    Long save(HRNewsDto newsItemDto, String updater);

    HRNewsDto get(Long id);

    boolean delete(Long id, String username);

    List<HRNewsDto> loadNewsShort(int page, int pageSize);
}
