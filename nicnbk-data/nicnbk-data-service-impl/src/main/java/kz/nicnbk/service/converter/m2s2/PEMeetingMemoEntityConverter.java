package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.converter.pe.PEFirmEntityConverter;
import kz.nicnbk.service.converter.pe.PEFundEntityConverter;
import kz.nicnbk.service.dto.m2s2.PrivateEquityMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class PEMeetingMemoEntityConverter extends FundMeetingMemoConverter<PrivateEquityMeetingMemo, PrivateEquityMeetingMemoDto> {

    @Autowired
    private PEFirmEntityConverter firmEntityConverter;

    @Autowired
    private PEFundEntityConverter fundEntityConverter;

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

        if(entity.getFund() != null) {
            dto.setFund(fundEntityConverter.disassemble(entity.getFund()));
        }
        if(entity.getFirm() != null) {
            dto.setFirm(firmEntityConverter.disassemble(entity.getFirm()));
        }

        return dto;
    }
}
