package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 12.10.2017.
 */
public class PECompanyPerformanceAndFundTrackRecordResultDto extends StatusResultDto {

    private List<PECompanyPerformanceDto> performanceDtoList;

    private PEFundTrackRecordDto trackRecordDTO;

    public PECompanyPerformanceAndFundTrackRecordResultDto(List<PECompanyPerformanceDto> performanceDtoList, PEFundTrackRecordDto trackRecordDTO, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.performanceDtoList = performanceDtoList;
        this.trackRecordDTO = trackRecordDTO;
    }

    public List<PECompanyPerformanceDto> getPerformanceDtoList() {
        return performanceDtoList;
    }

    public void setPerformanceDtoList(List<PECompanyPerformanceDto> performanceDtoList) {
        this.performanceDtoList = performanceDtoList;
    }

    public PEFundTrackRecordDto getTrackRecordDTO() {
        return trackRecordDTO;
    }

    public void setTrackRecordDTO(PEFundTrackRecordDto trackRecordDTO) {
        this.trackRecordDTO = trackRecordDTO;
    }
}