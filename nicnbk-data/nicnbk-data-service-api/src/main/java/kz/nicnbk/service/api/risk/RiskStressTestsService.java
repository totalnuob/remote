package kz.nicnbk.service.api.risk;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.monitoring.RiskStressTestsDto;

import java.util.Date;
import java.util.List;

public interface RiskStressTestsService  extends BaseService {

    List<RiskStressTestsDto> getStressTestsByDate(Date date);
}
