package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicReportService extends BaseService {

    Long save(PeriodicReportDto dto, String updater);

    boolean deleteFile(Long fileId);

    PeriodicReportDto get(Long id);

    List<PeriodicReportDto> getAll();

    List<FilesDto> getPeriodicReportFiles(Long reportId);

    FilesDto saveInputFile(Long reportId, FilesDto file);

    FileUploadResultDto parseFile(String fileType, FilesDto filesDto, Long reportId);

    ConsolidatedReportRecordHolderDto getScheduleInvestments(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementBalanceOperations(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementCashflows(Long reportId);
}
