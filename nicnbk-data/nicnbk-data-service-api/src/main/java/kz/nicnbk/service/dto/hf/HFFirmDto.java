package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.hf.HFManager;

import java.util.Date;

/**
 * Created by timur on 27.10.2016.
 */
public class HFFirmDto extends HistoryBaseEntityDto<HFManager> {
    private String name;
    private Date inception;
    private Double aum;
    private String aumCurrency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getInception() {
        return inception;
    }

    public void setInception(Date inception) {
        this.inception = inception;
    }

    public Double getAum() {
        return aum;
    }

    public void setAum(Double aum) {
        this.aum = aum;
    }

    public String getAumCurrency() {
        return aumCurrency;
    }

    public void setAumCurrency(String aumCurrency) {
        this.aumCurrency = aumCurrency;
    }
}
