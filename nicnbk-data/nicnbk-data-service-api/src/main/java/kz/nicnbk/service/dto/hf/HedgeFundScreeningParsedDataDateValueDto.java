package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;

import java.util.Date;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningParsedDataDateValueDto extends BaseEntityDto {

    private HedgeFundScreeningDto screening;

    private Long fundId;
    private String fundName;
    private String returnsCurrency; //Returns Currency
    private Date date;
    private Double value;

    public HedgeFundScreeningDto getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreeningDto screening) {
        this.screening = screening;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getReturnsCurrency() {
        return returnsCurrency;
    }

    public void setReturnsCurrency(String returnsCurrency) {
        this.returnsCurrency = returnsCurrency;
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

    public boolean isEmpty(){
        return this.fundId == null && StringUtils.isEmpty(this.fundName) &&
                StringUtils.isEmpty(this.returnsCurrency) && this.date != null && this.value != null;
    }
}


