package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.api.hf.HedgeFundScreeningRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningEntityConverter extends BaseDozerEntityConverter<HedgeFundScreening, HedgeFundScreeningDto> {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private HedgeFundScreeningRepository screeningRepository;

    @Override
    public HedgeFundScreening assemble(HedgeFundScreeningDto dto){
        HedgeFundScreening entity = super.assemble(dto);

        if(entity.getId() == null){
            // set creator
            if(dto.getCreator() != null) {
                EmployeeDto creatorDto = this.employeeService.findByUsername(dto.getCreator());
                if (creatorDto != null) {
                    entity.setCreator(new Employee(creatorDto.getId()));
                }
            }
        }else{
            // Creator
            HedgeFundScreening existingEntity = this.screeningRepository.findOne(dto.getId());
            if(existingEntity != null) {
                entity.setCreator(existingEntity.getCreator());
            }

            // Updater and Update date
            if(dto.getUpdater() != null) {
                EmployeeDto updaterDto = this.employeeService.findByUsername(dto.getUpdater());
                if(updaterDto != null) {
                    entity.setUpdater(new Employee(updaterDto.getId()));
                }
                entity.setUpdateDate(new Date());
            }
        }


        return entity;
    }

    @Override
    public HedgeFundScreeningDto disassemble(HedgeFundScreening entity){
        HedgeFundScreeningDto dto = super.disassemble(entity);
        // creator
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }
        // updater
        if(entity.getUpdater() != null){
            dto.setUpdater(entity.getUpdater().getUsername());
        }
        return dto;
    }

    public List<HedgeFundScreeningDto> disassembleList(List<HedgeFundScreening> entities){
        List<HedgeFundScreeningDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreening entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }


}
