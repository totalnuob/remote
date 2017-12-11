package kz.nicnbk.service.api.reporting;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;

import java.io.InputStream;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicReportService extends BaseService {

    Long save(PeriodicReportDto dto, String updater);

    boolean deleteFile(Long fileId);

    PeriodicReportDto get(Long id);

    List<PeriodicReportDto> getAll();

    PeriodicReportInputFilesHolder getPeriodicReportFiles(Long reportId);

    FilesDto getPeriodicReportFile(Long reportId, String type);

    FilesDto saveInputFile(Long reportId, FilesDto file);

    FileUploadResultDto parseFile(String fileType, FilesDto filesDto, Long reportId);

    ConsolidatedReportRecordHolderDto getScheduleInvestments(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementBalanceOperations(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementCashflows(Long reportId);

    ConsolidatedReportRecordHolderDto getStatementChanges(Long reportId);

    ConsolidatedReportRecordHolderDto getGeneralLedgerBalance(Long reportId);

    List<GeneratedGeneralLedgerFormDto> getSingularGeneratedForm(Long reportId);

    ListResponseDto getTarragonGeneratedForm(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceUSDForm(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseUSDForm(Long reportId);
    List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeUSDForm(Long reportId);

    ConsolidatedReportRecordHolderDto getNOAL(Long reportId, int tranche);

    boolean saveOtherInfo(ReportOtherInfoDto dto);

    ReportOtherInfoDto getOtherInfo(Long reportId);

    boolean saveNICKMFReportingData(NICKMFReportingDataHolderDto dataHolderDto);

    NICKMFReportingDataHolderDto getNICKMFReportingData(Long reportId);

    NICKMFReportingDataHolderDto getNICKMFReportingDataFromPreviousMonth(Long reportId);

    boolean safeDelete(Long reportId, FileTypeLookup fileTypeLookup, String username);

    boolean savePEGeneralLedgerFormData(PEGeneralLedgerFormDataHolderDto dataHolderDto);

    boolean deletePEGeneralLedgerFormDataRecordById(Long recordId);

    boolean saveUpdatedTarragonInvestment(UpdateTarragonInvestmentDto updateDto);

    List<PreviousYearInputDataDto> getPreviousYearInputData(Long reportId);

    List<PreviousYearInputDataDto> getPreviousYearInputDataFromPreviousMonth(Long reportId);

    boolean savePreviousYearInputData(List<PreviousYearInputDataDto> records, Long reportId);

    boolean safeDeleteFile(Long fileId);

    boolean markReportAsFinal(Long reportId);

    InputStream getExportFileStream(Long reportId, String type);

    List<GeneratedGeneralLedgerFormDto> getTarragonGLAddedRecordsPreviousMonth(Long reportId);

    List<ConsolidatedKZTForm8RecordDto> getConsolidatedBalanceKZTForm8(Long reportId);

    List<ConsolidatedKZTForm10RecordDto> getConsolidatedBalanceKZTForm10(Long reportId);

    List<ConsolidatedKZTForm14RecordDto> getConsolidatedBalanceKZTForm14(Long reportId);

    List<ConsolidatedKZTForm13RecordDto> getConsolidatedBalanceKZTForm13(Long reportId);

    List<ConsolidatedKZTForm7RecordDto> getConsolidatedBalanceKZTForm7(Long reportId);

    List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm1(Long reportId);

    List<ReserveCalculationDto> getReserveCalculation();

    boolean saveReserveCalculation(List<ReserveCalculationDto> records);

}
