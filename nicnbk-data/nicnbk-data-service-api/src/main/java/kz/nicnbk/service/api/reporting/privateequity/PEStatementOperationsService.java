package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementOperations;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.StatementBalanceOperationsDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonStatementBalanceOperationsHolderDto;

import java.util.List;

/**
 * Created by magzumov on 01.07.2017.
 */
public interface PEStatementOperationsService extends BaseService {

    ReportingPEStatementOperations assemble(ConsolidatedReportRecordDto dto, String trancheTypeNameEn, Long reportId);

    List<ReportingPEStatementOperations> assembleList(List<ConsolidatedReportRecordDto> dtoList, String trancheTypeNameEn, Long reportId);

    boolean save(List<ReportingPEStatementOperations> entities);

    ConsolidatedReportRecordHolderDto get(Long reportId);

    TarragonStatementBalanceOperationsHolderDto getStatementOperations(Long reportId);

    boolean existEntityWithType(String code);

    boolean excludeIncludeTarragonRecord(Long recordId);

    List<StatementBalanceOperationsDto> getStatementOperationsRecords(Long reportId);

    boolean deleteByReportId(Long reportId);
}
