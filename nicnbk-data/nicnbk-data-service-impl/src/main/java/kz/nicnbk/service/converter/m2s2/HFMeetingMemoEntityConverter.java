package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
import kz.nicnbk.service.dto.m2s2.HedgeFundsMeetingMemoDto;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class HFMeetingMemoEntityConverter extends FundMeetingMemoConverter<HedgeFundsMeetingMemo, HedgeFundsMeetingMemoDto> {

    @Override
    public HedgeFundsMeetingMemo assemble(HedgeFundsMeetingMemoDto dto){
        HedgeFundsMeetingMemo entity = super.assemble(dto);
        assembleFundNonmappedFields(entity, dto);
        return entity;
    }

    @Override
    public HedgeFundsMeetingMemoDto disassemble(HedgeFundsMeetingMemo entity){
        HedgeFundsMeetingMemoDto dto = super.disassemble(entity);
        disassembleFundNonmappedFields(dto, entity);
        return dto;
    }
}
