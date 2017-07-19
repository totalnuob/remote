package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementCashflows;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;

import java.util.List;

/**
 * Created by magzumov on 01.07.2017.
 */
public interface PEStatementCashflowsService extends BaseService {

    ReportingPEStatementCashflows assemble(ConsolidatedReportRecordDto dto, Long reportId);

    List<ReportingPEStatementCashflows> assembleList(List<ConsolidatedReportRecordDto> dtoList, Long reportId);

    boolean save(List<ReportingPEStatementCashflows> entities);

    ConsolidatedReportRecordHolderDto get(Long reportId);
}
