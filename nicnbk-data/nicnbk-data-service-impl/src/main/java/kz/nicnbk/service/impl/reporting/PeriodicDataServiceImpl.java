package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.PeriodicDataRepository;
import kz.nicnbk.repo.model.reporting.PeriodicData;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.converter.reporting.PeriodicDataEntityConverter;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.impl.reporting.lookup.PeriodicDataTypeLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 18.07.2017.
 */
@Service
public class PeriodicDataServiceImpl implements PeriodicDataService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicDataServiceImpl.class);

    // TODO: refactor
    public static final String CASHFLOW_BEGINNING_PERIOD_TRANCHE_A = "CASH_BGN_A";
    public static final String CASHFLOW_BEGINNING_PERIOD_TRANCHE_B = "CASH_BGN_B";

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGES_PER_VIEW = 5;


    @Autowired
    private PeriodicDataRepository periodicDataRepository;

    @Autowired
    private PeriodicDataEntityConverter periodicDataEntityConverter;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Override
    public PeriodicDataDto get(Date date, String type) {
        PeriodicData entity = this.periodicDataRepository.getByDateAndType(date, type);
        if(entity != null){
            PeriodicDataDto dto = new PeriodicDataDto(entity.getDate(), entity.getType().getCode(), entity.getValue());
            dto.setEditable(true);
            return dto;
        }
        return null;
    }

    @Override
    public PeriodicDataDto getCashflowBeginningPeriod(Date date, int tranche) {
        if(tranche == 1){
            return get(date, CASHFLOW_BEGINNING_PERIOD_TRANCHE_A);
        }else {
            return get(date, CASHFLOW_BEGINNING_PERIOD_TRANCHE_B);
        }
    }

    @Override
    public PeriodicDataDto getCashflowBeginningPeriodForYear(int year, int tranche) {
        Date date = DateUtils.getFirstDayYear(year);
        return getCashflowBeginningPeriod(date, tranche);
    }

    @Override
    public PeriodicDataPagedSearchResultDto searchPeriodicData(PeriodicDataSearchParamsDto searchParams) {
        if(searchParams == null || searchParams.isEmpty()){
            searchParams = new PeriodicDataSearchParamsDto();
            searchParams.setPage(0);
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }else if(searchParams.getPageSize() == 0){
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }

        PeriodicDataPagedSearchResultDto result = new PeriodicDataPagedSearchResultDto();
        Page<PeriodicData> entityPage = this.periodicDataRepository.search(searchParams.getDateFrom(), searchParams.getDateTo(), searchParams.getType(),
                new PageRequest(searchParams.getPage(), searchParams.getPageSize(),
                        new Sort(Sort.Direction.DESC, "date", "id")));
        if(entityPage != null && entityPage.getContent() != null){

            int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            result.setTotalElements(entityPage.getTotalElements());
            if (entityPage.getTotalElements() > 0) {
                result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page + 1));
                result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                        page + 1, result.getShowPageFrom(), entityPage.getTotalPages()));
            }
            result.setTotalPages(entityPage.getTotalPages());
            result.setCurrentPage(page + 1);
            if (searchParams != null) {
                result.setSearchParams(searchParams.getSearchParamsAsString());
            }

            if(entityPage != null && entityPage.getContent() != null) {
                result.setPeriodicData(this.periodicDataEntityConverter.disassembleList(entityPage.getContent()));

                for(PeriodicDataDto dto: result.getPeriodicData()){
                    dto.setEditable(true);
                }
            }
        }

        return result;
    }

    @Override
    public EntitySaveResponseDto save(PeriodicDataDto dto, String username) {
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        try {
            if (dto != null) {
                if(dto.getType() == null || StringUtils.isEmpty(dto.getType().getCode())){
                    String errorMessage = "Error saving periodic data: type is undefined";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
                if(dto.getValue() == null){
                    String errorMessage = "Error saving periodic data: value is missing";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
                if(dto.getDate() == null){
                    String errorMessage = "Error saving periodic data: date is missing";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }

                if(dto.getDate() != null && new Date().compareTo(dto.getDate()) < 0){
                    String errorMessage = "Error saving periodic data for date " + DateUtils.getDateFormatted(dto.getDate()) +
                            ". Date cannot be greater than current date.";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
                if(dto.getType() != null && (dto.getType().getCode().equalsIgnoreCase(PeriodicDataTypeLookup.NET_PROFIT.getCode()) ||
                        dto.getType().getCode().equalsIgnoreCase(PeriodicDataTypeLookup.RESERVE_REVALUATION.getCode()))){
                    if(!DateUtils.getDateFormatted(dto.getDate()).startsWith("31.12.")){
                        String errorMessage = "Error saving periodic data for this type: date must be 31.12.YYYY";
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }
                }

                // Check existing
                PeriodicData existingPeriodicData =
                        this.periodicDataRepository.getByDateAndType(dto.getDate(), dto.getType().getCode());

                if (existingPeriodicData != null && (dto.getId() == null || existingPeriodicData.getId() != dto.getId())) {
                    String errorMessage = "Periodic data record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate()) +
                            " and type.";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }

//                if(dto.getValue() <= 0){
//                    String errorMessage = "Periodic data save failed: value must be positive";
//                    logger.error(errorMessage);
//                    saveResponse.setErrorMessageEn(errorMessage);
//                    return saveResponse;
//                }

                PeriodicData entity = this.periodicDataEntityConverter.assemble(dto);
                entity.setRevaluated(null);
                if(entity.getId() != null && (entity.getType().getCode().equalsIgnoreCase(PeriodicDataTypeLookup.NET_PROFIT.getCode()) ||
                        entity.getType().getCode().equalsIgnoreCase(PeriodicDataTypeLookup.RESERVE_REVALUATION.getCode()))){
                    if(dto.getRevaluated() != null && dto.getRevaluated().booleanValue()){
                        // 1. Подчет резерва ОФП-1
                        // Если переоценка сделана (обновлен NET_PROFIT, RESERVE за год), то Prev Year Input данные не включаются.
                        entity.setRevaluated(true);
                    }
                }

                this.periodicDataRepository.save(entity);
                saveResponse.setSuccessMessageEn("Successfully saved record.");
                logger.info("Successfully saved Periodic data record: id=" + dto.getId() + ", type=" + dto.getType() +
                        ", date=" + DateUtils.getDateFormatted(dto.getDate()) + ", value=" + entity.getValue().doubleValue() + " [user=" + username + "]");
            }else{
                String errorMessage = "Error saving periodic data: data is null";
                logger.error(errorMessage);
                saveResponse.setErrorMessageEn(errorMessage);
                return saveResponse;
            }
        }catch (Exception ex){
            logger.error("Error saving Periodic data", ex);
            saveResponse.setErrorMessageEn("Error saving Periodic data");
            return saveResponse;
        }
        return saveResponse;

    }

    @Override
    public boolean delete(Long id, String username) {
        try {
            PeriodicData periodicData = this.periodicDataRepository.findOne(id);
            if(periodicData != null){
                // Check if can delete
                // Get date of most recent report with status SUBMITTED
//                Date mostRecentFinalReportDate = null;
//                List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
//                if(periodicReportDtos != null){
//                    for(PeriodicReportDto periodicReportDto: periodicReportDtos){
//                        if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
//                            if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
//                                mostRecentFinalReportDate = periodicReportDto.getReportDate();
//                            }
//                        }
//                    }
//                }
//
//                // Cannot delete record with date earlier than most recent SUBMITTED report date
//                if(mostRecentFinalReportDate != null && periodicData.getDate().compareTo(mostRecentFinalReportDate) <= 0){
//                    String errorMessage = "Periodic data record delete failed: record id " + periodicData.getId() +
//                            ": finalized report exists with date '" + DateUtils.getDateFormatted(mostRecentFinalReportDate) +
//                            "', record date '" + DateUtils.getDateFormatted(periodicData.getDate()) + "'";
//                    logger.error(errorMessage);
//                    return false;
//                }

                this.periodicDataRepository.delete(id);
                logger.info("Successfully deleted Periodic data record: id=" + id + ", type=" + periodicData.getType().getCode() +
                        ", date=" + DateUtils.getDateFormatted(periodicData.getDate()) + ", value=" + periodicData.getValue().doubleValue()
                        + " [user=" + username + "]");
                return true;

            }else{
                logger.error("Error deleting periodic data record: no record found with id " + id);
                return false;
            }

        }catch (Exception ex){
            logger.error("Error deleting currency rate record: id " + id, ex);
            return false;
        }
    }




    // TODO: replace with this method

    private boolean checkEditablePeriodicData(Date date){
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        // Cannot delete record with date earlier than most recent SUBMITTED report date
        if(mostRecentFinalReportDate != null && date.compareTo(mostRecentFinalReportDate) <= 0){
//            String errorMessage = "Periodic data record delete failed: record id " + periodicData.getId() +
//                    ": finalized report exists with date '" + DateUtils.getDateFormatted(mostRecentFinalReportDate) +
//                    "', record date '" + DateUtils.getDateFormatted(periodicData.getDate()) + "'";
//            logger.error(errorMessage);
            return false;
        }
        return true;
    }
}
