package kz.nicnbk.service.impl.hr;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.hr.HRNewsRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.hr.HRNews;
import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.news.News;
import kz.nicnbk.service.api.hr.HRNewsService;
import kz.nicnbk.service.converter.hr.HRNewsEntityConverter;
import kz.nicnbk.service.dto.hr.HRNewsDto;
import kz.nicnbk.service.dto.news.NewsDto;
import org.jsoup.Jsoup;
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
public class HRNewsServiceImpl implements HRNewsService {

    private static final Logger logger = LoggerFactory.getLogger(HRNewsServiceImpl.class);

    /* Max number of characters in short version of news content (excerpt) */


    @Autowired
    private HRNewsEntityConverter hrNewsConverter;

    @Autowired
    private HRNewsRepository hrNewsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(HRNewsDto newsItemDto, String updater) {

        try {
            HRNews entity = hrNewsConverter.assemble(newsItemDto);
            if(newsItemDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(updater);
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.hrNewsRepository.findOne(newsItemDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = hrNewsRepository.findOne(newsItemDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                 entity.setUpdater(updatedby);
            }
            Long id = hrNewsRepository.save(entity).getId();
            logger.info(newsItemDto.getId() == null ? "HR News created: " + id + ", " + entity.getCreator().getUsername() :
                    "HR News updated: " + id + ", " + entity.getUpdater().getUsername());
            return id;
        }catch(Exception ex){
            logger.error("Error saving hr news.", ex);
        }
        return null;
    }

    @Override
    public HRNewsDto get(Long id) {
        if(id != null){
            try {
                HRNews entity = hrNewsRepository.findOne(id);
                if (entity != null) {
                    return hrNewsConverter.disassemble(entity);
                }
            }catch (Exception ex){
                logger.error("Error loading hr news by id: " + id, ex);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Long id, String username) {
        if(id == null){
            logger.info("HR News delete not preformed. ID is null");
            return false;
        }
        try {
            hrNewsRepository.delete(id);
            // TODO: by who
            logger.info("HR News deleted: " + id + ", by " + username);
        }catch(Exception ex){
            logger.error("HR News delete failed: " + id, ex);
        }
        return true;
    }


    @Override
    public List<HRNewsDto> loadNewsShort(int page, int pageSize){
        List<HRNewsDto> newsItems = null;
        try {
            Page<HRNews> entityPage = hrNewsRepository.findAll(new PageRequest(page, pageSize,
                    new Sort(Sort.Direction.DESC, "creationDate", "id")));

            if(entityPage != null && entityPage.getContent() != null){
                newsItems = new ArrayList<>();
                for(HRNews entity: entityPage.getContent()){
                    newsItems.add(hrNewsConverter.disassemble(entity));
                }
            }
        }catch(Exception ex){
            logger.error("Error loading hr news list: page=" + page + ", pagesize=" + pageSize, ex);
        }
        return newsItems;
    }


}