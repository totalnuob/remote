package kz.nicnbk.service.impl.benchmark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.benchmark.BenchmarkValueRepository;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.BenchmarkLookup;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ConnectException;
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

    private void setCalculatedMonthReturn(List<BenchmarkValueDto> dtoList){
        if(dtoList != null && !dtoList.isEmpty()){
            Date prevMonthDate = null;
            Double prevMonthIndexValue = null;
            for(int i = dtoList.size() - 1; i >= 0; i--){
                BenchmarkValueDto dto = dtoList.get(i);
                if(DateUtils.getDateOnly(DateUtils.getLastDayOfCurrentMonth(dto.getDate())).compareTo(DateUtils.getDateOnly(dto.getDate())) != 0){
                    // not last day of month
                    continue;
                }
                if(dto.getIndexValue() != null && prevMonthIndexValue != null && prevMonthDate != null){
                    // Check two end of month dates
                    if(DateUtils.getMonthsDifference(prevMonthDate, DateUtils.getDateOnly(dto.getDate())) == 1){
                        dto.setCalculatedMonthReturn(MathUtils.divide(10, MathUtils.subtract(10, dto.getIndexValue(), prevMonthIndexValue), prevMonthIndexValue));

                        prevMonthIndexValue = dto.getIndexValue();
                        prevMonthDate = DateUtils.getDateOnly(dto.getDate());
                    }else{
                        prevMonthIndexValue = null;
                        prevMonthDate = null;
                    }
                }
                if(prevMonthIndexValue == null) {
                    prevMonthIndexValue = dto.getIndexValue();
                    prevMonthDate = DateUtils.getDateOnly(dto.getDate());
                }
            }
        }
    }

//    @Override
//    public List<BenchmarkValueDto> getValuesFromDateAsList(Date fromDate, String benchmarkCode) {
//
//        // TODO: pagination
//        int pageNumber = 0;
//        int pageSize = Integer.MAX_VALUE;
//        Date prevDayFromDate = DateUtils.moveDateByDays(fromDate, -1, false);
//        Page<BenchmarkValue> entities = this.benchmarkValueRepository.getValuesFromDate(prevDayFromDate, benchmarkCode,
//                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));
//
//        List<BenchmarkValueDto> dtoList = this.benchmarkValueEntityConverter.disassembleList(entities.getContent());
//        setCalculatedMonthReturn(dtoList);
//        return dtoList;
//    }

//    @Override
//    public double[] getMonthReturnValuesFromDateAsArray(Date fromDate, String benchmarkCode) {
//        // TODO: pagination
//        int pageNumber = 0;
//        int pageSize = 10000;
//        Date prevDayFromDate = DateUtils.moveDateByDays(fromDate, -1, false);
//        Page<BenchmarkValue> entities = this.benchmarkValueRepository.getValuesFromDate(prevDayFromDate, benchmarkCode,
//                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));
//
//        List<BenchmarkValueDto> dtoList = this.benchmarkValueEntityConverter.disassembleList(entities.getContent());
//        setCalculatedMonthReturn(dtoList);
//
//        double[] values = new double[(int) dtoList.size()];
//        for(int i = 0; i < dtoList.size(); i++){
//            values[i] = dtoList.get(i).getCalculatedMonthReturn();
//        }
//        return values;
//    }

    @Override
    public double[] getMonthReturnValuesBetweenDatesAsArray(Date dateFrom, Date dateTo, String benchmarkCode) {
        // TODO: pagination
        int pageNumber = 0;
        int pageSize = 10000;
        Date prevMonthFromDate = DateUtils.moveDateByMonths(dateFrom, -1);
        Page<BenchmarkValue> entities = this.benchmarkValueRepository.getValuesBetweenDates(prevMonthFromDate, dateTo, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));
        List<BenchmarkValueDto> dtoList = this.benchmarkValueEntityConverter.disassembleList(entities.getContent());

        setCalculatedMonthReturn(dtoList); //calculate return from index values
        List<BenchmarkValueDto> nonemptyDtoList = new ArrayList<>();
        // non empty calculated value within dates
        if(dtoList != null && !dtoList.isEmpty()){
            for(BenchmarkValueDto dto: dtoList){
                boolean datesOk = (dto.getDate().after(dateFrom) || DateUtils.isSameDate(dto.getDate(), dateFrom)) &&
                        (dto.getDate().before(dateTo) || DateUtils.isSameDate(dto.getDate(), dateTo));
                if(dto.getCalculatedMonthReturn() != null && datesOk){
                    nonemptyDtoList.add(dto);
                }
            }
        }

        double[] values = new double[(int) nonemptyDtoList.size()];
        for(int i = 0; i < nonemptyDtoList.size(); i++){
            values[i] = nonemptyDtoList.get(i).getCalculatedMonthReturn();
        }
        return values;
    }

    @Override
    public BenchmarkPagedSearchResult search(BenchmarkSearchParams searchParams) {
        if(searchParams == null){
            searchParams = new BenchmarkSearchParams();
            searchParams.setPage(0);
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }else if(searchParams.getPageSize() == 0){
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }

        BenchmarkPagedSearchResult result = new BenchmarkPagedSearchResult();
        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;

        if(searchParams.getFromDate() == null){
            searchParams.setFromDate(DateUtils.DEFAULT_START);
        }
        if(searchParams.getToDate() == null){
            searchParams.setToDate(new Date());
        }
        // add 1 month before to calculate Return, and add 1 to page size
        Date prevMonthFromDate = DateUtils.moveDateByDays(searchParams.getFromDate(), -1, false);
        Page<BenchmarkValue> entityPage = this.benchmarkValueRepository.getValuesBetweenDates(prevMonthFromDate, searchParams.getToDate(), searchParams.getBenchmarkCode(),
                new PageRequest(page, searchParams.getPageSize() + 1, // add 1 to page size
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
                List<BenchmarkValueDto> dtoList =this.benchmarkValueEntityConverter.disassembleList(entityPage.getContent());
                if(dtoList != null && !dtoList.isEmpty()){
                    setCalculatedMonthReturn(dtoList);
                    List<BenchmarkValueDto> withinDatesDtoList = new ArrayList<>();
                    // value within dates
                    int count = 0;
                    for(BenchmarkValueDto dto: dtoList){
                        boolean datesOk = (dto.getDate().after(searchParams.getFromDate()) || DateUtils.isSameDate(dto.getDate(), searchParams.getFromDate())) &&
                                (dto.getDate().before(searchParams.getToDate()) || DateUtils.isSameDate(dto.getDate(), searchParams.getToDate()));
                        if(datesOk){
                            withinDatesDtoList.add(dto);
                            count++;
                        }
                        if(count == searchParams.getPageSize()){ // pageSize + 1 elements loaded for return calculation, remove extra
                            // extra records not added
                            break;
                        }
                    }
                    result.setBenchmarks(withinDatesDtoList);
                }
            }
        }

        //testBenchmarkReturns(result.getBenchmarks());
        return result;
    }

//    private void testBenchmarkReturns(List<BenchmarkValueDto> benchmarks){
//        if(benchmarks != null && !benchmarks.isEmpty()){
//            int misMatch = 0;
//            int matching = 0;
//            int missingReturns = 0;
//            int missingCalculated = 0;
//            int missingIndex = 0;
//            double maxMismatch = 0.0;
//            for(BenchmarkValueDto dto: benchmarks){
//                if(dto.getReturnValue() != null && dto.getCalculatedMonthReturn() != null){
//                    if(dto.getReturnValue().doubleValue() != dto.getCalculatedMonthReturn().doubleValue()){
//                        System.out.println(dto.getBenchmark().getCode() + " " +
//                                DateUtils.getDateFormatted(dto.getDate()) + " : return=" + dto.getReturnValue().doubleValue() +
//                                ", calc=" +  new BigDecimal(dto.getCalculatedMonthReturn().doubleValue()).setScale(10, RoundingMode.HALF_UP).toPlainString() +
//                                ", diff=" + new BigDecimal(MathUtils.subtract(10,dto.getReturnValue().doubleValue(), dto.getCalculatedMonthReturn().doubleValue()))
//                                        .setScale(3, RoundingMode.HALF_UP).toPlainString());
//                        misMatch++;
//                        double diff = MathUtils.subtract(3,dto.getReturnValue().doubleValue(), dto.getCalculatedMonthReturn().doubleValue());
//                        if(Math.abs(diff) > maxMismatch){
//                            maxMismatch = Math.abs(diff);
//                        }
//                    }else{
//                        matching++;
//                    }
//                }else{
//                    if(dto.getReturnValue() == null){
//                        missingReturns++;
//                    }
//                    if(dto.getCalculatedMonthReturn() == null){
//                        missingCalculated++;
//                    }
//                }
//                if(dto.getIndexValue() == null){
//                    missingIndex++;
//                }
//            }
//            System.out.println("Total mismatch: " + misMatch + "; max mismatch=" + maxMismatch);
//            System.out.println("Total matching: " + matching);
//            System.out.println("Total missing return in DB: " + missingReturns);
//            System.out.println("Total missing calculated values: " + missingCalculated);
//            System.out.println("Total missing index: " + missingIndex);
//        }
//    }

    @Override
    public EntitySaveResponseDto save(BenchmarkValueDto dto, String username) {
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        try {
            if (dto != null) {
                if(dto.getDate() == null){
                    String errorMessage = "Error saving benchmark: date required";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
                if(dto.getDate() != null && new Date().compareTo(dto.getDate()) < 0){
                    String errorMessage = "Error saving benchmark for date " + DateUtils.getDateFormatted(dto.getDate()) +
                            ". Date cannot be greater than current date.";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }
                // Check end of month
                if(!DateUtils.isSameDate(DateUtils.getLastDayOfCurrentMonth(dto.getDate()), dto.getDate())){
                    String errorMessage = "Error saving benchmark for date '" + DateUtils.getDateFormatted(dto.getDate()) +
                            "'. Must be end of month";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }

                BenchmarkValue existingEntity = this.benchmarkValueRepository.getValuesForDateAndType(dto.getDate(), dto.getBenchmark().getCode());
                if(dto.getId() != null) {
                    if (existingEntity != null && existingEntity.getId().longValue() != dto.getId().longValue()) {
                        String errorMessage = "Benchmark record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }
                }else {// New record
                    if (existingEntity != null) {
                        // Can update only index value
                        if(existingEntity.getIndexValue() != null){
                            // index value change
                            String errorMessage = "Benchmark record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                            logger.error(errorMessage);
                            saveResponse.setErrorMessageEn(errorMessage);
                            return saveResponse;
                        }
                    }
                }

                if(dto.getId() == null && existingEntity != null){
                    // update existing entity
                    dto.setId(existingEntity.getId());
                }
                BenchmarkValue entity = this.benchmarkValueEntityConverter.assemble(dto);
                if(existingEntity != null){
                    entity.setReturnValue(existingEntity.getReturnValue());
                }else{
                    entity.setReturnValue(null);
                }
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
                saveResponse.setSuccessMessageEn("Successfully " + (existingEntity != null ? "updated." : "saved."));
                logger.info("Successfully saved benchmark value: id=" + entity.getId().longValue() + ", date=" + DateUtils.getDateFormatted(dto.getDate()) +
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
    public EntityListSaveResponseDto save(List<BenchmarkValueDto> dtoList, boolean stopOnError, String username) {
        EntityListSaveResponseDto saveListResponse = new EntityListSaveResponseDto();
        saveListResponse.setRecords(new ArrayList());
        for(BenchmarkValueDto dto: dtoList){
            EntitySaveResponseDto saveResponseDto = save(dto, username);
            saveListResponse.getRecords().add(dto);
            if(saveResponseDto.getStatus() != null && saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
                if(stopOnError) {
                    // Error
                    saveListResponse.setErrorMessageEn(saveResponseDto.getMessage().getNameEn());
                    saveListResponse.setStatus(ResponseStatusType.FAIL);
                    return saveListResponse;
                }else{
                    saveListResponse.appendErrorMessageEn(saveResponseDto.getMessage().getNameEn() + " ");
                    saveListResponse.setStatus(ResponseStatusType.OK_WITH_ERRORS);
                }
            }
        }
        saveListResponse.setSuccessMessageEn("Successfully saved benchmark rate list");
        return saveListResponse;
    }

    @Override
    public EntityListSaveResponseDto downloadBenchmarksBB(BenchmarkSearchParams searchParams, String username) {
        EntityListSaveResponseDto responseDto = new EntityListSaveResponseDto();
        try {
            if(searchParams.getFromDate() == null || searchParams.getToDate() == null){
                logger.error("Benchmark load request failed: both 'From date' and 'To date' required.");
                responseDto.setErrorMessageEn("Benchmark load request failed: both 'From date' and 'To date' required.");
                return responseDto;
            }else if(searchParams.getFromDate() != null && searchParams.getToDate() != null && searchParams.getToDate().before(searchParams.getFromDate())){
                logger.error("Benchmark load request failed: 'From date' must be before 'To date'");
                responseDto.setErrorMessageEn("Benchmark load request failed: 'From date' must be before 'To date'");
                return responseDto;
            }
            if(searchParams.getBenchmarkCode() == null){
                logger.error("Benchmark load request failed: Benchmark code required");
                responseDto.setErrorMessageEn("Benchmark load request failed: Benchmark code required");
                return responseDto;
            }
            if(searchParams.getStationCode() == null){
                logger.error("Benchmark load request failed: Bloomberg station required");
                responseDto.setErrorMessageEn("Benchmark load request failed: Bloomberg station required");
                return responseDto;
            }
            ListResponseDto listResponseDto = loadBenchmarksBB(searchParams);
            if(listResponseDto.isStatusOK()) {
                List<BenchmarkValueDto> dtoList = listResponseDto.getRecords();
                return save(dtoList, false, username);
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

    private String getBloombergStatusUrlStatic(String stationCode) throws UnknownHostException {
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

    private String convertToBloombergCode(String benchmarkCode) {
        switch (benchmarkCode) {
            case "HFRI_FOF":
                return "HFRIFOF Index";
            case "HFRI_AWC":
                return "HFRIAWC Index";
            case "MSCI_WRLD":
                return "MXWO Index";
            case "MSCIACWIIM":
                return "MXWDIM Index";
            case "MSCI_EM":
                return "MXEF Index";
            case "US_HIGHYLD":
                return "H0A0 Index";
            case "SP500_SPX":
                return "SPX Index";
            case "SP500_SPTR":
                return "SPTR Index";
            case "GLOBAL_FI":
                return "LEGATRUH Index";
            case "T_BILLS":
                return "G0O1 Index";
            default:
                return null;
        }

    }

    private String convertToUNIICCode(String benchmarkCode) {
        switch (benchmarkCode) {
            case "HFRIFOF Index":
                return BenchmarkLookup.HFRIFOF.getCode();
            case "HFRIAWC Index":
                return BenchmarkLookup.HFRIAWC.getCode();
            case "MXWO Index":
                return BenchmarkLookup.MSCI_WORLD.getCode();
            case "MXWDIM Index":
                return BenchmarkLookup.MSCI_ACWI_IMI.getCode();
            case "MXEF Index":
                return BenchmarkLookup.MSCI_EM.getCode();
            case "H0A0 Index":
                return BenchmarkLookup.US_HIGH_YIELDS.getCode();
            case "SPX Index":
                return BenchmarkLookup.SNP_500_SPX.getCode();
            case "LEGATRUH Index":
                return BenchmarkLookup.GLOBAL_FI.getCode();
            case "G0O1 Index":
                return BenchmarkLookup.T_BILLS.getCode();
            case "SPTR Index":
                return BenchmarkLookup.SNP_500_SPTR.getCode();
            default:
                return null;
        }

    }

    private ListResponseDto loadBenchmarksBB(BenchmarkSearchParams searchParams) throws ConnectException, Exception {
        ListResponseDto responseDto = new ListResponseDto();
        List<BenchmarkValueDto> benchmarks = new ArrayList<>();

        String url = getBloombergStatusUrlStatic(searchParams.getStationCode());
        logger.info(url);

        String bloombergBenchmarkCode = convertToBloombergCode(searchParams.getBenchmarkCode());
        if(bloombergBenchmarkCode == null){
            responseDto.setErrorMessageEn("Failed to load data from Bloomberg: benchmark code could not be converted to bloomberg format - '" + searchParams.getBenchmarkCode() + "'");
            return responseDto;
        }else{
            searchParams.setBenchmarkCode(bloombergBenchmarkCode);
        }
        ResponseEntity<String> responseEntity = (new RestTemplate()).postForEntity(url, searchParams, String.class);
        String response = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        ResponseDto result = mapper.readValue(response, new TypeReference<ResponseDto>() {});

        SecurityDataDto securityData = new SecurityDataDto();
        for (int i = 0; i < result.getSecurityDataDtoList().size(); i++) {
            if (result.getSecurityDataDtoList().get(i).getSecurity().equals(searchParams.getBenchmarkCode())){
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
            String unicBenchmarkCode = convertToUNIICCode(searchParams.getBenchmarkCode());
            if(unicBenchmarkCode == null){
                responseDto.setErrorMessageEn("Failed to save data from Bloomberg: benchmark code could not be " +
                        "converted from bloomberg format - '" + searchParams.getBenchmarkCode() + "'");
                return responseDto;
            }
            for (int i = 0; i < fieldDataDtoList.size(); i++) {
                Date date = formatter.parse(fieldDataDtoList.get(i).getDate());
                BenchmarkValueDto benchmark = new BenchmarkValueDto();
                benchmark.setBenchmark(new BaseDictionaryDto(unicBenchmarkCode, null, null, null));
                if(DateUtils.getLastDayOfCurrentMonth(date).after(new Date())){
                    continue;
                }
                benchmark.setDate(DateUtils.getLastDayOfCurrentMonth(date));
                benchmark.setIndexValue( Double.valueOf(fieldDataDtoList.get(i).getValue()));
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
                return null;
        }

    }

    @Override
    public List<BenchmarkValueDto> getBenchmarkValuesForDatesAndType(Date dateFrom, Date dateTo, String benchmarkCode){

        List<BenchmarkValueDto> benchmarks = new ArrayList<>();
        int pageNumber = 0;
        int pageSize = 10000;
        Date prevMonthFromDate = DateUtils.moveDateByMonths(dateFrom, -1);
        Page<BenchmarkValue> entitiesPage = this.benchmarkValueRepository.getValuesBetweenDates(prevMonthFromDate, dateTo, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "date", "id")));

        if(entitiesPage != null){
            benchmarks = this.benchmarkValueEntityConverter.disassembleList(entitiesPage.getContent());
            setCalculatedMonthReturn(benchmarks);
            List<BenchmarkValueDto> withinDatesDtoList = new ArrayList<>();
            // calculated value within dates
            if(benchmarks != null && !benchmarks.isEmpty()){
                for(BenchmarkValueDto dto: benchmarks){
                    boolean datesOk = (dto.getDate().after(dateFrom) || DateUtils.isSameDate(dto.getDate(), dateFrom)) &&
                            (dto.getDate().before(dateTo) || DateUtils.isSameDate(dto.getDate(), dateTo));
                    if(datesOk){
                        withinDatesDtoList.add(dto);
                    }
                }
                benchmarks = withinDatesDtoList;
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
