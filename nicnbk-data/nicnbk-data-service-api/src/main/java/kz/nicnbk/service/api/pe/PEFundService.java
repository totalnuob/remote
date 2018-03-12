package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.pe.*;

import java.util.Date;
import java.util.List;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public interface PEFundService extends BaseService {

    PEFundDto get(Long fundId);

    Long save(PEFundDto fundDto, String username);

    PECompanyPerformanceResultDto savePerformance(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    PEFundTrackRecordResultDto calculateTrackRecord(Long fundId, int calculationType);

    PEFundTrackRecordResultDto updateStatistics(Long fundId);

    PECompanyPerformanceAndFundTrackRecordResultDto savePerformanceAndUpdateStatistics(List<PECompanyPerformanceDto> performanceDtoList, Long fundId, String username);

    PEGrossCashflowResultDto saveGrossCF(List<PEGrossCashflowDto> grossCashflowDtoList, Long fundId, String username);

    PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto saveGrossCFAndRecalculatePerformanceIddAndUpdateStatistics(List<PEGrossCashflowDto> grossCashflowDtoList, Long fundId, String username);

    List<PEFundDto> loadFirmFunds(Long firmId, boolean report);

//    Date updateAsOfDateOnePager(Date asOfDateOnePager, Long fundId);

    String getIndustriesAsString(Long fundId);
}