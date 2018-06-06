package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by magzumov on 05.07.2016.
 */
@Entity(name="reserve_calc_files")
public class ReserveCalculationFiles extends BaseEntity{

    private ReserveCalculation entity;

    private Files file;

    public ReserveCalculationFiles(){}

    public ReserveCalculationFiles(Long entityId, Long fileId){
        ReserveCalculation entity = new ReserveCalculation();
        entity.setId(entityId);
        this.entity = entity;
        Files file = new Files();
        file.setId(fileId);
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="entity_id", nullable = false)
    public ReserveCalculation getEntity() {
        return entity;
    }

    public void setEntity(ReserveCalculation entity) {
        this.entity = entity;
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
