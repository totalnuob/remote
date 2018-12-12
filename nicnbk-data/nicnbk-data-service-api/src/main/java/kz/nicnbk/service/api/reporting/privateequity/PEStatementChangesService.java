package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementChanges;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.StatementChangesDto;

import java.util.List;

/**
 * Created by magzumov on 01.07.2017.
 */
public interface PEStatementChangesService extends BaseService {

    ReportingPEStatementChanges assemble(ConsolidatedReportRecordDto dto, Long reportId);

    List<ReportingPEStatementChanges> assembleList(List<ConsolidatedReportRecordDto> dtoList, Long reportId);

    boolean save(List<ReportingPEStatementChanges> entities);

    ConsolidatedReportRecordHolderDto get(Long reportId);

    List<StatementChangesDto> getStatementChanges(Long reportId);

    boolean deleteByReportId(Long reportId);

    boolean excludeIncludeTarragonRecord(Long recordId);
}
