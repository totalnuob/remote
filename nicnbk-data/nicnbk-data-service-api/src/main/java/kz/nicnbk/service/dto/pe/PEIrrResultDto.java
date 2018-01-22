package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

/**
 * Created by Pak on 09/01/2018.
 */
public class PEIrrResultDto extends StatusResultDto {

    private Double irr;

    public PEIrrResultDto(Double irr, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.irr = irr;
    }

    public Double getIrr() {
        return irr;
    }

    public void setIrr(Double irr) {
        this.irr = irr;
    }
}
