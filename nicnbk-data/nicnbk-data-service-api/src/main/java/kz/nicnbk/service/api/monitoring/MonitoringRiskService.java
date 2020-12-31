package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Pak on 20.06.2019.
 */

public interface MonitoringRiskService {

    MonitoringRiskHedgeFundReportDto getMonthlyHedgeFundReport(MonitoringRiskReportSearchParamsDto searchParamsDto);

    List<Date> getDateList();

    MonitoringRiskHedgeFundAllocationSubStrategyResultDto uploadStrategy(Set<FilesDto> filesDtoSet, String updater);

    MonitoringRiskHedgeFundAllocationResultDto uploadTopPortfolio(Set<FilesDto> filesDtoSet, String updater);


}
