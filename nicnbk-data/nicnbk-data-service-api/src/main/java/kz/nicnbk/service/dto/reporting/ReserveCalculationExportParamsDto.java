package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 30.11.2017.
 */
public class ReserveCalculationExportParamsDto implements BaseDto {

    private String director;
    private String doer;
    private List<String> approveList;

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDoer() {
        return doer;
    }

    public void setDoer(String doer) {
        this.doer = doer;
    }

    public List<String> getApproveList() {
        return approveList;
    }

    public void setApproveList(List<String> approveList) {
        this.approveList = approveList;
    }
}
