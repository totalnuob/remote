package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 12.10.2017.
 */
public class PEGrossCashflowResultDto extends StatusResultDto {

    private List<PEGrossCashflowDto> cashflowDtoList;

    public PEGrossCashflowResultDto (List<PEGrossCashflowDto> cashflowDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.cashflowDtoList = cashflowDtoList;
    }

    public List<PEGrossCashflowDto> getCashflowDtoList() {
        return cashflowDtoList;
    }

    public void setCashflowDtoList(List<PEGrossCashflowDto> cashflowDtoList) {
        this.cashflowDtoList = cashflowDtoList;
    }
}
