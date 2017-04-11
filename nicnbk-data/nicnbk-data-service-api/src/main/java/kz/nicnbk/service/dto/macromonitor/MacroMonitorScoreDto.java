package kz.nicnbk.service.dto.macromonitor;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorField;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorScore;
import kz.nicnbk.repo.model.macromonitor.MacroMonitorType;

import java.util.Date;

/**
 * Created by zhambyl on 29-Mar-17.
 */
public class MacroMonitorScoreDto extends HistoryBaseEntityDto<MacroMonitorScore> {

    private Date date;
    private Double score;

    private String field; //field name
    private String type;  // macro monitor type: GLOBAL, US, EU, CHINA, etc.

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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
