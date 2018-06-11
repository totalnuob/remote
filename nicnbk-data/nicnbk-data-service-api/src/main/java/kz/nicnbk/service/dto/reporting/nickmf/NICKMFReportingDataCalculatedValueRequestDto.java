package kz.nicnbk.service.dto.reporting.nickmf;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public class NICKMFReportingDataCalculatedValueRequestDto implements BaseDto {

    private Long reportId;
    private String code;
    private String nameRu;

    public NICKMFReportingDataCalculatedValueRequestDto(){}

    public NICKMFReportingDataCalculatedValueRequestDto(Long reportId, String code, String nameRu){
        this.reportId = reportId;
        this.code = code;
        this.nameRu = nameRu;

    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }
}
