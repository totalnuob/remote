package kz.nicnbk.repo.model.legal;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import javax.persistence.*;


@Entity
@Table(name="legal_updates")
public class LegalUpdateNews extends CreateUpdateBaseEntity{

    private String header;
    private String source;
    private String content;
    // TODO: store? extract from content?
    private String shortContent;

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
