package kz.nicnbk.service.dto.hf;

import java.util.Date;

/**
 * Created by timur on 27.10.2016.
 */
public class ReturnDto {

    private Date period;
    private Double value;

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
