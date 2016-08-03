package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.m2s2.RealEstateMeetingMemo;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.m2s2.RealEstateMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class REMeetingMemoEntityConverter extends BaseDozerEntityConverter<RealEstateMeetingMemo, RealEstateMeetingMemoDto> {

    @Autowired
    private LookupTypeService lookupTypeService;

    @Override
    public RealEstateMeetingMemo assemble(RealEstateMeetingMemoDto dto){
        RealEstateMeetingMemo entity = super.assemble(dto);

        // TODO: refactor code duplication
        if(StringUtils.isNotEmpty(dto.getMeetingType())) {
            MeetingType meetingType = lookupTypeService.findByTypeAndCode(MeetingType.class, dto.getMeetingType());
            entity.setMeetingType(meetingType);
        }

        // arranged by
        if(StringUtils.isNotEmpty(dto.getArrangedBy())){
            MeetingArrangedBy arrangedBy = lookupTypeService.findByTypeAndCode(MeetingArrangedBy.class, dto.getArrangedBy());
            entity.setArrangedBy(arrangedBy);
        }

        // set currency
        if(StringUtils.isNotEmpty(dto.getFundSizeCurrency())) {
            Currency currency = lookupTypeService.findByTypeAndCode(Currency.class, dto.getFundSizeCurrency());
            entity.setFundSizeCurrency(currency);
        }
        // strategies
        Set<Strategy> strategies = new HashSet<>();
        if(dto.getStrategies() != null){
            for(BaseDictionaryDto strategyDto: dto.getStrategies()){
                Strategy strategy = lookupTypeService.findByTypeAndCode(Strategy.class, strategyDto.getCode());
                strategies.add(strategy);
            }
        }
        entity.setStrategies(strategies);

        // geographies
        Set<Geography> geographies = new HashSet<>();
        if(dto.getGeographies() != null){
            for(BaseDictionaryDto geographyDto: dto.getGeographies()){
                Geography geography = lookupTypeService.findByTypeAndCode(Geography.class, geographyDto.getCode());
                geographies.add(geography);
            }
        }
        entity.setGeographies(geographies);

        // attendees
        if(dto.getAttendeesNIC() != null){
            Set<Employee> employees = new HashSet<>();
            for(EmployeeDto employeeDto: dto.getAttendeesNIC()){
                // TODO: check id or handle error
                Employee employee = new Employee();
                employee.setId(employeeDto.getId());
                employees.add(employee);
            }
            entity.setAttendeesNIC(employees);
        }
        return entity;
    }

    @Override
    public RealEstateMeetingMemoDto disassemble(RealEstateMeetingMemo entity){
        RealEstateMeetingMemoDto dto = super.disassemble(entity);

        // TODO: refactor code duplication
        // set meeting type
        if(entity.getMeetingType() != null){
            dto.setMeetingType(entity.getMeetingType().getCode());
        }

        // arranged by
        if(entity.getArrangedBy() != null) {
            dto.setArrangedBy(entity.getArrangedBy().getCode());
        }

        // set currency
        if(entity.getFundSizeCurrency() != null) {
            dto.setFundSizeCurrency(entity.getFundSizeCurrency().getCode());
        }

        // strategies
        Set<BaseDictionaryDto> strategies = new HashSet<>();
        if(entity.getStrategies() != null){
            for(Strategy strategy: entity.getStrategies()){
                BaseDictionaryDto strategyDto = new BaseDictionaryDto();
                strategyDto.setCode(strategy.getCode());
                strategyDto.setNameEn(strategy.getNameEn());
                strategies.add(strategyDto);
            }
        }
        dto.setStrategies(strategies);

        // geographies
        Set<BaseDictionaryDto> geographies = new HashSet<>();
        if(entity.getGeographies() != null){
            for(Geography geography: entity.getGeographies()){
                BaseDictionaryDto geographyDto = new BaseDictionaryDto();
                geographyDto.setCode(geography.getCode());
                geographyDto.setNameEn(geography.getNameEn());
                geographies.add(geographyDto);
            }
        }
        dto.setGeographies(geographies);

        // attendees
        Set<EmployeeDto> employees = new HashSet<>();
        if(entity.getAttendeesNIC() != null){
            for(Employee employee: entity.getAttendeesNIC()){
                EmployeeDto employeeDto = new EmployeeDto();
                employeeDto.setId(employee.getId());
                employees.add(employeeDto);
            }
        }
        dto.setAttendeesNIC(employees);
        return dto;
    }

    @Override
    public Class<RealEstateMeetingMemo> getEntityClass() {
        return RealEstateMeetingMemo.class;
    }

    @Override
    public Class<RealEstateMeetingMemoDto> getDtoClass() {
        return RealEstateMeetingMemoDto.class;
    }
}
