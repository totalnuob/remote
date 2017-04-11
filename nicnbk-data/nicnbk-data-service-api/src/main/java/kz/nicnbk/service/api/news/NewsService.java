package kz.nicnbk.service.api.news;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.news.NewsDto;

import java.util.List;

public interface NewsService extends BaseService{

    Long save(NewsDto newsItemDto, String updater);

    NewsDto get(Long id);

    boolean delete(Long id, String username);

    List<NewsDto> loadNewsShort(int pageSize);

    List<NewsDto> loadNewsShort(String type, int page, int pageSize);
}
