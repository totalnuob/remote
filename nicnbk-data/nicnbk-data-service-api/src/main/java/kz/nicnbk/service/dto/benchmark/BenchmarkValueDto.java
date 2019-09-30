package kz.nicnbk.service.dto.benchmark;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.benchmark.BenchmarkValue;

import java.util.Date;

/**
 * Created by magzumov on 05.01.2017.
 */
public class BenchmarkValueDto extends BaseEntityDto<BenchmarkValue> implements Comparable{
    private BaseDictionaryDto benchmark;
    private Date date;
    private Double returnValue;
    private Double ytd;
//    private Double indexValue;

    public BaseDictionaryDto getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(BaseDictionaryDto benchmark) {
        this.benchmark = benchmark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Double returnValue) {
        this.returnValue = returnValue;
    }

//    public Double getIndexValue() {
//        return indexValue;
//    }
//
//    public void setIndexValue(Double indexValue) {
//        this.indexValue = indexValue;
//    }


    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    @Override
    public int compareTo(Object o) {
        return this.date.compareTo(((BenchmarkValueDto) o).date);
    }
}
