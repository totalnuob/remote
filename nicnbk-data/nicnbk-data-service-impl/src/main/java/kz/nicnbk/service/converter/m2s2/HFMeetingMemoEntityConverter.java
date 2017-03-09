package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
import kz.nicnbk.service.api.hf.HedgeFundService;
import kz.nicnbk.service.converter.hf.HFManagerEntityConverter;
import kz.nicnbk.service.converter.hf.HedgeFundEntityConverter;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import kz.nicnbk.service.dto.hf.HedgeFundDto;
import kz.nicnbk.service.dto.m2s2.HedgeFundsMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class HFMeetingMemoEntityConverter extends FundMeetingMemoConverter<HedgeFundsMeetingMemo, HedgeFundsMeetingMemoDto> {

    @Autowired
    private HedgeFundEntityConverter converter;

    @Autowired
    private HFManagerEntityConverter managerConverter;

    @Autowired
    private HedgeFundService fundService;

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

        if(entity.getFund() != null ) {
            HedgeFundDto fundDto = converter.disassemble(entity.getFund());
            dto.setFund(fundDto);
        }
        if(entity.getManager() != null) {
            HFManagerDto managerDto = managerConverter.disassemble(entity.getManager());
            dto.setManager(managerDto);
        }

        if(entity.getCreator() != null){
            dto.setOwner(entity.getCreator().getUsername());
        }

        return dto;
    }
}
