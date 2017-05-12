package kz.nicnbk.repo.model.macromonitor;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhambyl on 29-Mar-17.
 */
@Entity(name = "macro_monitor_score")
public class MacroMonitorScore extends CreateUpdateBaseEntity {

    private Date date;
    private Double score;

    private MacroMonitorField field; //field name
    private MacroMonitorType type;  // macro monitor type: GLOBAL, US, EU, CHINA, etc.


    @Column(name="date")
    @DateTimeFormat(pattern = "MM-yyyy")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    public MacroMonitorField getField() {
        return field;
    }

    public void setField(MacroMonitorField field) {
        this.field = field;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    public MacroMonitorType getType() {
        return type;
    }

    public void setType(MacroMonitorType type) {
        this.type = type;
    }
}
