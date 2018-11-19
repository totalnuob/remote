package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by zhambyl on 12/11/2018.
 */
@Entity(name = "hf_research_page_files")
public class HFResearchPageFiles extends BaseEntity{

    private HFResearchPage researchPage;

    private Files file;

    public HFResearchPageFiles(){}

    public HFResearchPageFiles(Long researchPageId, Long fileId){
        HFResearchPage researchPage = new HFResearchPage();
        researchPage.setId(researchPageId);
        this.researchPage = researchPage;
        Files file = new Files();
        file.setId(fileId);
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hf_research_page_id", nullable = false)
    public HFResearchPage getResearchPage() {
        return researchPage;
    }

    public void setResearchPage(HFResearchPage researchPage) {
        this.researchPage = researchPage;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="file_id", nullable = false)
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }
}
