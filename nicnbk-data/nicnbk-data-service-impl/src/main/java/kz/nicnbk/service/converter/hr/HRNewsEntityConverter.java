package kz.nicnbk.service.converter.hr;

import kz.nicnbk.repo.model.hr.HRNews;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hr.HRNewsDto;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class HRNewsEntityConverter extends BaseDozerEntityConverter<HRNews, HRNewsDto> {

    private static final int SHORT_NEWS_CONTENT_SIZE = 600;

    @Override
    public HRNews assemble(HRNewsDto dto) {
        HRNews entity = super.assemble(dto);

        return entity;
    }

    @Override
    public HRNewsDto disassemble(HRNews entity) {
        HRNewsDto dto = super.disassemble(entity);
        dto.setShortContent(extractShort(dto.getContent()));
        return dto;
    }

    private String extractShort(String htmlContent){
        if(htmlContent == null){
            return "";
        }
        String strippedContent = Jsoup.parse(htmlContent).text();
        return strippedContent.substring(0, Math.min(SHORT_NEWS_CONTENT_SIZE, strippedContent.length()));
    }
}
