package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.hf.HFResearchPage;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Set;
import java.util.Date;

/**
 * Created by zhambyl on 12/11/2018.
 */
public class HFResearchPageDto extends CreateUpdateBaseEntityDto<HFResearchPage> {

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;
    private String topic;

    private HFManager manager;

    private Set<FilesDto> files;

    private String owner;

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

    public HFManager getManager() {
        return manager;
    }

    public void setManager(HFManager manager) {
        this.manager = manager;
    }

    public Set<FilesDto> getFiles() {
        return files;
    }

    public void setFiles(Set<FilesDto> files) {
        this.files = files;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
