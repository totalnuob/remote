package kz.nicnbk.service.converter.tripmemo;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.tripmemo.TripType;
import kz.nicnbk.repo.model.tripmemo.TripMemo;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
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
    private LookupService lookupService;

    @Override
    public TripMemo assemble(TripMemoDto dto) {
        if (dto == null) {
            return null;
        }

        TripMemo tripMemo = super.assemble(dto); //mapper.map(dto, TripMemo.class);

        // trip type
        if(StringUtils.isNotEmpty(dto.getTripType())){
            TripType tripType = lookupService.findByTypeAndCode(TripType.class, dto.getTripType());
            tripMemo.setTripType(tripType);
        }

        return tripMemo;
    }

    @Override
    public TripMemoDto disassemble(TripMemo entity) {
        if (entity == null) {
            return null;
        }
        TripMemoDto tripMemoDto = super.disassemble(entity); //mapper.map(entity, TripMemoDto.class);

        // trip type
        if(entity.getTripType() != null){
            tripMemoDto.setTripType(entity.getTripType().getCode());
        }

        // creator
        if(entity.getCreator() != null){
            tripMemoDto.setOwner(entity.getCreator().getUsername());
        }

        // updater
        if(entity.getUpdater() != null){
            tripMemoDto.setUpdater(entity.getUpdater().getUsername());
        }
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
