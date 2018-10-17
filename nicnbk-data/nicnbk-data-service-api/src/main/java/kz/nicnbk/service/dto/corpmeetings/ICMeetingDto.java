package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.corpmeetings.CorpMeeting;
import kz.nicnbk.repo.model.corpmeetings.ICMeeting;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

/**
 * Created by magzumov on 04-Aug-16.
 */
public class ICMeetingDto extends CreateUpdateBaseEntityDto<ICMeeting> implements Comparable {

    private String number;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;
    private Boolean closed;

    private Boolean editable;

    private Set<FilesDto> materials;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Set<FilesDto> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<FilesDto> materials) {
        this.materials = materials;
    }

    @Override
    public int compareTo(Object o) {
        ICMeetingDto other = ((ICMeetingDto) o);
        if(this.number != null && other.number != null){
            return Integer.parseInt(other.number) - Integer.parseInt(this.number);
        }else{
            return 0;
        }
    }
}

