package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class PEMeetingMemoEntityConverter extends BaseDozerEntityConverter<PrivateEquityMeetingMemo, PrivateEquityMeetingMemoDto> {

    @Autowired
    private LookupTypeService lookupTypeService;

    @Override
    public PrivateEquityMeetingMemo assemble(PrivateEquityMeetingMemoDto dto){
        PrivateEquityMeetingMemo entity = super.assemble(dto);

        // TODO: refactor code duplication
        if(StringUtils.isNotEmpty(dto.getMeetingType())) {
            MeetingType meetingType = lookupTypeService.findByTypeAndCode(MeetingType.class, dto.getMeetingType());
            entity.setMeetingType(meetingType);
        }

        // set currency
        Currency currency = lookupTypeService.findByTypeAndCode(Currency.class, dto.getFundSizeCurrency());
        entity.setFundSizeCurrency(currency);
        return entity;
    }

    @Override
    public PrivateEquityMeetingMemoDto disassemble(PrivateEquityMeetingMemo entity){
        PrivateEquityMeetingMemoDto dto = super.disassemble(entity);

        // TODO: refactor code duplication
        // set meeting type
        if(entity.getMeetingType() != null){
            dto.setMeetingType(entity.getMeetingType().getCode());
        }

        // set currency
        dto.setFundSizeCurrency(entity.getFundSizeCurrency().getCode());
        return dto;
    }


    @Override
    public Class<PrivateEquityMeetingMemo> getEntityClass() {
        return PrivateEquityMeetingMemo.class;
    }

    @Override
    public Class<PrivateEquityMeetingMemoDto> getDtoClass() {
        return PrivateEquityMeetingMemoDto.class;
    }
}
