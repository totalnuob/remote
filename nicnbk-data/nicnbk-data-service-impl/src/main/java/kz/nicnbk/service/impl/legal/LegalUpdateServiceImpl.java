package kz.nicnbk.service.impl.legal;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.legal.LegalUpdateRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.legal.LegalUpdateNews;
import kz.nicnbk.service.api.legal.LegalUpdateService;
import kz.nicnbk.service.converter.legal.LegalUpdateNewsEntityConverter;
import kz.nicnbk.service.dto.legal.LegalUpdateDto;
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
public class LegalUpdateServiceImpl implements LegalUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(LegalUpdateServiceImpl.class);

    /* Max number of characters in short version of news content (excerpt) */
    private static final int SHORT_NEWS_CONTENT_SIZE = 600;

    @Autowired
    private LegalUpdateNewsEntityConverter entityConverter;

    @Autowired
    private LegalUpdateRepository entityRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(LegalUpdateDto newsItemDto, String updater) {
        // TODO: dont save short content, extract dynamically ???
        // set short content
        newsItemDto.setShortContent(extractShort(newsItemDto.getContent()));
        try {
            LegalUpdateNews entity = entityConverter.assemble(newsItemDto);
            if(newsItemDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(updater);
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.entityRepository.findOne(newsItemDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = entityRepository.findOne(newsItemDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                 entity.setUpdater(updatedby);
            }
            Long id = entityRepository.save(entity).getId();
            logger.info(newsItemDto.getId() == null ? "Legal Update created: " + id + ", " + entity.getCreator().getUsername() :
                    "Legal Update updated: " + id + ", " + entity.getUpdater().getUsername());
            return id;
        }catch(Exception ex){
            logger.error("Error saving Legal Update.", ex);
        }
        return null;
    }

    @Override
    public LegalUpdateDto get(Long id) {
        if(id != null){
            try {
                LegalUpdateNews entity = entityRepository.findOne(id);
                if (entity != null) {
                    return entityConverter.disassemble(entity);
                }
            }catch (Exception ex){
                logger.error("Error loading Legal Update by id: " + id, ex);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Long id, String username) {
        if(id == null){
            logger.info("Legal Update delete not preformed. ID is null");
            return false;
        }
        try {
            entityRepository.delete(id);
            // TODO: by who
            logger.info("Legal Update deleted: " + id + ", by " + username);
        }catch(Exception ex){
            logger.error("Legal Update delete failed: " + id, ex);
        }
        return true;
    }

    @Override
    public List<LegalUpdateDto> loadNewsShort(int pageSize) {
        List<LegalUpdateDto> newsItemsByType = loadNewsShort(0, pageSize);
        return newsItemsByType;
    }

    @Override
    public List<LegalUpdateDto> loadNewsShort(int page, int pageSize){
        List<LegalUpdateDto> newsItems = null;
        try {
            Page<LegalUpdateNews> entityPage = entityRepository.findByPage(new PageRequest(page, pageSize,
                    new Sort(Sort.Direction.DESC, "creationDate", "id")));

            if(entityPage != null && entityPage.getContent() != null){
                newsItems = new ArrayList<>();
                for(LegalUpdateNews entity: entityPage.getContent()){
                    newsItems.add(entityConverter.disassemble(entity));
                }
            }
        }catch(Exception ex){
            logger.error("Error loading Legal Update list: page=" + page + ", page size=" + pageSize, ex);
        }
        return newsItems;
    }


    private String extractShort(String htmlContent){
        if(htmlContent == null){
            return "";
        }
        String strippedContent = Jsoup.parse(htmlContent).text();
        return strippedContent.substring(0, Math.min(SHORT_NEWS_CONTENT_SIZE, strippedContent.length()));
    }
}