package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;

import java.util.Date;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFilteredResultStatisticsDto implements BaseDto {

    private Integer total;
    private Integer[][] qualified;
    private Integer[][] unqualified;
    private Integer[][] undecided;

    private HedgeFundScreeningFilteredResultDto parameters;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer[][] getQualified() {
        return qualified;
    }

    public void setQualified(Integer[][] qualified) {
        this.qualified = qualified;
    }

    public Integer[][] getUnqualified() {
        return unqualified;
    }

    public void setUnqualified(Integer[][] unqualified) {
        this.unqualified = unqualified;
    }

    public Integer[][] getUndecided() {
        return undecided;
    }

    public void setUndecided(Integer[][] undecided) {
        this.undecided = undecided;
    }

    public HedgeFundScreeningFilteredResultDto getParameters() {
        return parameters;
    }

    public void setParameters(HedgeFundScreeningFilteredResultDto parameters) {
        this.parameters = parameters;
    }
}


