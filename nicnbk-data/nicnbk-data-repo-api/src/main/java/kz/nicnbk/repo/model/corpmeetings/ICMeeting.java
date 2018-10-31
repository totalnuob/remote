package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.tag.Tag;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity
@Table(name = "ic_meeting")
public class ICMeeting extends CreateUpdateBaseEntity{

    private String number;
    private Boolean closed;

    private Boolean deleted;

    @Basic
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    @Column(name="meeting_number", length=DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name="date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}