package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.monitoring.*;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov.
 */
public interface MonitoringHedgeFundService extends BaseService {

    MonitoringHedgeFundListDataHolderDto getAllData();

    MonitoringHedgeFundDataHolderDto getMonitoringDataByDate(MonitoringHedgeFundSearchParamsDto searchParams);

    MonitoringHedgeFundDataDto getMonitoringDataForPreviousDate(MonitoringHedgeFundTypedSearchParamsDto searchParams);

    EntitySaveResponseDto save(MonitoringHedgeFundDataHolderDto dataDto, String username);
}