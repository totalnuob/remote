package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.monitoring.MonitoringHedgeFundDataDto;
import kz.nicnbk.service.dto.monitoring.MonitoringHedgeFundDataHolderDto;
import kz.nicnbk.service.dto.monitoring.MonitoringHedgeFundSearchParamsDto;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov.
 */
public interface MonitoringHedgeFundService extends BaseService {

    List<MonitoringHedgeFundDataHolderDto> getAllData();

    MonitoringHedgeFundDataHolderDto getMonitoringDataByDate(MonitoringHedgeFundSearchParamsDto searchParams);

    EntitySaveResponseDto save(MonitoringHedgeFundDataHolderDto dataDto, String username);
}