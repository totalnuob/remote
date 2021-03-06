package kz.nicnbk.service.converter.news;

import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.news.News;
import kz.nicnbk.repo.model.news.NewsType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.news.NewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class NewsEntityConverter extends BaseDozerEntityConverter<News, NewsDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public News assemble(NewsDto dto) {
        News entity = super.assemble(dto);
        // set type
        NewsType newsType = lookupService.findByTypeAndCode(NewsType.class, dto.getType());
        entity.setType(newsType);
        return entity;
    }

    @Override
    public NewsDto disassemble(News entity) {
        NewsDto dto = super.disassemble(entity);
        // set type
        dto.setType(entity.getType().getCode());

        if(entity.getCreator() != null){
            dto.setOwner(entity.getCreator().getUsername());
        }
        return dto;
    }
}
