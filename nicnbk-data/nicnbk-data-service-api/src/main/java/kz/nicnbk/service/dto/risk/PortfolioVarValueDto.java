package kz.nicnbk.service.dto.risk;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.risk.PortfolioVarValue;

import java.util.Date;

public class PortfolioVarValueDto extends BaseEntityDto<PortfolioVarValue> implements Comparable {
    private BaseDictionaryDto portfolioVar;
    private Date date;
    private Double value;

    public BaseDictionaryDto getPortfolioVar() {
        return portfolioVar;
    }

    public void setPortfolioVar(BaseDictionaryDto portfolioVar) {
        this.portfolioVar = portfolioVar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        return this.date.compareTo(((PortfolioVarValueDto) o).date);
    }
}
