package kz.nicnbk.service.api.reporting.realestate;

import kz.nicnbk.repo.model.reporting.realestate.ReportingREBalanceSheet;
import kz.nicnbk.repo.model.reporting.realestate.ReportingREProfitLoss;
import kz.nicnbk.repo.model.reporting.realestate.ReportingRESecuritiesCost;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;
import kz.nicnbk.service.dto.reporting.realestate.*;

import java.util.List;

/**
 * Created by magzumov on 17.01.2018.
 */
public interface PeriodicReportREService {

    ReportingREBalanceSheet assembleBalanceSheet(ConsolidatedReportRecordDto dto, Long reportId);

    List<ReportingREBalanceSheet> assembleBalanceSheetList(List<ConsolidatedReportRecordDto> dtoList, Long reportId);


    ReportingREProfitLoss assembleProfitLoss(ConsolidatedReportRecordDto dto, Long reportId);

    List<ReportingREProfitLoss> assembleProfitLossList(List<ConsolidatedReportRecordDto> dtoList, Long reportId);

    ReportingRESecuritiesCost assembleSecuritiesCost(ConsolidatedReportRecordDto dto, Long reportId);

    List<ReportingRESecuritiesCost> assembleSecuritiesCostList(List<ConsolidatedReportRecordDto> dtoList, Long reportId);

    boolean saveBalanceSheet(List<ReportingREBalanceSheet> entities, String username);

    boolean saveProfitLoss(List<ReportingREProfitLoss> entities, String username);

    boolean saveSecuritiesCost(List<ReportingRESecuritiesCost> entities, String username);

    boolean deleteByReportId(Long reportId);

    List<TerraBalanceSheetRecordDto> getBalanceSheetRecords(Long reportId);

    List<TerraProfitLossRecordDto> getProfitLossRecords(Long reportId);

    List<TerraSecuritiesCostRecordDto> getSecuritiesCostRecords(Long reportId);

    ListResponseDto getTerraGeneratedForm(Long reportId);

    ListResponseDto getTerraGeneratedFormWithoutExcluded(Long reportId);

    EntityListSaveResponseDto saveRealEstateGeneralLedgerFormData(RealEstateGeneralLedgerFormDataHolderDto dataHolderDto);

    boolean deleteRealEstateGeneralLedgerFormDataRecordById(Long recordId);

    List<TerraGeneratedGeneralLedgerFormDto> getTerraGLAddedRecordsPreviousMonth(Long reportId);

    boolean excludeIncludeTerraRecord(ExcludeTerraRecordDto excludeTerraRecordDto);

    TerraCombinedDataHolderDto getTerraCombinedParsedData(Long reportId);

    boolean existBalanceEntityWithType(String code);

    boolean existProfitLossEntityWithType(String code);


}
