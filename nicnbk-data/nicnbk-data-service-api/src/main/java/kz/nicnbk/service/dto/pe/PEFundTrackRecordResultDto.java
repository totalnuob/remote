package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

/**
 * Created by Pak on 06.10.2017.
 */
public class PEFundTrackRecordResultDto extends StatusResultDto {

    private PEFundTrackRecordDto trackRecordDTO;

    public PEFundTrackRecordResultDto(PEFundTrackRecordDto trackRecordDTO, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.trackRecordDTO = trackRecordDTO;
    }

    public PEFundTrackRecordDto getTrackRecordDTO() {
        return trackRecordDTO;
    }

    public void setTrackRecordDTO(PEFundTrackRecordDto trackRecordDTO) {
        this.trackRecordDTO = trackRecordDTO;
    }
}
