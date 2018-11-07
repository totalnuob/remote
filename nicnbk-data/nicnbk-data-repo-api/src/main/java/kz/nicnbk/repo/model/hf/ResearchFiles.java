package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhambyl on 06/11/2018.
 */
@Entity(name = "research_files")
public class ResearchFiles extends BaseEntity {

    private String portfolioUpdates;
    private Date updateDate;
    private HFResearch research;
    private Files file;

    public ResearchFiles(){}

    public ResearchFiles(Long researchId, Long fileId){
        HFResearch research = new HFResearch();
        research.setId(researchId);
        this.research = research;
        Files file = new Files();
        file.setId(fileId);
        this.file = file;
    }

    @Column(name = "update_date")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "portfolio_updates")
    public String getPortfolioUpdates() {
        return portfolioUpdates;
    }

    public void setPortfolioUpdates(String portfolioUpdates) {
        this.portfolioUpdates = portfolioUpdates;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "research_id", nullable = false)
    public HFResearch getResearch() {
        return research;
    }

    public void setResearch(HFResearch research) {
        this.research = research;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", nullable = false)
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }
}
