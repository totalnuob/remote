package kz.nicnbk.service.converter.tripMemo;

import kz.nicnbk.repo.model.tripMemo.TripMemo;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.tripMemo.TripMemoDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
@Component
public class TripMemoEntityConverter extends BaseDozerEntityConverter<TripMemo, TripMemoDto> {

    @Autowired
    private Mapper mapper;

    @Override
    public TripMemo assemble(TripMemoDto dto) {
        if (dto == null) {
            return null;
        }

        TripMemo tripMemo = mapper.map(dto, TripMemo.class);
        return tripMemo;
    }

    @Override
    public TripMemoDto disassemble(TripMemo entity) {
        if (entity == null) {
            return null;
        }

        TripMemoDto tripMemoDto = mapper.map(entity, TripMemoDto.class);
        return tripMemoDto;
    }

    @Override
    public List<TripMemo> assembleList(List<TripMemoDto> dtoList) {
        List<TripMemo> entities = new ArrayList<>();
        if(dtoList != null){
            for(TripMemoDto dto:dtoList){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }

    @Override
    public List<TripMemoDto> disassembleList(List<TripMemo> entityList) {
        List<TripMemoDto> tripMemoDto = new ArrayList<>();
        if(entityList != null){
            for(TripMemo entity:entityList) {
                tripMemoDto.add(disassemble(entity));
            }
        }
        return tripMemoDto;
    }
}
