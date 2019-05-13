package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.hf.HedgeFundScreeningFilteredResultRepository;
import kz.nicnbk.repo.api.hf.HedgeFundScreeningRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.hf.HedgeFundScreening;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningFilteredResult;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningFilteredResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningFilteredResultEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningFilteredResult, HedgeFundScreeningFilteredResultDto> {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private HedgeFundScreeningFilteredResultRepository filteredResultRepository;

    @Override
    public HedgeFundScreeningFilteredResult assemble(HedgeFundScreeningFilteredResultDto dto){
        HedgeFundScreeningFilteredResult entity = super.assemble(dto);

        entity.setScreening(new HedgeFundScreening(dto.getScreeningId()));

        if(StringUtils.isNotEmpty(dto.getStartDateMonth())){
            Date monthDate = DateUtils.getDate("01." + dto.getStartDateMonth());
            if(monthDate != null) {
                Date lastDayDate = DateUtils.getLastDayOfCurrentMonth(monthDate);
                entity.setStartDate(lastDayDate);
            }
        }

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
            HedgeFundScreeningFilteredResult existingEntity = this.filteredResultRepository.findOne(dto.getId());
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
    public HedgeFundScreeningFilteredResultDto disassemble(HedgeFundScreeningFilteredResult entity){
        HedgeFundScreeningFilteredResultDto dto = super.disassemble(entity);

        dto.setScreeningId(entity.getScreening().getId());

        if(entity.getStartDate() != null){
            int month = DateUtils.getMonth(entity.getStartDate());
            dto.setStartDateMonth(((month + 1) < 10 ? "0" + (month + 1) : month + 1) + "." + DateUtils.getYear(entity.getStartDate()));
        }
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

    public List<HedgeFundScreeningFilteredResultDto> disassembleList(List<HedgeFundScreeningFilteredResult> entities){
        List<HedgeFundScreeningFilteredResultDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreeningFilteredResult entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }


}
