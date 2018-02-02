package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 17.01.2018.
 */
public enum PeriodicReportType {
    SUBMITTED("SUBMITTED"),
    NEW("NEW");

    PeriodicReportType(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
