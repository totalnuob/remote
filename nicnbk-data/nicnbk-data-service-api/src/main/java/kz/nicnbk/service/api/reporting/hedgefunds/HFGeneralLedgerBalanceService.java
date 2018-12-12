package kz.nicnbk.service.api.reporting.hedgefunds;

import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFGeneralLedgerBalance;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.SingularityAdjustmentsDto;
import kz.nicnbk.service.dto.reporting.SingularityGeneralLedgerBalanceRecordDto;

import java.util.List;

/**
 * Created by magzumov on 28.08.2017.
 */
public interface HFGeneralLedgerBalanceService extends BaseService {

    ReportingHFGeneralLedgerBalance assemble(SingularityGeneralLedgerBalanceRecordDto dto, Long reportId);

    SingularityGeneralLedgerBalanceRecordDto getRecordById(Long recordId);

    List<ReportingHFGeneralLedgerBalance> assembleList(List<SingularityGeneralLedgerBalanceRecordDto> dtoList, Long reportId);

    boolean save(List<ReportingHFGeneralLedgerBalance> entities);

    boolean excludeIncludeSingularityRecord(Long recordId, String username);

    ConsolidatedReportRecordHolderDto getWithExcludedRecords(Long reportId);

    ConsolidatedReportRecordHolderDto getWithoutExcludedRecords(Long reportId);

    boolean deleteByReportId(Long reportId);

    boolean saveAdjustments(SingularityAdjustmentsDto adjustmentsDto, String updater);

    List<SingularityGeneralLedgerBalanceRecordDto> getAdjustedRecords(Long reportId);

    boolean existEntityWithChartAccountsType(String code);
}
