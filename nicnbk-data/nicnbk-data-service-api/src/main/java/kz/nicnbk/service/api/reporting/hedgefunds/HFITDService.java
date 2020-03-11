package kz.nicnbk.service.api.reporting.hedgefunds;


import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITD;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITDHistorical;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.SingularityITDHistoricalRecordDto;
import kz.nicnbk.service.dto.reporting.SingularityITDRecordDto;
import kz.nicnbk.service.dto.reporting.SingularityNOALRecordDto;

import java.util.List;

public interface HFITDService extends BaseService {

    ReportingHFITD assemble(SingularityITDRecordDto dto);

    List<ReportingHFITD> assembleList(List<SingularityITDRecordDto> dtoList, Long reportId);

    //List<ReportingHFITDHistorical> assembleListHRS(List<SingularityITDHistoricalRecordDto> dtoList, Long reportId);

    boolean updateHRS(List<SingularityITDHistoricalRecordDto> dtoList, Long reportId);

    boolean save(List<ReportingHFITD> entities);

    //boolean saveHRS(List<ReportingHFITDHistorical> entities);

    ConsolidatedReportRecordHolderDto getParsedData(Long reportId);

    boolean deleteByReportId(Long reportId);

    List<SingularityITDHistoricalRecordDto> getHistoricalData();
}
