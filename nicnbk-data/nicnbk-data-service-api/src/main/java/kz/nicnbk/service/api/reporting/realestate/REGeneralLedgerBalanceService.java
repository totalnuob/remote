package kz.nicnbk.service.api.reporting.realestate;


import kz.nicnbk.repo.model.reporting.realestate.ReportingREGeneralLedgerBalance;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.SingularityGeneralLedgerBalanceRecordDto;

import java.util.List;

/**
 * Created by magzumov on 28.08.2017.
 */
public interface REGeneralLedgerBalanceService extends BaseService {

    ReportingREGeneralLedgerBalance assemble(SingularityGeneralLedgerBalanceRecordDto dto, Long reportId);

    List<ReportingREGeneralLedgerBalance> assembleList(List<SingularityGeneralLedgerBalanceRecordDto> dtoList, Long reportId);

    boolean save(List<ReportingREGeneralLedgerBalance> entities);

    ConsolidatedReportRecordHolderDto get(Long reportId);

}
