package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.files.Files;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening")
public class HedgeFundScreening extends CreateUpdateBaseEntity {

    private String name;
    private String description;
    private Date date;
    private Files dataFile;
    private Files ucitsFile;

    public HedgeFundScreening(){}

    public HedgeFundScreening(Long id){
        setId(id);
    }

    @Column (name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="data_file_id")
    public Files getDataFile() {
        return dataFile;
    }

    public void setDataFile(Files dataFile) {
        this.dataFile = dataFile;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ucits_file_id")
    public Files getUcitsFile() {
        return ucitsFile;
    }

    public void setUcitsFile(Files ucitsFile) {
        this.ucitsFile = ucitsFile;
    }
}
