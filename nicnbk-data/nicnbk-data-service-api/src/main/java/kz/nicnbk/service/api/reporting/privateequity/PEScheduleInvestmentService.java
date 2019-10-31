package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
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

    ReportingPEScheduleInvestment assemble(ScheduleInvestmentsDto dto, Long reportId);

    List<ReportingPEScheduleInvestment> assembleList(List<ConsolidatedReportRecordDto> dtoList, int tranche, Long reportId);

    List<ReportingPEScheduleInvestment> assembleList(List<ScheduleInvestmentsDto> dtoList, Long reportId);

    boolean save(List<ReportingPEScheduleInvestment> entities);

    boolean save(List<ScheduleInvestmentsDto> dtoList, Long reportId);

    boolean save(ScheduleInvestmentsDto dto);

    ConsolidatedReportRecordHolderDto get(Long reportId);

    List<ScheduleInvestmentsDto> getScheduleInvestments(Long reportId);

    ConsolidatedReportRecordHolderDto getSOIReport(Long reportId);

    EntitySaveResponseDto updateScheduleInvestments(UpdateTarragonInvestmentDto updateDto);

    ScheduleInvestmentsDto getScheduleInvestments(Long reportId, String fundName, int tranche);

    boolean deleteByReportId(Long reportId);

    boolean excludeIncludeTarragonRecord(Long recordId);


}
