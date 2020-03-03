package kz.nicnbk.service.api.legal;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.legal.LegalUpdateDto;

import java.util.List;

public interface LegalUpdateService extends BaseService{

    Long save(LegalUpdateDto newsItemDto, String updater);

    LegalUpdateDto get(Long id);

    boolean delete(Long id, String username);

    List<LegalUpdateDto> loadNewsShort(int pageSize);

    List<LegalUpdateDto> loadNewsShort(int page, int pageSize);
}
