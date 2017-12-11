package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.ScheduleInvestmentsDto;
import kz.nicnbk.service.dto.reporting.UpdateTarragonInvestmentDto;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface PEScheduleInvestmentService extends BaseService {

    ReportingPEScheduleInvestment assemble(ConsolidatedReportRecordDto dto, int tranche, Long reportId);

    List<ReportingPEScheduleInvestment> assembleList(List<ConsolidatedReportRecordDto> dtoList, int tranche, Long reportId);

    boolean save(List<ReportingPEScheduleInvestment> entities);

    boolean save(ScheduleInvestmentsDto dto);

    ConsolidatedReportRecordHolderDto get(Long reportId);

    List<ScheduleInvestmentsDto> getScheduleInvestments(Long reportId);

    boolean updateScheduleInvestments(UpdateTarragonInvestmentDto updateDto);

    ScheduleInvestmentsDto getScheduleInvestments(Long reportId, String fundName, int tranche);

    boolean deleteByReportId(Long reportId);


}
