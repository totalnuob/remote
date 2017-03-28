package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.HedgeFundsMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class MeetingMemoEntityConverter extends CommonMeetingMemoConverter<MeetingMemo, MeetingMemoDto> {

    @Override
    public MeetingMemo assemble(MeetingMemoDto dto){
        MeetingMemo entity = super.assemble(dto);
        assembleNonmappedFields(entity, dto);

        return entity;
    }

    @Override
    public MeetingMemoDto disassemble(MeetingMemo entity){
        MeetingMemoDto dto = super.disassemble(entity);
        disassembleNonmappedFields(dto, entity);
        return dto;
    }

    public List<MeetingMemoDto> disPE(List<PrivateEquityMeetingMemo> memos){
        List<MeetingMemoDto> list = new ArrayList<>();
        for(PrivateEquityMeetingMemo pememo: memos){
            pememo.setFirmName(pememo.getFirm() != null ? pememo.getFirm().getFirmName(): null);
            pememo.setFundName(pememo.getFund() != null ? pememo.getFund().getFundName(): null);
            MeetingMemo memo = (MeetingMemo) pememo;
            list.add(disassemble(memo));
        }
        return list;
    }

    public List<MeetingMemoDto> disHF(List<HedgeFundsMeetingMemo> memos){
        List<MeetingMemoDto> list = new ArrayList<>();
        for(HedgeFundsMeetingMemo hfmemo: memos){
            hfmemo.setFirmName(hfmemo.getManager() != null ? hfmemo.getManager().getName(): null);
            hfmemo.setFundName(hfmemo.getFund() != null ? hfmemo.getFund().getName(): null);
            MeetingMemo memo = (MeetingMemo) hfmemo;
            list.add(disassemble(memo));
        }
        return list;
    }

}
