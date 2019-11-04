package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonStatementBalanceOperationsHolderDto;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicReportService extends BaseService {

    EntitySaveResponseDto savePeriodicReport(PeriodicReportDto dto, String updater);

    boolean deletePeriodicReportFileAssociationById(Long fileId);

    PeriodicReportDto getPeriodicReport(Long id);

    PeriodicReportDto findReportByReportDate(Date date);

    List<PeriodicReportDto> getAllPeriodicReports();

    PeriodicReportInputFilesHolder getPeriodicReportInputFiles(Long reportId);

    FilesDto getPeriodicReportFileByIdAndType(Long reportId, String type);

    //EntitySaveResponseDto saveInterestRate(Long reportId, String interestRate, String updater);

    FilesDto saveInputFile(Long reportId, FilesDto file);

    TarragonStatementBalanceOperationsHolderDto getStatementBalanceOperations(Long reportId);

    ConsolidatedReportRecordHolderDto getNOAL(Long reportId, int tranche);

    ListResponseDto generateConsolidatedBalanceUSDForm(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedIncomeExpenseUSDForm(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> generateConsolidatedTotalIncomeUSDForm(Long reportId);

    boolean safeDeleteFile(Long fileId, String username);

    boolean markReportAsFinal(Long reportId);

    ReportingFundRenameInfoDto getFundRenameInfo(Long reportId);

    boolean saveFundRenameInfo(ReportingFundRenameInfoDto info);

    ReportingFundNameListHolderDto getFundNameList(Long reportId);

    //InputStream getExportFileStream(Long reportId, String type);
    FilesDto getExportFileStream(Long reportId, String type);

    FilesDto getExportAllKZTReportsFileStream(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm6(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm8(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm10(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm14(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm13(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm7(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm1(Long reportId);

    ListResponseDto generateConsolidatedIncomeExpenseKZTForm2(Long reportId);

    ListResponseDto generateConsolidatedTotalIncomeKZTForm3(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm19(Long reportId);

    ListResponseDto generateConsolidatedBalanceKZTForm22(Long reportId);
}
