package kz.nicnbk.service.api.reporting;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicReportService extends BaseService {

    Long savePeriodicReport(PeriodicReportDto dto, String updater);

    boolean deletePeriodicReportFileAssociationById(Long fileId);

    PeriodicReportDto getPeriodicReport(Long id);

    PeriodicReportDto findReportByReportDate(Date date);

    List<PeriodicReportDto> getAllPeriodicReports();

    PeriodicReportInputFilesHolder getPeriodicReportInputFiles(Long reportId);

    FilesDto getPeriodicReportFileByIdAndType(Long reportId, String type);

    FilesDto saveInputFile(Long reportId, FilesDto file);

    ConsolidatedReportRecordHolderDto getStatementBalanceOperations(Long reportId);

    ConsolidatedReportRecordHolderDto getNOAL(Long reportId, int tranche);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedBalanceUSDForm(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedIncomeExpenseUSDForm(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedTotalIncomeUSDForm(Long reportId);

    boolean safeDeleteFile(Long fileId);

    boolean markReportAsFinal(Long reportId);

    InputStream getExportFileStream(Long reportId, String type);

    List<ConsolidatedKZTForm8RecordDto> generateConsolidatedBalanceKZTForm8(Long reportId);

    List<ConsolidatedKZTForm10RecordDto> generateConsolidatedBalanceKZTForm10(Long reportId);

    List<ConsolidatedKZTForm14RecordDto> generateConsolidatedBalanceKZTForm14(Long reportId);

    List<ConsolidatedKZTForm13RecordDto> generateConsolidatedBalanceKZTForm13(Long reportId);

    List<ConsolidatedKZTForm7RecordDto> generateConsolidatedBalanceKZTForm7(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedBalanceKZTForm1(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedIncomeExpenseKZTForm2(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedTotalIncomeKZTForm3(Long reportId);

    List<ConsolidatedKZTForm19RecordDto> generateConsolidatedBalanceKZTForm19(Long reportId);

    List<ConsolidatedKZTForm22RecordDto> generateConsolidatedBalanceKZTForm22(Long reportId);

}
