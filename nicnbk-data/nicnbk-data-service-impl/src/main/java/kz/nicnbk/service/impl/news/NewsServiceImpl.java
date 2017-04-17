package kz.nicnbk.service.impl.news;

import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.news.NewsRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.news.News;
import kz.nicnbk.service.api.news.NewsService;
import kz.nicnbk.service.converter.news.NewsEntityConverter;
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
public class NewsServiceImpl implements NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    /* Max number of characters in short version of news content (excerpt) */
    private static final int SHORT_NEWS_CONTENT_SIZE = 600;

    @Autowired
    private NewsEntityConverter newsConverter;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Long save(NewsDto newsItemDto, String updater) {
        // TODO: dont save short content, extract dynamically ???

        // set short content
        newsItemDto.setShortContent(extractShort(newsItemDto.getContent()));
        try {
            News entity = newsConverter.assemble(newsItemDto);
            if(newsItemDto.getId() == null){ // CREATE
                Employee employee = this.employeeRepository.findByUsername(newsItemDto.getOwner());
                // set creator
                entity.setCreator(employee);
            }else{ // UPDATE
                // set creator
                Employee employee = this.newsRepository.findOne(newsItemDto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = newsRepository.findOne(newsItemDto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                 entity.setUpdater(updatedby);
            }
            Long id = newsRepository.save(entity).getId();
            logger.info(newsItemDto.getId() == null ? "News created: " + id + ", " + entity.getCreator().getUsername() :
                    "News updated: " + id + ", " + entity.getUpdater().getUsername());
            return id;
        }catch(Exception ex){
            logger.error("Error saving news.", ex);
        }
        return null;
    }

    @Override
    public NewsDto get(Long id) {
        if(id != null){
            try {
                News entity = newsRepository.findOne(id);
                if (entity != null) {
                    return newsConverter.disassemble(entity);
                }
            }catch (Exception ex){
                logger.error("Error loading news by id: " + id, ex);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Long id, String username) {
        if(id == null){
            logger.info("News delete not preformed. ID is null");
            return false;
        }
        try {
            newsRepository.delete(id);
            // TODO: by who
            logger.info("News deleted: " + id + ", by " + username);
        }catch(Exception ex){
            logger.error("News delete failed: " + id, ex);
        }
        return true;
    }

    @Override
    public List<NewsDto> loadNewsShort(int pageSize) {
        List<NewsDto> newsItems = new ArrayList<>();
        for(NewsTypeLookup type: NewsTypeLookup.values()){
            List<NewsDto> newsItemsByType = loadNewsShort(type.getCode(), 0, pageSize);
            if(newsItemsByType != null){
                newsItems.addAll(newsItemsByType);
            }else{
                // error occurred, already logged in method call
                return null;
            }
        }
        return newsItems;
    }

    /**
     * Returns short version (short content) news of the specified category for the specified page in a paginated view.
     *
     * @param type - category
     * @param page - page number
     * @return
     */
    @Override
    public List<NewsDto> loadNewsShort(String type, int page, int pageSize){
        List<NewsDto> newsItems = null;
        try {
            Page<News> entityPage = newsRepository.findByType(type, new PageRequest(page, pageSize,
                    new Sort(Sort.Direction.DESC, "creationDate", "id")));

            if(entityPage != null && entityPage.getContent() != null){
                newsItems = new ArrayList<>();
                for(News entity: entityPage.getContent()){
                    newsItems.add(newsConverter.disassemble(entity));
                }
            }
        }catch(Exception ex){
            logger.error("Error loading news list: type=" + type + ", page=" + page + ", pagesize=" + pageSize, ex);
        }
        return newsItems;
    }


    /**
     * Returns short version, i.e. without html tags, of the news content.
     * Uses jsoup library.
     *
     * @param htmlContent - html string
     * @return
     */
    private String extractShort(String htmlContent){
        if(htmlContent == null){
            return "";
        }
        String strippedContent = Jsoup.parse(htmlContent).text();
        return strippedContent.substring(0, Math.min(SHORT_NEWS_CONTENT_SIZE, strippedContent.length()));
    }

}