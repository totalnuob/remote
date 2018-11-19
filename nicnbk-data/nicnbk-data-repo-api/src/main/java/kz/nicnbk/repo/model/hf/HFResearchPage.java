package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.files.Files;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhambyl on 12/11/2018.
 */
@Entity
@Table(name = "hf_research_page")
public class HFResearchPage extends CreateUpdateBaseEntity {

    private Date date;
    private String topic;

    private HFManager manager;


    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    public HFManager getManager() {
        return manager;
    }

    public void setManager(HFManager manager) {
        this.manager = manager;
    }
}
