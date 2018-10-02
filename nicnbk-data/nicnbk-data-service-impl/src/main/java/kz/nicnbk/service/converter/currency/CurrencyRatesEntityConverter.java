package kz.nicnbk.service.converter.currency;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.CurrencyRates;
import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.converter.m2s2.CommonMeetingMemoConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class CurrencyRatesEntityConverter  extends BaseDozerEntityConverter<CurrencyRates, CurrencyRatesDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Override
    public CurrencyRates assemble(CurrencyRatesDto dto){
        CurrencyRates entity = super.assemble(dto);
        if (dto.getCurrency() != null && dto.getCurrency().getCode() != null) {
            Currency currency = this.lookupService.findByTypeAndCode(Currency.class, dto.getCurrency().getCode());
            entity.setCurrency(currency);
        }
        return entity;
    }

    @Override
    public CurrencyRatesDto disassemble(CurrencyRates entity){
        CurrencyRatesDto dto = super.disassemble(entity);
        if (entity.getCurrency() != null) {
            BaseDictionaryDto currencyDto = new BaseDictionaryDto(entity.getCurrency().getCode(), entity.getCurrency().getNameEn(),
                    entity.getCurrency().getNameRu(), entity.getCurrency().getNameKz());
            dto.setCurrency(currencyDto);
        }

        dto.setEditable(true);

        // Get date of most recent report with status SUBMITTED
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        // cannot delete record with date earlier than most recent SUBMITTED report date
        if(mostRecentFinalReportDate != null && entity.getDate().compareTo(mostRecentFinalReportDate) <= 0){
            dto.setEditable(false);
        }

        return dto;
    }
}
