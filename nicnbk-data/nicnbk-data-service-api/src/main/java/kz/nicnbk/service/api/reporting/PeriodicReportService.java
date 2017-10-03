package kz.nicnbk.service.api.reporting;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;

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

    FilesDto getPeriodicReportFile(Long reportId, String type);

    FilesDto saveInputFile(Long reportId, FilesDto file);

    FileUploadResultDto parseFile(String fileType, FilesDto filesDto, Long reportId);

    ConsolidatedReportRecordHolderDto getScheduleInvestments(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementBalanceOperations(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementCashflows(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementChanges(Long reportId);

    ConsolidatedReportRecordHolderDto getGeneralLedgerBalance(Long reportId);

    ConsolidatedReportRecordHolderDto getNOAL(Long reportId, int tranche);

    boolean saveOtherInfo(ReportOtherInfoDto dto);

    ReportOtherInfoDto getOtherInfo(Long reportId);

    boolean saveNICKMFReportingData(NICKMFReportingDataHolderDto dataHolderDto);

    NICKMFReportingDataHolderDto getNICKMFReportingData(Long reportId);

    NICKMFReportingDataHolderDto getNICKMFReportingDataFromPreviousMonth(Long reportId);

    boolean safeDelete(Long reportId, FileTypeLookup fileTypeLookup, String username);
}
