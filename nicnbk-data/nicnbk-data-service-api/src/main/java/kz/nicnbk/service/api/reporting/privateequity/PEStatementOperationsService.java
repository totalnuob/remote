package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementOperations;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.StatementBalanceOperationsDto;

import java.util.List;

/**
 * Created by magzumov on 01.07.2017.
 */
public interface PEStatementOperationsService extends BaseService {

    ReportingPEStatementOperations assemble(ConsolidatedReportRecordDto dto, int tranche, Long reportId);

    List<ReportingPEStatementOperations> assembleList(List<ConsolidatedReportRecordDto> dtoList, int tranche, Long reportId);

    boolean save(List<ReportingPEStatementOperations> entities);

    ConsolidatedReportRecordHolderDto get(Long reportId);

    boolean existEntityWithType(String code);

    boolean excludeIncludeTarragonRecord(Long recordId);

    List<StatementBalanceOperationsDto> getStatementOperationsRecords(Long reportId);

    boolean deleteByReportId(Long reportId);
}
