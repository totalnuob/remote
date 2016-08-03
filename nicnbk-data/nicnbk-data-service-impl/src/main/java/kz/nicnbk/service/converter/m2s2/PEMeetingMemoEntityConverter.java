package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class PEMeetingMemoEntityConverter extends FundMeetingMemoConverter<PrivateEquityMeetingMemo, PrivateEquityMeetingMemoDto> {

    @Override
    public PrivateEquityMeetingMemo assemble(PrivateEquityMeetingMemoDto dto){
        PrivateEquityMeetingMemo entity = super.assemble(dto);
        assembleFundNonmappedFields(entity, dto);
        return entity;
    }

    @Override
    public PrivateEquityMeetingMemoDto disassemble(PrivateEquityMeetingMemo entity){
        PrivateEquityMeetingMemoDto dto = super.disassemble(entity);
        disassembleFundNonmappedFields(dto, entity);
        return dto;
    }
}
