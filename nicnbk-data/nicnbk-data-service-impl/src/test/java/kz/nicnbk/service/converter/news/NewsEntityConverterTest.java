package kz.nicnbk.service.converter.news;

import kz.nicnbk.repo.model.news.News;
import kz.nicnbk.repo.model.news.NewsType;
import kz.nicnbk.service.config.ServiceBeanConfiguration;
import kz.nicnbk.service.dto.news.NewsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, /*DirtiesContextTestExecutionListener.class,*/})
public class NewsEntityConverterTest {

    @Autowired
    private NewsEntityConverter converter;

    @Test
    public void testNewsEntityAssemble(){
        NewsDto dto = getTestDto();
        News entity = converter.assemble(dto);
        assert (checkEquals(entity, dto));
    }

    @Test
    public void testNewsEntityDisassemble(){
        News entity = getTestEntity();
        NewsDto dto = converter.disassemble(entity);
        assert (checkEquals(entity, dto));
    }

    private static NewsDto getTestDto(){
        NewsDto dto = new NewsDto();
        dto.setId(10L);
        dto.setContent("My test content");
        dto.setCreationDate(new Date());
        dto.setHeader("My test header");
        dto.setShortContent("My test short content");
        dto.setSource("My test source");
        dto.setType("GENERAL");
        return dto;
    }

    private static News getTestEntity(){
        News entity = new News();
        entity.setId(10L);
        entity.setContent("My test content");
        entity.setCreationDate(new Date());
        entity.setHeader("My test header");
        entity.setShortContent("My test short content");
        entity.setSource("My test source");

        NewsType type = new NewsType();
        type.setCode("GENERAL");
        type.setId(10);
        entity.setType(type);

        return entity;
    }

    private static boolean checkEquals(News entity, NewsDto dto){
        return entity.getId().longValue() == dto.getId().longValue() &&
                entity.getContent().equals(dto.getContent()) &&
                entity.getCreationDate().getTime() == dto.getCreationDate().getTime() &&
                entity.getHeader().equals(dto.getHeader()) &&
                entity.getShortContent().equals(dto.getShortContent()) &&
                entity.getSource().equals(dto.getSource()) &&
                entity.getType().getCode().equals(dto.getType());
    }

}
