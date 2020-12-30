package kz.nicnbk.service.impl.risk;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.risk.RiskStressTestsRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.risk.PortfolioVarValue;
import kz.nicnbk.repo.model.risk.RiskStressTests;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.risk.RiskStressTestsService;
import kz.nicnbk.service.converter.risk.RiskStressTestEntityConverter;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.lookup.PortfolioVarPagedSearchResult;
import kz.nicnbk.service.dto.lookup.PortfolioVarSearchParams;
import kz.nicnbk.service.dto.lookup.RiskStressTestPagedSearchParams;
import kz.nicnbk.service.dto.lookup.RiskStressTestPagedSearchResult;
import kz.nicnbk.service.dto.monitoring.RiskStressTestsDto;
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

@Service
public class RiskStressTestsServiceImpl implements RiskStressTestsService {

    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    private static final Logger logger = LoggerFactory.getLogger(RiskStressTestsServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RiskStressTestsRepository stressTestsRepository;

    @Autowired
    private RiskStressTestEntityConverter riskStressTestEntityConverter;

    @Override
    public List<RiskStressTestsDto> getStressTestsByDate(Date date) {
        List<RiskStressTestsDto> stressTests = new ArrayList<>();
        List< RiskStressTests> entities = this.stressTestsRepository.findByDate(date);
        if(entities != null && !entities.isEmpty()){
            for(RiskStressTests entity: entities){
                RiskStressTestsDto dto = new RiskStressTestsDto(entity.getDate(), entity.getName(), entity.getValue());
                stressTests.add(dto);
            }
        }
        return stressTests;
    }

    @Override
    public List<RiskStressTestsDto> getAllStressTests() {
        List<RiskStressTestsDto> stressTests = new ArrayList<>();
        List< RiskStressTests> entities = this.stressTestsRepository.findAll();
        if(entities != null && !entities.isEmpty()){
            for(RiskStressTests entity: entities){
                RiskStressTestsDto dto = new RiskStressTestsDto(entity.getDate(), entity.getName(), entity.getValue());
                stressTests.add(dto);
            }
        }
        return stressTests;
    }

    @Override
    public RiskStressTestPagedSearchResult search(RiskStressTestPagedSearchParams searchParams) {
        if(searchParams == null){
            searchParams = new RiskStressTestPagedSearchParams();
            searchParams.setPage(0);
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }else if(searchParams.getPageSize() == 0){
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }

        RiskStressTestPagedSearchResult result = new RiskStressTestPagedSearchResult();
        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
        Page<RiskStressTests> entityPage = this.stressTestsRepository.getValuesBetweenDates(searchParams.getFromDate(), searchParams.getToDate(),
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
                result.setStressTests(this.riskStressTestEntityConverter.disassembleList(entityPage.getContent()));
            }
        }

        return result;
    }

    @Override
    public EntitySaveResponseDto save(RiskStressTestsDto dto, String username) {
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        try {
            if (dto != null) {
                if(dto.getDate() != null && new Date().compareTo(dto.getDate()) < 0){
                    String errorMessage = "Error saving stress test for date " + DateUtils.getDateFormatted(dto.getDate()) +
                            ". Date cannot be greater than current date.";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }

                RiskStressTests existingEntity = null;
                if(dto.getId() != null) {
                    // Check existing date
                    RiskStressTests existingRiskStressTest =
                            this.stressTestsRepository.getRiskStressTestsByDate(dto.getDate());
                    if (existingRiskStressTest != null && existingRiskStressTest.getId().longValue() != dto.getId().longValue()) {
                        String errorMessage = "Stress test record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }else if(existingRiskStressTest != null && existingRiskStressTest.getId().longValue() == dto.getId().longValue()){
                        existingEntity = existingRiskStressTest;
                    }

                }else {// New record
                    // Check existing date
                    RiskStressTests existingRiskStressTests =
                            this.stressTestsRepository.getRiskStressTestsByDate(dto.getDate());

                    if (existingRiskStressTests != null) {
                        String errorMessage = "Stress test record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }
                }

                RiskStressTests entity = this.riskStressTestEntityConverter.assemble(dto);
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
                this.stressTestsRepository.save(entity);
                saveResponse.setSuccessMessageEn("Successfully saved.");
                logger.info("Successfully saved stress testr value: id=" + dto.getId() + ", date=" + DateUtils.getDateFormatted(dto.getDate()) +
                        ", [user=" + username + "]");
            }
            return saveResponse;
        }catch (Exception ex){
            logger.error("Error saving stress test", ex);
            saveResponse.setErrorMessageEn("Error saving stress test");
            return saveResponse;
        }
    }


}
