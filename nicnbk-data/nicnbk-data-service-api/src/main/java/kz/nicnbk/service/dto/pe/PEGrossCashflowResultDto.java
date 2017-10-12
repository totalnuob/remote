package kz.nicnbk.service.dto.pe;

import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import kz.nicnbk.service.dto.common.StatusResultDto;

import java.util.List;

/**
 * Created by Pak on 12.10.2017.
 */
public class PEGrossCashflowResultDto extends StatusResultDto {

    private List<PEGrossCashflowDto> cashflowDtoList;

    public List<PEGrossCashflowDto> getCashflowDtoList() {
        return cashflowDtoList;
    }

    public void setCashflowDtoList(List<PEGrossCashflowDto> cashflowDtoList) {
        this.cashflowDtoList = cashflowDtoList;
    }
}
