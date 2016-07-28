package kz.nicnbk.service.dto.news;


import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.news.News;

import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */
public class NewsDto extends BaseEntityDto<News> {

    private String type;
    private String header;
    private String source;
    private String content;
    private String shortContent;
    private Date creationDate;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
