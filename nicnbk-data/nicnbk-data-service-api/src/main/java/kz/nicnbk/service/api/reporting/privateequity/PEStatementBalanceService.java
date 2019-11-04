package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementBalance;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.StatementBalanceOperationsDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonStatementBalanceOperationsHolderDto;

import java.util.List;

/**
 * Created by magzumov on 01.07.2017.
 */
public interface PEStatementBalanceService extends BaseService {

    ReportingPEStatementBalance assemble(ConsolidatedReportRecordDto dto, String trancheTypeNameEn, Long reportId);

    List<ReportingPEStatementBalance> assembleList(List<ConsolidatedReportRecordDto> dtoList, String trancheTypeNameEn, Long reportId);

    boolean save(List<ReportingPEStatementBalance> entities);

    ConsolidatedReportRecordHolderDto get(Long reportId);

    TarragonStatementBalanceOperationsHolderDto getStatementBalanceDto(Long reportId);

    boolean excludeIncludeTarragonRecord(Long recordId);

    boolean existEntityWithType(String code);

    List<StatementBalanceOperationsDto> getStatementBalanceRecords(Long reportId);

    boolean deleteByReportId(Long reportId);
}
