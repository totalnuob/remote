package kz.nicnbk.service.api.reporting.hedgefunds;


import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordHolderDto;
import kz.nicnbk.service.dto.reporting.SingularityNOALRecordDto;

import java.util.List;

/**
 * Created by magzumov on 28.08.2017.
 */
public interface HFNOALService extends BaseService {

    ReportingHFNOAL assemble(SingularityNOALRecordDto dto, Long reportId);

    List<ReportingHFNOAL> assembleList(List<SingularityNOALRecordDto> dtoList, Long reportId, int tranche);

    boolean save(List<ReportingHFNOAL> entities);

    ConsolidatedReportRecordHolderDto get(Long reportId, int tranche);

    boolean deleteByReportId(Long reportId, int tranche);
}
