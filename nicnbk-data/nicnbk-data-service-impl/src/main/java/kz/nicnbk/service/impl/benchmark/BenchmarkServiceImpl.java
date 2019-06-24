package kz.nicnbk.service.impl.benchmark;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.benchmark.BenchmarkValueRepository;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.benchmark.BenchmarkService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.benchmark.BenchmarkValueEntityConverter;
import kz.nicnbk.service.dto.benchmark.BenchmarkValueDto;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

                // Check if can edit
                // TODO: check usage in scoring
                // TODO: if saved scoring exists, editing not allowed


                if(dto.getId() != null) {

                    // Check existing date
                    BenchmarkValue existingCurrencyRates =
                            this.benchmarkValueRepository.getValuesForDateAndType(dto.getDate(), dto.getBenchmark().getCode());
                    if (existingCurrencyRates != null && existingCurrencyRates.getId().longValue() != dto.getId().longValue()) {
                        String errorMessage = "Benchmark record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }

                }else {// New record
                    // Check existing date
                    BenchmarkValue existingBenchmarks =
                            this.benchmarkValueRepository.getValuesForDateAndType(dto.getDate(), dto.getBenchmark().getCode());

                    // TODO: after check if can edit is implemented, instead of returning error --> delete record and insert new
                    // TODO:
//                    if(existingBenchmarks.getId() != null) {
//                        this.benchmarkValueRepository.delete(existingBenchmarks.getId());
//                    }
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
                    }
                }else{
                    //update
                    entity.setUpdateDate(new Date());
                    entity.setUpdater(new Employee(employeeDto.getId()));
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
        for(BenchmarkValueDto dto: dtoList){
            EntitySaveResponseDto saveResponseDto = save(dto, username);
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

}
