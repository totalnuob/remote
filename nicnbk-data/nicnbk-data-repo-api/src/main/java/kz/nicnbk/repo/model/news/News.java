package kz.nicnbk.repo.model.news;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.TypedEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="news")
public class News extends CreateUpdateBaseEntity implements TypedEntity<NewsType>{

    private NewsType type;

    private String header;

    private String source;

    private String content;

    // TODO: store? extract from content?
    private String shortContent;

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    public NewsType getType() {
        return type;
    }

    @Override
    public void setType(NewsType type) {
        this.type = type;
    }


    @Column(columnDefinition="TEXT")
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Column(columnDefinition="TEXT")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Column(columnDefinition="TEXT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name="short_content", columnDefinition="TEXT")
    public String getShortContent() {
        return shortContent;
    }

    public void setShortContent(String shortContent) {
        this.shortContent = shortContent;
    }

}
