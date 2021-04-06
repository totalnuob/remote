package kz.nicnbk.service.impl.common;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.common.CurrencyRatesRepository;
import kz.nicnbk.repo.model.common.CurrencyRates;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.converter.currency.CurrencyRatesEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesPagedSearchResult;
import kz.nicnbk.service.dto.lookup.CurrencyRatesSearchParams;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by magzumov on 20.11.2017.
 */

@Service
public class CurrencyRatesServiceImpl implements CurrencyRatesService {

    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRatesServiceImpl.class);

    @Autowired
    private CurrencyRatesRepository currencyRatesRepository;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private CurrencyRatesEntityConverter currencyRatesEntityConverter;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private EmployeeService employeeService;


    @Override
    public CurrencyRatesDto getRateForDateAndCurrency(Date date, String currencyCode){
        Date dateFormatted = DateUtils.getDateOnly(date);
        CurrencyRates entity = this.currencyRatesRepository.getRateForDateAndCurrency(dateFormatted, currencyCode);
        if(entity != null){
            CurrencyRatesDto dto = new CurrencyRatesDto();
            dto.setDate(entity.getDate());
            dto.setValue(entity.getValue());
            dto.setAverageValue(entity.getAverageValue());
            dto.setValueUSD(entity.getValueUSD());
            dto.setCurrency(new BaseDictionaryDto(entity.getCurrency().getCode(), entity.getCurrency().getNameEn(),
                    entity.getCurrency().getNameRu(), entity.getCurrency().getNameKz()));
            return dto;
        }
        return null;
    }

    @Override
    public CurrencyRatesDto getLstRateForMonthDateAndCurrencyBackwards(Date date, String currencyCode){
        Date dateFormatted = DateUtils.getDateOnly(date);
        boolean sameMonth = true;
        while(sameMonth) {
            CurrencyRates entity = this.currencyRatesRepository.getRateForDateAndCurrency(dateFormatted, currencyCode);
            if (entity != null) {
                CurrencyRatesDto dto = new CurrencyRatesDto();
                dto.setDate(entity.getDate());
                dto.setValue(entity.getValue());
                dto.setAverageValue(entity.getAverageValue());
                dto.setValueUSD(entity.getValueUSD());
                return dto;
            }else{

                Date newDateFormatted = DateUtils.getDateOnly(DateUtils.moveDateByDays(dateFormatted, -1, false));
                sameMonth = DateUtils.getMonth(dateFormatted) == DateUtils.getMonth(newDateFormatted);
                dateFormatted = newDateFormatted;
            }
        }
        return null;
    }

    @Override
    public Double getAverageYearRateForFixedDateAndCurrency(Date date, String currencyCode){
        Date dateFormatted = DateUtils.getDateOnly(date);

        CurrencyRates rates = this.currencyRatesRepository.getRateForDateAndCurrency(dateFormatted, currencyCode);

        if(rates != null && rates.getAverageValueYear() != null) {
            return rates.getAverageValueYear();
        }else{
            String errorMessage = "Average USD rate for date " + DateUtils.getDateFormatted(date) +" not found.";
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    @Override
    public CurrencyRatesPagedSearchResult search(CurrencyRatesSearchParams searchParams) {

//        Iterator<CurrencyRates> iterator = this.currencyRatesRepository.findAll().iterator();
//        while(iterator.hasNext()){
//            CurrencyRates entity = iterator.next();
//
//            this.currencyRatesRepository.delete(entity.getId());
//            entity.setId(null);
//            entity.setCreator(new Employee(35L));
//            entity.setCreationDate(new Date());
//            this.currencyRatesRepository.save(entity);
//        }

        if(searchParams == null){
            searchParams = new CurrencyRatesSearchParams();
            searchParams.setPage(0);
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }else if(searchParams.getPageSize() == 0){
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }

        CurrencyRatesPagedSearchResult result = new CurrencyRatesPagedSearchResult();
        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
        Page<CurrencyRates> entityPage = this.currencyRatesRepository.search(searchParams.getCurrencyCode(), searchParams.getFromDate(), searchParams.getToDate(),
                new PageRequest(page, searchParams.getPageSize(),
                new Sort(Sort.Direction.DESC, "date", "id")));
        if(entityPage != null && entityPage.getContent() != null){

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
                result.setCurrencyRates(this.currencyRatesEntityConverter.disassembleList(entityPage.getContent()));
            }
        }

        return result;
    }

    @Override
    public Double getAverageRateForAllMonthsBeforeDateAndCurrency(Date date, String currencyCode, int scale) {
        Date dateFormatted = DateUtils.getDateOnly(date);
        Date firstDay = DateUtils.getFirstDayOfDateYear(dateFormatted);

        Date dateTo = DateUtils.getFirstDayOfNextMonth(date);

        List<CurrencyRates> rates = this.currencyRatesRepository.getAverageRatesAfterDateAndCurrency(firstDay, dateTo, currencyCode);
        int months = DateUtils.getMonthsDifference(firstDay, dateTo);

        if(rates != null){
            //BigDecimal sum = new BigDecimal("0");
            Double sum = 0.0;
            int count = 0;
            for(CurrencyRates rate: rates){
                if(rate.getDate().compareTo(dateFormatted) <= 0 && rate.getAverageValue() != null){
                    sum  = MathUtils.add(sum, rate.getAverageValue().doubleValue());
                    count++;
                }
            }
            if(months != count){
                String errorMessage = "Average Monthly USD rate calculation error on date " + DateUtils.getDateFormatted(date) +
                        " (since " + DateUtils.getDateFormatted(firstDay) + ") : expected rate records for " + months + " months, found " + count + " rate records";
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            if(count > 0) {
                return MathUtils.divide(sum, count + 0.0);
            }
        }
        return null;
    }

    @Override
    public EntitySaveResponseDto save(CurrencyRatesDto dto, String username) {
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        try {
            if (dto != null) {
                CurrencyRates existingEntity = null;
                if(dto.getId() != null) {
                    existingEntity = this.currencyRatesRepository.findOne(dto.getId());

                    // Check existing date
                    CurrencyRates existingCurrencyRates =
                            this.currencyRatesRepository.getRateForDateAndCurrency(dto.getDate(), dto.getCurrency().getCode());
                    if (existingCurrencyRates != null && existingCurrencyRates.getId() != dto.getId()) {
                        String errorMessage = "Currency rate record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }
                }else {// New record
                    // Check existing date
                    CurrencyRates existingCurrencyRates =
                            this.currencyRatesRepository.getRateForDateAndCurrency(dto.getDate(), dto.getCurrency().getCode());
                    if (existingCurrencyRates != null) {
                        String errorMessage = "Currency rate record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }
                }

                saveResponse = checkCurrencyRateDto(dto);
                if(saveResponse.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
                    return saveResponse;
                }

                CurrencyRates entity = this.currencyRatesEntityConverter.assemble(dto);
                EmployeeDto employeeDto = this.employeeService.findByUsername(username);
                if(dto.getId() != null){
                    entity.setUpdateDate(new Date());
                    entity.setUpdater(new Employee(employeeDto.getId()));

                    if(existingEntity != null) {
                        entity.setCreator(existingEntity.getCreator());
                        entity.setCreationDate(existingEntity.getCreationDate());
                    }
                }else{
                    entity.setCreationDate(new Date());
                    entity.setCreator(new Employee(employeeDto.getId()));
                }

                this.currencyRatesRepository.save(entity);
                saveResponse.setSuccessMessageEn("Successfully saved.");
                logger.info("Successfully saved currency rate: id=" + dto.getId() + ", date=" + DateUtils.getDateFormatted(dto.getDate()) +
                        ", [user=" + username + "]");
            }
            return saveResponse;
        }catch (Exception ex){
            logger.error("Error saving currency rate", ex);
            saveResponse.setErrorMessageEn("Error saving currency rate");
            return saveResponse;
        }
    }

    private EntitySaveResponseDto checkCurrencyRateDto(CurrencyRatesDto dto){
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        saveResponse.setStatus(ResponseStatusType.SUCCESS);
        if(dto.getDate() != null && new Date().compareTo(dto.getDate()) < 0){
            String errorMessage = "Currency rate save failed: date " + DateUtils.getDateFormatted(dto.getDate()) +
                    ". Date cannot be greater than current date.";
            logger.error(errorMessage);
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }
        if((dto.getValue() != null && dto.getValue() <= 0) || (dto.getValueUSD() != null && dto.getValueUSD() <= 0)
            || dto.getQuoteCurrencyValue() != null && dto.getQuoteCurrencyValue() <= 0){
            String errorMessage = "Currency rate save failed: value must be positive";
            logger.error(errorMessage);
            saveResponse.setErrorMessageEn(errorMessage);
            return saveResponse;
        }
        if(dto.getAverageValue() != null){
            if(dto.getAverageValue() <= 0){
                String errorMessage = "Currency rate (average) save failed: value must be positive";
                logger.error(errorMessage);
                saveResponse.setErrorMessageEn(errorMessage);
                return saveResponse;
            }else{
                if(dto.getDate() == null ||
                        !DateUtils.isSameDate(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getDate())){
                    String errorMessage = "Currency rate (average) save failed: date must be last day of month";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        if(dto.getAverageValueYear() != null){
            if(dto.getAverageValueYear() <= 0){
                String errorMessage = "Currency rate (average year) save failed: value must be positive";
                logger.error(errorMessage);
                saveResponse.setErrorMessageEn(errorMessage);
                return saveResponse;
            }else{
                if(dto.getDate() == null ||
                        !DateUtils.isSameDate(DateUtils.getLastDayOfCurrentYear(dto.getDate()), dto.getDate())){
                    String errorMessage = "Currency rate (average year) save failed: date must be December 31";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
            }
        }
        return saveResponse;
    }


    @Override
    public EntityListSaveResponseDto save(List<CurrencyRatesDto> dtoList, String username) {
        EntityListSaveResponseDto saveListResponse = new EntityListSaveResponseDto();
        for(CurrencyRatesDto dto: dtoList){
            EntitySaveResponseDto saveResponseDto = save(dto, username);
            if(saveResponseDto.getStatus() != null && saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
                // Error
                saveListResponse.setErrorMessageEn(saveResponseDto.getMessage().getNameEn());
                saveListResponse.setStatus(ResponseStatusType.FAIL);

                return saveListResponse;
            }
        }
        saveListResponse.setSuccessMessageEn("Successfully saved currency rate list");
        return saveListResponse;
    }

    @Override
    public boolean delete(Long id, String username) {
        try {
            CurrencyRates currencyRate = this.currencyRatesRepository.findOne(id);
            if(currencyRate != null){
                // Check if can delete
                // Get date of most recent report with status SUBMITTED
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
                if(mostRecentFinalReportDate != null && currencyRate.getDate().compareTo(mostRecentFinalReportDate) <= 0){
                    String errorMessage = "Currency rate record delete failed: record id " + currencyRate.getId() +
                            ": finalized report exists with date '" + DateUtils.getDateFormatted(mostRecentFinalReportDate) +
                            "', record date '" + DateUtils.getDateFormatted(currencyRate.getDate()) + "'";
                    logger.error(errorMessage);
                    return false;
                }

                // TODO: check with screening and scoring
                //

                this.currencyRatesRepository.delete(id);
                logger.info("Successfully deleted currency rate: id=" +id + ", date=" +
                        DateUtils.getDateFormatted(currencyRate.getDate()) + "[user=" + username + "]");
                return true;

            }else{
                logger.error("Error deleting currency rate record: no record found with id " + id);
                return false;
            }

        }catch (Exception ex){
            logger.error("Error deleting currency rate record: id " + id, ex);
            return false;
        }
    }

    @Override
    public Double getUSDValueRateForDateAndCurrency(Date date, String currencyCode) {
        CurrencyRatesDto currencyRatesDto = getRateForDateAndCurrency(date, currencyCode);
        if(currencyRatesDto != null && currencyRatesDto.getValueUSD() != null){
            return currencyRatesDto.getValueUSD();
        }
        return null;
    }

    @Override
    public List<CurrencyRatesDto> getRatesEndOfMonthForDateRangeAndCurrencies(Date dateFrom, Date dateTo, Set<String> currencies) {
        List<CurrencyRatesDto> currencyRates = new ArrayList<>();
        dateTo = DateUtils.getLastDayOfCurrentMonth(dateTo);
        dateFrom = DateUtils.getLastDayOfCurrentMonth(dateFrom);
        for(String currency: currencies){
            Date date = dateFrom;
            while(date.before(dateTo) || date.compareTo(dateTo) == 0) {
                //System.out.println(currency + " - " + DateUtils.getDateFormatted(date));
                CurrencyRatesDto currencyRatesDto = getRateForDateAndCurrency(date, currency);
                if(currencyRatesDto != null) {
                    currencyRates.add(currencyRatesDto);
                }
                date = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(date, 1));
            }
        }
        return currencyRates;
    }

}
