package kz.nicnbk.service.dto.hr;


import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.hr.HRNews;
import kz.nicnbk.repo.model.news.News;

import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */
public class HRNewsDto extends CreateUpdateBaseEntityDto<HRNews> {

    private String header;
    private String source;
    private String content;
    private String shortContent;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShortContent() {
        return shortContent;
    }

    public void setShortContent(String shortContent) {
        this.shortContent = shortContent;
    }
}
