package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 30/03/2018.
 */
public class PEFirmFundsAndTotalIrrResultDto extends StatusResultDto {

    private List<PEFundDto> fundDtoList;
    private Double totalIrr;

    public PEFirmFundsAndTotalIrrResultDto (List<PEFundDto> fundDtoList, Double totalIrr, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.fundDtoList = fundDtoList;
        this.totalIrr = totalIrr;
    }

    public List<PEFundDto> getFundDtoList() {
        return fundDtoList;
    }

    public void setFundDtoList(List<PEFundDto> fundDtoList) {
        this.fundDtoList = fundDtoList;
    }

    public Double getTotalIrr() {
        return totalIrr;
    }

    public void setTotalIrr(Double totalIrr) {
        this.totalIrr = totalIrr;
    }
}
