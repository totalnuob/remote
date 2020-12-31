package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.risk.PortfolioVarValueRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.risk.PortfolioVarValue;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFPortfolioRiskService;
import kz.nicnbk.service.converter.risk.PortfolioVarValueEntityConverter;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.lookup.PortfolioVarPagedSearchResult;
import kz.nicnbk.service.dto.lookup.PortfolioVarSearchParams;
import kz.nicnbk.service.dto.risk.PortfolioVarValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HFPortfolioRiskServiceImpl implements HFPortfolioRiskService {
    int DEFAULT_PAGES_PER_VIEW = 5;

    /* Number of elements per page */
    int DEFAULT_PAGE_SIZE = 20;

    private static final Logger logger = LoggerFactory.getLogger(HFPortfolioRiskServiceImpl.class);

    @Autowired
    private PortfolioVarValueRepository portfolioVarValueRepository;

    @Autowired
    private PortfolioVarValueEntityConverter portfolioVarValueEntityConverter;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public List<PortfolioVarValueDto> getValuesFromDateAsList(Date fromDate, String benchmarkCode) {

        int pageNumber = 0;
        int pageSize = Integer.MAX_VALUE;
        Page<PortfolioVarValue> entites = this.portfolioVarValueRepository.getValuesFromDate(fromDate, benchmarkCode,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "date", "id")));

        List<PortfolioVarValueDto> dtoList = this.portfolioVarValueEntityConverter.disassembleList(entites.getContent());
        return dtoList;
    }

    @Override
    public PortfolioVarPagedSearchResult search(PortfolioVarSearchParams searchParams) {

        if(searchParams == null){
            searchParams = new PortfolioVarSearchParams();
            searchParams.setPage(0);
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }else if(searchParams.getPageSize() == 0){
            searchParams.setPageSize(DEFAULT_PAGE_SIZE);
        }

        PortfolioVarPagedSearchResult result = new PortfolioVarPagedSearchResult();
        int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
        Page<PortfolioVarValue> entityPage = this.portfolioVarValueRepository.getValuesBetweenDates(searchParams.getFromDate(), searchParams.getToDate(), searchParams.getPortfolioVarCode(),
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
                result.setPortfolioVars(this.portfolioVarValueEntityConverter.disassembleList(entityPage.getContent()));
            }
        }

        return result;
    }

    @Override
    public EntitySaveResponseDto save(PortfolioVarValueDto dto, String username) {
        EntitySaveResponseDto saveResponse = new EntitySaveResponseDto();
        try {
            if (dto != null) {
                if(dto.getDate() != null && new Date().compareTo(dto.getDate()) < 0){
                    String errorMessage = "Error saving portfolio VaR for date " + DateUtils.getDateFormatted(dto.getDate()) +
                            ". Date cannot be greater than current date.";
                    logger.error(errorMessage);
                    saveResponse.setErrorMessageEn(errorMessage);
                    return saveResponse;
                }

                PortfolioVarValue existingEntity = null;
                if(dto.getId() != null) {
                    // Check existing date
                    PortfolioVarValue existingPortfolioVarValue =
                            this.portfolioVarValueRepository.getValuesForDateAndType(dto.getDate(), dto.getPortfolioVar().getCode());
                    if (existingPortfolioVarValue != null && existingPortfolioVarValue.getId().longValue() != dto.getId().longValue()) {
                        String errorMessage = "Portfolio VaR record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }else if(existingPortfolioVarValue != null && existingPortfolioVarValue.getId().longValue() == dto.getId().longValue()){
                        existingEntity = existingPortfolioVarValue;
                    }

                }else {// New record
                    // Check existing date
                    PortfolioVarValue existingPortfolioVars =
                            this.portfolioVarValueRepository.getValuesForDateAndType(dto.getDate(), dto.getPortfolioVar().getCode());

                    if (existingPortfolioVars != null) {
                        String errorMessage = "Portfolio VaR record save failed: record already exists for date " + DateUtils.getDateFormatted(dto.getDate());
                        logger.error(errorMessage);
                        saveResponse.setErrorMessageEn(errorMessage);
                        return saveResponse;
                    }
                }

                PortfolioVarValue entity = this.portfolioVarValueEntityConverter.assemble(dto);
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
                this.portfolioVarValueRepository.save(entity);
                saveResponse.setSuccessMessageEn("Successfully saved.");
                logger.info("Successfully saved portfolio var value: id=" + dto.getId() + ", date=" + DateUtils.getDateFormatted(dto.getDate()) +
                        ", [user=" + username + "]");
            }
            return saveResponse;
        }catch (Exception ex){
            logger.error("Error saving portfolio VaR", ex);
            saveResponse.setErrorMessageEn("Error saving portfolio VaR");
            return saveResponse;
        }
    }

    @Override
    public PortfolioVarValueDto getPortfolioVarEndOfMonthForDate(Date date, String portfolioVarCode) {
        PortfolioVarValueDto result = new PortfolioVarValueDto();
        PortfolioVarValue portfolioVarValue;
        Date endOfMonthDate = DateUtils.getLastDayOfCurrentMonth(date);
        portfolioVarValue = portfolioVarValueRepository.getValuesForDateAndType(endOfMonthDate, portfolioVarCode);
        if (portfolioVarValue != null) {
             result = portfolioVarValueEntityConverter.disassemble(portfolioVarValue);
        }
        return result;
    }
}
