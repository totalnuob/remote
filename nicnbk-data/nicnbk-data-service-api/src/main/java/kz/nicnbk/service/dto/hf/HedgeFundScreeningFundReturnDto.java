package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFundReturnDto extends BaseEntityDto implements Serializable, Comparable {

    private String date;
    private Double value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
        int month = Integer.parseInt(this.date.split("\\.")[0]);
        int year = Integer.parseInt(this.date.split("\\.")[1]);

        HedgeFundScreeningFundReturnDto other = (HedgeFundScreeningFundReturnDto) o;
        int monthOther = Integer.parseInt(other.date.split("\\.")[0]);
        int yearOther = Integer.parseInt(other.date.split("\\.")[1]);

        if(year < yearOther){
            return -1;
        }else if(year > yearOther){
            return 1;
        }else{
            if(month < monthOther){
                return -1;
            }else if(month > monthOther){
                return 1;
            }else{
                return 0;
            }
        }
    }
}


