package kz.nicnbk.service.api.monitoring;

import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.*;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Pak on 20.06.2019.
 */

public interface MonitoringRiskService {

    MonitoringRiskHedgeFundReportDto getMonthlyHedgeFundReport(MonitoringRiskReportSearchParamsDto searchParamsDto);

    MonitoringRiskHedgeFundReportDto getMonthlyHedgeFundRiskReport(MonitoringRiskReportSearchParamsDto searchParamsDto);

    List<Date> getDateList();

    List<Date> getReportDateList();

    MonitoringRiskHedgeFundAllocationSubStrategyResultDto uploadStrategy(Set<FilesDto> filesDtoSet, String updater);

    EntitySaveResponseDto saveReport(MonitoringRiskHFMonthlyReportDto reportDto);

    ResponseDto uploadHFReturns(Long reportId, FilesDto filesDto, String updater);

    ResponseDto uploadHFAllocations(Long reportId, FilesDto filesDto, String updater);

    MonitoringRiskHedgeFundAllocationResultDto uploadTopPortfolio(Set<FilesDto> filesDtoSet, String updater);

    ByteArrayInputStream exportTopPortfolio(Date date);

    boolean deletePortfolios(Date date, String updater);

    ByteArrayInputStream exportStrategy(Date date);

    boolean deleteStrategy(Date date, String updater);

    boolean deleteReturnsClassAFile(Long reportId, String updater);
    boolean deleteReturnsClassBFile(Long reportId, String updater);
    boolean deleteReturnsConsFile(Long reportId, String updater);
    boolean deleteAllocationsConsFile(Long reportId, String updater);

}
