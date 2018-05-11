package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.ReserveCalculationDto;
import kz.nicnbk.service.dto.reporting.ReserveCalculationExportParamsDto;
import kz.nicnbk.service.dto.reporting.ReserveCalculationPagedSearchResult;
import kz.nicnbk.service.dto.reporting.ReserveCalculationSearchParams;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReserveCalculationService extends BaseService {

    /* Number of pages per view */
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    List<ReserveCalculationDto> getAllReserveCalculations();

    ReserveCalculationPagedSearchResult search(ReserveCalculationSearchParams searchParams);

    List<ReserveCalculationDto> getReserveCalculationsForMonth(String code, Date date, boolean useValuationDate);

    Double getReserveCalculationSumKZTForMonth(String code, Date date);

    boolean save(List<ReserveCalculationDto> records);

    //List<ReserveCalculationDto> getReserveCalculationsByExpenseType(String code);

    List<ReserveCalculationDto> getReserveCalculationsByExpenseTypeBeforeDate(String code, Date date);

    List<ReserveCalculationDto> getReserveCalculationsByExpenseTypeAfterDate(String code, Date date);

    FilesDto getExportFileStream(Long recordId, String type, ReserveCalculationExportParamsDto exportParamsDto);

    boolean deleteReserveCalculationRecord(Long recordId);

}
