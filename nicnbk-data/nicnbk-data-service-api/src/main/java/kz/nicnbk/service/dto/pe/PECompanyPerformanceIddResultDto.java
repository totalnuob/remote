package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 17.10.2017.
 */
public class PECompanyPerformanceIddResultDto extends StatusResultDto {

    private List<PECompanyPerformanceIddDto> performanceIddDtoList;

    public PECompanyPerformanceIddResultDto (List<PECompanyPerformanceIddDto> performanceIddDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.performanceIddDtoList = performanceIddDtoList;
    }

    public List<PECompanyPerformanceIddDto> getPerformanceIddDtoList() {
        return performanceIddDtoList;
    }

    public void setPerformanceIddDtoList(List<PECompanyPerformanceIddDto> performanceIddDtoList) {
        this.performanceIddDtoList = performanceIddDtoList;
    }
}
