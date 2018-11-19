package kz.nicnbk.service.impl.hf;

import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.hf.HFManagerRepository;
import kz.nicnbk.repo.api.hf.HFResearchPageRepository;
import kz.nicnbk.repo.api.hf.HFResearchRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.hf.HFResearch;
import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.converter.hf.HFManagerEntityConverter;
import kz.nicnbk.service.dto.hf.*;
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
 * Created by timur on 19.10.2016.
 */
@Service
public class HFManagerServiceImpl implements HFManagerService {

    private static final Logger logger = LoggerFactory.getLogger(HFManagerServiceImpl.class);

    @Autowired
    private HFManagerRepository repository;

    @Autowired
    private HFManagerEntityConverter converter;

    @Autowired
    private HedgeFundService fundService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private HFResearchPageRepository hfResearchPageRepository;

    @Autowired
    private HFResearchRepository hfResearchRepository;

    @Override
    public Long save(HFManagerDto firmDto, String updater) {
        try {
            HFManager entity = converter.assemble(firmDto);

            if(firmDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(firmDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.repository.findOne(firmDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = repository.findOne(firmDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }

            Long id = repository.save(entity).getId();
            logger.info(firmDto.getId() == null ? "HF manager created: " + id + ", by " + entity.getCreator().getUsername() :
                    "HF manager updated: " + id + ", by " + updater);
            return id;
        }catch (Exception ex){
            logger.error("Error saving HF manager: " + (firmDto != null && firmDto.getId() != null ? firmDto.getId() : "new") ,ex);
        }
        return null;
    }

    @Override
    public HFManagerDto get(Long id) {
        try {
            HFManager entity = this.repository.findOne(id);
            HFManagerDto firmDto = this.converter.disassemble(entity);
            // load funds
            firmDto.setFunds(this.fundService.loadManagerFunds(id));
            return firmDto;
        }catch(Exception ex){
            logger.error("Error loading HF manager: " + id, ex);
        }
        return null;
    }

    @Override
    public List<HFManagerDto> findAll(){
        try {
            return converter.disassembleList(repository.findAllByOrderByNameDesc());
        }catch (Exception ex){
            logger.error("Failed to load all HF managers", ex);
        }
        return null;
    }

    @Override
    public List<HFManagerDto> findInvestedInBFunds() {
        try {
            List<HFManager> entityList = this.repository.findInvestedInBFunds();

            if(entityList != null) {
                List<HFManagerDto> result = this.converter.disassembleList(entityList);

                for(HFManagerDto managerDto:result){

                    HFResearch hfResearch = hfResearchRepository.findByManagerId(managerDto.getId());
                    if(hfResearch != null) {
                        managerDto.setInvestmentAmount(hfResearch.getAllocationSize());
                        managerDto.setInvestmentDate(hfResearch.getInvestmentsDates());
                    }
                    Date date = hfResearchPageRepository.findLatestResearch(managerDto.getId());
                    if(date != null) {
                        managerDto.setResearchUpdated(date);
                    }
                }
                return result;
            }
        }catch(Exception ex){
            logger.error("Failed to search HF managers", ex);
        }
        return null;
    }

    @Override
    public HedgeFundManagerPagedSearchResult findByName(HedgeFundSearchParams searchParams) {
        try {
            int page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
            int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
            Page<HFManager> entityPage = this.repository.findByName(searchParams.getName(),
                    //new PageRequest(searchParams.getPage(), searchParams.getPageSize(), new Sort(Sort.Direction.DESC, "id")));
                    new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id")));

            HedgeFundManagerPagedSearchResult result = new HedgeFundManagerPagedSearchResult();
            if (entityPage != null) {
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
                result.setManagers(this.converter.disassembleList(entityPage.getContent()));
            }
            return result;
        }catch (Exception ex){
            logger.error("Failed to search HF managers", ex);
        }
        return null;
    }
}
