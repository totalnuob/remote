package kz.nicnbk.service.impl.news;

import kz.nicnbk.repo.api.news.NewsRepository;
import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.news.News;
import kz.nicnbk.service.api.news.NewsService;
import kz.nicnbk.service.converter.news.NewsEntityConverter;
import kz.nicnbk.service.dto.news.NewsDto;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    /* Max number of characters in short version of news content (excerpt) */
    private static final int SHORT_NEWS_CONTENT_SIZE = 600;

    @Autowired
    private NewsEntityConverter newsConverter;

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public Long save(NewsDto newsItemDto) {

        // TODO: dont save short content, extract dynamically
        // set short content
        newsItemDto.setShortContent(extractShort(newsItemDto.getContent()));

        News entity = newsConverter.assemble(newsItemDto);
        if(entity != null){
            Long id = newsRepository.save(entity).getId();
            return id;
        }
        return null;
    }

    @Override
    public NewsDto get(Long id) {
        if(id != null){
            News entity = newsRepository.findOne(id);
            if(entity != null){
                return newsConverter.disassemble(entity);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        if(id == null){
            return false;
        }
        newsRepository.delete(id);
        return true;
    }

    @Override
    public List<NewsDto> loadNewsShort(int pageSize) {
        List<NewsDto> newsItems = new ArrayList<>();
        for(NewsTypeLookup type: NewsTypeLookup.values()){
            newsItems.addAll(loadNewsShort(type.getCode(), 0, pageSize));
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
        List<NewsDto> newsItems = new ArrayList<>();
        Page<News> entityPage = newsRepository.findByType(type, new PageRequest(page, pageSize,
                new Sort(Sort.Direction.DESC, "id")));
        if(entityPage != null && entityPage.getContent() != null){
            for(News entity: entityPage.getContent()){
                newsItems.add(newsConverter.disassemble(entity));
            }
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