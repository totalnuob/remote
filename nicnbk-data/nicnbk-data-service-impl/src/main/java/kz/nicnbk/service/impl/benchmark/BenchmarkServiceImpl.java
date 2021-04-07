package kz.nicnbk.service.impl.benchmark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.benchmark.BenchmarkValueRepository;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.repo.model.bloomberg.SecurityData;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
import kz.nicnbk.repo.model.lookup.BloombergStationLookup;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.benchmark.BenchmarkValueEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.bloomberg.FieldDataDto;
import kz.nicnbk.service.dto.bloomberg.ResponseDto;
import kz.nicnbk.service.dto.bloomberg.SecurityDataDto;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.lookup.BenchmarkPagedSearchResult;
import kz.nicnbk.service.dto.lookup.BenchmarkSearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by magzumov on 05.01.2017.
 */

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkServiceImpl.class);

    @Autowired
    private BenchmarkValueRepository benchmarkValueRepository;

    @Autowired
    private BenchmarkValueEntityConverter benchmarkValueEntityConverter;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LookupService lookupService;

    @Override
    public List<BenchmarkValueDto> getValuesFromDateAsList(Date fromDate, String benchmarkCode) {

        // TODO: pagination
        int pageNumber = 0;
        int pageSize = Integer.MAX_VALUE;
        Page<BenchmarkValue> entites = this.benchmarkValueRepository.getValuesFromDate(fromDate, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));

        List<BenchmarkValueDto> dtoList = this.benchmarkValueEntityConverter.disassembleList(entites.getContent());
        return dtoList;
    }

    @Override
    public double[] getReturnValuesFromDateAsArray(Date fromDate, String benchmarkCode) {
        // TODO: pagination
        int pageNumber = 0;
        int pageSize = 10000;
        Page<BenchmarkValue> entities = this.benchmarkValueRepository.getValuesFromDate(fromDate, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));

        double[] values = new double[(int) entities.getTotalElements()];
        for(int i = 0; i < entities.getContent().size(); i++){
            values[i] = entities.getContent().get(i).getReturnValue();
        }
        return values;
    }

    @Override
    public double[] getReturnValuesBetweenDatesAsArray(Date dateFrom, Date dateTo, String benchmarkCode) {
        // TODO: pagination
        int pageNumber = 0;
        int pageSize = 10000;
        Page<BenchmarkValue> entities = this.benchmarkValueRepository.getValuesBetweenDates(dateFrom, dateTo, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));

        double[] values = new double[(int) entities.getTotalElements()];
        for(int i = 0; i < entities.getContent().size(); i++){
            values[i] = entities.getContent().get(i).getReturnValue();
        }
        return values;
    }

    @Override
    public BenchmarkPagedSearchResult search(BenchmarkSearchParams searchParams) {

//        Iterator<BenchmarkValue> iterator = this.benchmarkValueRepository.findAll().iterator();
//        while(iterator.hasNext()){
//            BenchmarkValue entity = iterator.next();
//
//            this.benchmarkValueRepository.delete(entity.getId());
//            entity.setId(null);
//            entity.setCreator(new Employee(35L));
//            entity.setCreationDate(new Date());
//            this.benchmarkValueRepository.save(entity);
//        }

        if(searchParams == null){
            searchParams = new BenchmarkSearchParams();
            searchParams.setPage(0);
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }else if(searchParams.getPageSize() == 0){
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }

        BenchmarkPagedSearchResult result = new BenchmarkPagedSearchResult();
        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
        Page<BenchmarkValue> entityPage = this.benchmarkValueRepository.getValuesBetweenDates(searchParams.getFromDate(), searchParams.getToDate(), searchParams.getBenchmarkCode(),
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
                result.setBenchmarks(this.benchmarkValueEntityConverter.disassembleList(entityPage.getContent()));
            }
        }

        return result;
    }

    @Override
    public EntitySaveResponseDto save(BenchmarkValueDto dto, String username) {
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        try {
            if (dto != null) {
                if(dto.getDate() != null && new Date().compareTo(dto.getDate()) < 0){
                    String errorMessage = "Error saving benchmark for date " + DateUtils.getDateFormatted(dto.getDate()) +
                            ". Date cannot be greater than current date.";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }

                BenchmarkValue existingEntity = null;
                if(dto.getId() != null) {
                    // Check existing date
                    BenchmarkValue existingBenchmarkValue =
                            this.benchmarkValueRepository.getValuesForDateAndType(dto.getDate(), dto.getBenchmark().getCode());
                    if (existingBenchmarkValue != null && existingBenchmarkValue.getId().longValue() != dto.getId().longValue()) {
                        String errorMessage = "Benchmark record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }else if(existingBenchmarkValue != null && existingBenchmarkValue.getId().longValue() == dto.getId().longValue()){
                        existingEntity = existingBenchmarkValue;
                    }

                }else {// New record
                    // Check existing date
                    BenchmarkValue existingBenchmarks =
                            this.benchmarkValueRepository.getValuesForDateAndType(dto.getDate(), dto.getBenchmark().getCode());

                    if (existingBenchmarks != null) {
                        String errorMessage = "Benchmark record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }
                }

                BenchmarkValue entity = this.benchmarkValueEntityConverter.assemble(dto);
                EmployeeDto employeeDto = this.employeeService.findByUsername(username);
                if(dto.getId() == null){
                    // new instance
                    if(employeeDto != null){
                        entity.setCreator(new Employee(employeeDto.getId()));
                        entity.setCreationDate(new Date());
                    }
                }else{
                    //update
                    entity.setUpdateDate(new Date());
                    entity.setUpdater(new Employee(employeeDto.getId()));

                    if(existingEntity != null) {
                        entity.setCreator(existingEntity.getCreator());
                        entity.setCreationDate(existingEntity.getCreationDate());
                    }
                }
                this.benchmarkValueRepository.save(entity);
                saveResponse.setSuccessMessageEn("Successfully saved.");
                logger.info("Successfully saved benchmark value: id=" + dto.getId() + ", date=" + DateUtils.getDateFormatted(dto.getDate()) +
                        ", [user=" + username + "]");
            }
            return saveResponse;
        }catch (Exception ex){
            logger.error("Error saving benchmark", ex);
            saveResponse.setErrorMessageEn("Error saving benchmark");
            return saveResponse;
        }
    }

    @Override
    public EntityListSaveResponseDto save(List<BenchmarkValueDto> dtoList, String username) {
        EntityListSaveResponseDto saveListResponse = new EntityListSaveResponseDto();
        saveListResponse.setRecords(new ArrayList());
        for(BenchmarkValueDto dto: dtoList){
            EntitySaveResponseDto saveResponseDto = save(dto, username);
            saveListResponse.getRecords().add(dto);
            if(saveResponseDto.getStatus() != null && saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
                // Error
                saveListResponse.setErrorMessageEn(saveResponseDto.getMessage().getNameEn());
                saveListResponse.setStatus(ResponseStatusType.FAIL);
                return saveListResponse;
            }
        }
        saveListResponse.setSuccessMessageEn("Successfully saved benchmark rate list");
        return saveListResponse;
    }

    @Override
    public EntityListSaveResponseDto getBenchmarksBB(BenchmarkSearchParams searchParams, String username) {
        EntityListSaveResponseDto responseDto = new EntityListSaveResponseDto();
        try {
            ListResponseDto listResponseDto = loadBenchmarksBB(searchParams);
            if(listResponseDto.isStatusOK()) {
                List<BenchmarkValueDto> dtoList = listResponseDto.getRecords();
                return save(dtoList, username);
            }else{
                responseDto.setErrorMessageEn(listResponseDto.getErrorMessageEn());
                return responseDto;
            }
        } catch (ResourceAccessException ex){
            logger.error("Connection error: connect timed out. Most likely given Bloomberg terminal is not available at the moment");
            responseDto.appendErrorMessageEn("Connection error: connect timed out. Most likely given Bloomberg terminal is not available at the moment");
        }catch (ConnectException connectException) {
            logger.error("Connection error: connect timed out. Most likely given Bloomberg terminal is not available at the moment");
            responseDto.appendErrorMessageEn("Connection error: connect timed out. Most likely given Bloomberg terminal is not available at the moment");
        } catch (Exception e) {
            logger.error("Error parsing Benchmark from Bloomberg with exception", e);
            responseDto.appendErrorMessageEn("Error parsing Benchmark from Bloomberg");
        }
        return responseDto;
    }

    private String getUrlStatic(String stationCode) throws UnknownHostException {
        String base_url = "";
        switch (stationCode) {
            case "HF":
                base_url = "BloombergHF-778";
                break;
            case "RISK":
                base_url = "BloombergRISK-790";
                break;
            case "RA":
                base_url = "BloombergYAO-788";
                break;
            default:
                base_url = "BloombergYAO-788";
        }
        return "http://" + base_url + ":8080/bloomberg/benchmark";
    }

    private ListResponseDto loadBenchmarksBB(BenchmarkSearchParams searchParams) throws ConnectException, Exception {
        ListResponseDto responseDto = new ListResponseDto();
        Date nextMonth = DateUtils.getLastDayOfNextMonth(searchParams.getToDate());
        if (nextMonth.before(new Date())) {
            searchParams.setToDate(nextMonth);
        }
        List<BenchmarkValueDto> benchmarks = new ArrayList<>();

        String url = getUrlStatic(searchParams.getStationCode());
        logger.info(url);
        ResponseEntity<String> responseEntity = (new RestTemplate()).postForEntity(url, searchParams, String.class);
        String response = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        ResponseDto result = mapper.readValue(response, new TypeReference<ResponseDto>() {});

        SecurityDataDto securityData = new SecurityDataDto();
        String benchmarkCode = renameCode(searchParams.getBenchmarkCode()) + " " + "Index";
        if(benchmarkCode.equalsIgnoreCase("")){
            responseDto.setErrorMessageEn("Error loading becnhmarks: Bloomberg benchmark code missing for '" + searchParams.getBenchmarkCode() + "'");
            return responseDto;
        }
        for (int i = 0; i < result.getSecurityDataDtoList().size(); i++) {
            if (result.getSecurityDataDtoList().get(i).getSecurity().equals(benchmarkCode)){
                securityData = result.getSecurityDataDtoList().get(i);
            }
        }
        if (securityData == null || securityData.getFieldDataDtoList().isEmpty()) {
            responseDto.setStatus(ResponseStatusType.SUCCESS);
            responseDto.setRecords(new ArrayList());
            return responseDto;
        }
        if (securityData != null || !securityData.getFieldDataDtoList().isEmpty()) {
            List<FieldDataDto> fieldDataDtoList = new ArrayList<>(securityData.getFieldDataDtoList());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < fieldDataDtoList.size(); i++) {
                Date date = formatter.parse(fieldDataDtoList.get(i).getDate());
                BenchmarkValueDto benchmark = new BenchmarkValueDto();
                benchmark.setBenchmark(new BaseDictionaryDto(searchParams.getBenchmarkCode(), null, null, null));
                benchmark.setDate(date);

                if (i + 1 < fieldDataDtoList.size()) {
                    Double startIndex = Double.valueOf(fieldDataDtoList.get(i).getValue());
                    Double endIndex = Double.valueOf(fieldDataDtoList.get(i+1).getValue());
                    benchmark.setReturnValue(getRateOfReturn(startIndex, endIndex));
                } else {
                    benchmark.setReturnValue(null);
                }
                benchmarks.add(benchmark);
            }
        }
        responseDto.setStatus(ResponseStatusType.SUCCESS);
        responseDto.setRecords(benchmarks);
        return responseDto;
    }

    private Double getRateOfReturn(Double startIndex, Double endIndex) {
        Double diff = endIndex - startIndex;
        return diff/startIndex;
    }

    private String renameCode(String benchmarkCode) {
        // TODO: Add BenchmarkLoookup --> Bloomberg Code
        switch (benchmarkCode) {
            case "HFRI_FOF":
                return "HFRIFOF";
            case "HFRI_AWC":
                return "HFRIAWC";
            case "MSCI_WRLD":
                return "MXWO";
            case "MSCIACWIIM":
                return "MXWDIM";
            case "MSCI_EM":
                return "MXEF";
            case "US_HIGHYLD":
                return "H0A0";
            case "SP500_SPX":
                return "SPX";
            case "GLOBAL_FI":
                return "LEGATRUH";
            case "LEGATRUU":
                return "LEGATRUU";
            default:
                return "";
        }

    }

    @Override
    public List<BenchmarkValueDto> getBenchmarkValuesForDatesAndType(Date dateFrom, Date dateTo, String benchmarkCode){

        List<BenchmarkValueDto> benchmarks = new ArrayList<>();
        int pageNumber = 0;
        int pageSize = 10000;
        Page<BenchmarkValue> entitiesPage = this.benchmarkValueRepository.getValuesBetweenDates(dateFrom, dateTo, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "date", "id")));

        if(entitiesPage != null){
            for(BenchmarkValue entity: entitiesPage.getContent()){
                benchmarks.add(this.benchmarkValueEntityConverter.disassemble(entity));
            }
        }
        return benchmarks;
    }

    /**
     * Returns list of benchmark values (dtos) that satisfy the specified parameters.
     * Monthly (end of the month) benchmark values within the specified date period.
     * For each date in date period looks for most recent date within specified days range,since
     * benchmark values can be set not for the last day of the month due to weekends or holidays.
     * Looks for specified benchmarks (arrays of benchmark codes).
     * @param dateFrom - start date
     * @param dateTo - end date
     * @param daysRange - moving each date by days
     * @param benchmarkCodes - array of benchmark codes
     * @return - list of benchmark values
     */
    @Override
    public List<BenchmarkValueDto> getBenchmarkValuesEndOfMonthForDateRangeAndTypes(Date dateFrom, Date dateTo, int daysRange, String[] benchmarkCodes) {
        List<BenchmarkValueDto> benchmarkValues = new ArrayList<>();
        for(String benchmark: benchmarkCodes){
            Date date = DateUtils.getLastDayOfCurrentMonth(dateFrom);
            while(date.before(dateTo) || date.compareTo(dateTo) == 0) {
                Date date1 = DateUtils.getLastDayOfCurrentMonth(date);
                Date date2 = DateUtils.moveDateByDays(date1, 0 - daysRange,false);
                List<BenchmarkValueDto> matchingValues = getBenchmarkValuesForDatesAndType(date2, date1, benchmark);
                if(matchingValues != null && !matchingValues.isEmpty()) {
                    Collections.sort(matchingValues);
                    Collections.reverse(matchingValues);
                    benchmarkValues.add(matchingValues.get(0));
                }
                date = DateUtils.getLastDayOfCurrentMonth(DateUtils.moveDateByMonths(date, 1));
            }
        }
        return benchmarkValues;
    }

}
