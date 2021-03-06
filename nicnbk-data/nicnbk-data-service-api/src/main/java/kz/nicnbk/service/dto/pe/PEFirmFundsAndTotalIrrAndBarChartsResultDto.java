package kz.nicnbk.service.dto.pe;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 30/03/2018.
 */
public class PEFirmFundsAndTotalIrrAndBarChartsResultDto extends StatusResultDto {

    private List<PEFundDto> fundDtoList;
    private Double totalIrr;
    private byte[] barChartNetIrrBytes;
    private byte[] barChartNetMoicBytes;

    public PEFirmFundsAndTotalIrrAndBarChartsResultDto(List<PEFundDto> fundDtoList, Double totalIrr, byte[] barChartNetIrrBytes, byte[] barChartNetMoicBytes, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.fundDtoList = fundDtoList;
        this.totalIrr = totalIrr;
        this.barChartNetIrrBytes = barChartNetIrrBytes;
        this.barChartNetMoicBytes = barChartNetMoicBytes;
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

    public byte[] getBarChartNetIrrBytes() {
        return barChartNetIrrBytes;
    }

    public void setBarChartNetIrrBytes(byte[] barChartNetIrrBytes) {
        this.barChartNetIrrBytes = barChartNetIrrBytes;
    }

    public byte[] getBarChartNetMoicBytes() {
        return barChartNetMoicBytes;
    }

    public void setBarChartNetMoicBytes(byte[] barChartNetMoicBytes) {
        this.barChartNetMoicBytes = barChartNetMoicBytes;
    }
}
