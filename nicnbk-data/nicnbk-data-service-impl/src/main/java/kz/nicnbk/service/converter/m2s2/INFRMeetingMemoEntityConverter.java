package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.InfrastructureMeetingMemo;
import kz.nicnbk.repo.model.m2s2.RealEstateMeetingMemo;
import kz.nicnbk.service.dto.m2s2.InfrastructureMeetingMemoDto;
import kz.nicnbk.service.dto.m2s2.RealEstateMeetingMemoDto;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class INFRMeetingMemoEntityConverter extends FundMeetingMemoConverter<InfrastructureMeetingMemo, InfrastructureMeetingMemoDto> {

    @Override
    public InfrastructureMeetingMemo assemble(InfrastructureMeetingMemoDto dto){
        InfrastructureMeetingMemo entity = super.assemble(dto);
        assembleFundNonmappedFields(entity, dto);
        return entity;
    }

    @Override
    public InfrastructureMeetingMemoDto disassemble(InfrastructureMeetingMemo entity){
        InfrastructureMeetingMemoDto dto = super.disassemble(entity);
        disassembleFundNonmappedFields(dto, entity);
        return dto;
    }
}
