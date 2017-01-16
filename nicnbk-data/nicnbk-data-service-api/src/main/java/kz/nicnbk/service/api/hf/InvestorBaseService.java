package kz.nicnbk.service.api.hf;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.hf.InvestorBaseDto;

import java.util.List;

/**
 * Created by magzumov on 15.12.2016.
 */
public interface InvestorBaseService  extends BaseService {

    Long save(InvestorBaseDto dto);

    boolean deleteByFundId(Long fundId);

    InvestorBaseDto get(Long id);

    List<InvestorBaseDto> findByFundId(Long id);
}
