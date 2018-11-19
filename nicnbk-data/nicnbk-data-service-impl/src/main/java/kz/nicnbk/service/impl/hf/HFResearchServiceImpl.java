package kz.nicnbk.service.impl.hf;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.api.hf.HFResearchPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kz.nicnbk.repo.api.hf.HFResearchRepository;
import kz.nicnbk.repo.model.hf.HFResearch;
import kz.nicnbk.service.api.hf.HFResearchService;
import kz.nicnbk.service.converter.hf.HFResearchEntityConverter;
import kz.nicnbk.service.dto.hf.HFResearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by zhambyl on 06/11/2018.
 */
@Service
public class HFResearchServiceImpl implements HFResearchService {

    private static final Logger logger = LoggerFactory.getLogger(HFResearchServiceImpl.class);

    @Autowired
    private HFResearchRepository repository;

    @Autowired
    private HFResearchEntityConverter converter;

    @Autowired
    private HFResearchPageService hfResearchPageService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private HFManagerService hfManagerService;

    @Override
    public Long save(HFResearchDto researchDto, String updater) {
        try {
            HFResearch entity = converter.assemble(researchDto);

            if(researchDto.getId() == null || researchDto.getId() == 0 ){ //CREATE
                Employee employee = this.employeeRepository.findByUsername(researchDto.getOwner());
                entity.setCreator(employee);
            } else {
                // set creator
                Employee employee = this.repository.findOne(researchDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = repository.findOne(researchDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }

            Long id = repository.save(entity).getId();
            logger.info(researchDto.getId() == null || researchDto.getId() == 0 ? "HF research created: " + id + ", by " + entity.getCreator().getUsername() :
                    "HF research updated: " + id + ", by " + updater);

            return id;
        }catch (Exception ex){
            logger.error("Error saving HF research form: " + (researchDto != null && researchDto.getId() != null ? researchDto.getId() : "new") ,ex);
        }
        return null;
    }

    @Override
    public HFResearchDto get(Long id) {
        try {
            HFResearch entity = this.repository.findByManagerId(id);
            if(entity != null) {
                HFResearchDto researchDto = this.converter.disassemble(entity);
                researchDto.setResearchPages(this.hfResearchPageService.loadResearch(id));
                researchDto.setManager(this.hfManagerService.get(id));
                return researchDto;
            }
            HFResearchDto researchDto = new HFResearchDto();
            researchDto.setId(0L);
            return researchDto;
        }catch(Exception ex){
            logger.error("Failed to load HF research page:" + id, ex);
        }
        return null;
    }
}
