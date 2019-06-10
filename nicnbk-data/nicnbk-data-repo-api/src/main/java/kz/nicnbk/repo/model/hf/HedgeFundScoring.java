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
@Table(name = "hf_scoring")
public class HedgeFundScoring extends CreateUpdateBaseEntity {

    private HedgeFundScreeningFilteredResult filteredResult;
    private Date date;
//    private String name;
//    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="filtered_result_id", nullable = false)
    public HedgeFundScreeningFilteredResult getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(HedgeFundScreeningFilteredResult filteredResult) {
        this.filteredResult = filteredResult;
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

}
