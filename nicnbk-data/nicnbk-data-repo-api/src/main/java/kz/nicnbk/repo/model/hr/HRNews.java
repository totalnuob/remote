package kz.nicnbk.repo.model.hr;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.TypedEntity;
import kz.nicnbk.repo.model.news.NewsType;

import javax.persistence.*;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name="hr_news")
public class HRNews extends CreateUpdateBaseEntity{

    private String header;

    private String subheader;

    private String content;


    @Column(columnDefinition="TEXT")
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Column(columnDefinition="TEXT")
    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    @Column(columnDefinition="TEXT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
