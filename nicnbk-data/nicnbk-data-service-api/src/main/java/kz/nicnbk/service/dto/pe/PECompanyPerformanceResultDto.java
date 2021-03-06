package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 12.10.2017.
 */
public class PECompanyPerformanceResultDto extends StatusResultDto {

    private List<PECompanyPerformanceDto> performanceDtoList;

    public PECompanyPerformanceResultDto (List<PECompanyPerformanceDto> performanceDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.performanceDtoList = performanceDtoList;
    }

    public List<PECompanyPerformanceDto> getPerformanceDtoList() {
        return performanceDtoList;
    }

    public void setPerformanceDtoList(List<PECompanyPerformanceDto> performanceDtoList) {
        this.performanceDtoList = performanceDtoList;
    }
}
