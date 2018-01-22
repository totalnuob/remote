package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 12.10.2017.
 */
public class PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto extends PEGrossCashflowResultDto {

    private List<PECompanyPerformanceIddDto> performanceIddDtoList;
    private PEFundTrackRecordDto trackRecordDTO;

    public PEGrossCashflowAndCompanyPerformanceIddAndFundTrackRecordResultDto(
            List<PECompanyPerformanceIddDto> performanceIddDtoList,
            PEFundTrackRecordDto trackRecordDTO,
            List<PEGrossCashflowDto> cashflowDtoList,
            StatusResultType status, String messageRu, String messageEn, String messageKz) {

        super(cashflowDtoList, status, messageRu, messageEn, messageKz);
        this.performanceIddDtoList = performanceIddDtoList;
        this.trackRecordDTO = trackRecordDTO;
    }

    public List<PECompanyPerformanceIddDto> getPerformanceIddDtoList() {
        return performanceIddDtoList;
    }

    public void setPerformanceIddDtoList(List<PECompanyPerformanceIddDto> performanceIddDtoList) {
        this.performanceIddDtoList = performanceIddDtoList;
    }

    public PEFundTrackRecordDto getTrackRecordDTO() {
        return trackRecordDTO;
    }

    public void setTrackRecordDTO(PEFundTrackRecordDto trackRecordDTO) {
        this.trackRecordDTO = trackRecordDTO;
    }
}
