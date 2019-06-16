package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;

import java.util.Date;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningParsedDataDateValueCombinedDto extends BaseEntityDto implements Comparable {

    private HedgeFundScreeningDto screening;

    private Long fundId;
    private String fundName;
    private String returnsCurrency; //Returns Currency
    private Date[] dates;
    private Double[] values;

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

    public Date[] getDates() {
        return dates;
    }

    public void setDates(Date[] dates) {
        this.dates = dates;
    }

    public Double[] getValues() {
        return values;
    }

    public void setValues(Double[] values) {
        this.values = values;
    }

    public boolean isEmpty(){
        return this.fundId == null && StringUtils.isEmpty(this.fundName) &&
                StringUtils.isEmpty(this.returnsCurrency) &&
                this.dates != null && this.dates.length > 0 &&
                this.values != null && this.values.length > 0;
    }

    @Override
    public int compareTo(Object o) {
        long a = this.fundId != null ? this.fundId.longValue() : 0;
        long b = ((HedgeFundScreeningParsedDataDateValueCombinedDto) o).fundId != null ?
                ((HedgeFundScreeningParsedDataDateValueCombinedDto) o).fundId.longValue() : 0;
        return a - b > 0 ? 1 : a -b < 0 ? -1 : 0;
    }
}


