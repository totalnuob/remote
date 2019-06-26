package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.monitoring.MonitoringHedgeFundDataDto;

import java.util.List;

/**
 * Created by magzumov.
 */
public interface MonitoringHedgeFundService extends BaseService {

    List<MonitoringHedgeFundDataDto> getAllData();

    boolean save(MonitoringHedgeFundDataDto dataDto, String username);
}