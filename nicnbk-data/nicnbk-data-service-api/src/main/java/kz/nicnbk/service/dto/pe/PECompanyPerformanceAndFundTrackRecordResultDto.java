package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 12.10.2017.
 */
public class PECompanyPerformanceAndFundTrackRecordResultDto extends PECompanyPerformanceResultDto {

    private PEFundTrackRecordDto trackRecordDTO;

    public PECompanyPerformanceAndFundTrackRecordResultDto(PEFundTrackRecordDto trackRecordDTO, List<PECompanyPerformanceDto> performanceDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(performanceDtoList, status, messageRu, messageEn, messageKz);
        this.trackRecordDTO = trackRecordDTO;
    }

    public PEFundTrackRecordDto getTrackRecordDTO() {
        return trackRecordDTO;
    }

    public void setTrackRecordDTO(PEFundTrackRecordDto trackRecordDTO) {
        this.trackRecordDTO = trackRecordDTO;
    }
}