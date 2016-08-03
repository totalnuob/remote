package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.RealEstateMeetingMemo;
import kz.nicnbk.service.dto.m2s2.RealEstateMeetingMemoDto;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class REMeetingMemoEntityConverter extends FundMeetingMemoConverter<RealEstateMeetingMemo, RealEstateMeetingMemoDto> {

    @Override
    public RealEstateMeetingMemo assemble(RealEstateMeetingMemoDto dto){
        RealEstateMeetingMemo entity = super.assemble(dto);
        assembleFundNonmappedFields(entity, dto);
        return entity;
    }

    @Override
    public RealEstateMeetingMemoDto disassemble(RealEstateMeetingMemo entity){
        RealEstateMeetingMemoDto dto = super.disassemble(entity);
        disassembleFundNonmappedFields(dto, entity);
        return dto;
    }
}
