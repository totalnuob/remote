package kz.nicnbk.service.api.reporting.realestate;


import kz.nicnbk.repo.model.reporting.realestate.ReportingREGeneralLedgerBalance;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.SingularityGeneralLedgerBalanceRecordDto;
import kz.nicnbk.service.dto.reporting.realestate.TerraGeneralLedgerBalanceRecordDto;

import java.util.List;

/**
 * Created by magzumov on 28.08.2017.
 */
public interface REGeneralLedgerBalanceService extends BaseService {

    ReportingREGeneralLedgerBalance assemble(TerraGeneralLedgerBalanceRecordDto dto, Long reportId);

    List<ReportingREGeneralLedgerBalance> assembleList(List<TerraGeneralLedgerBalanceRecordDto> dtoList, Long reportId);

    boolean save(List<ReportingREGeneralLedgerBalance> entities);

    //ConsolidatedReportRecordHolderDto get(Long reportId);

    boolean deleteByReportId(Long reportId);

    List<TerraGeneralLedgerBalanceRecordDto> getRecordsByReportId(Long reportId);

    boolean excludeIncludeRecord(Long recordId);
    boolean excludeRecord(Long recordId);
    boolean includeRecord(Long recordId);
}
