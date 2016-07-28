package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.service.config.ServiceBeanConfiguration;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 08.07.2016.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class MeetingMemoConverterTest {

    @Autowired
    private MeetingMemoEntityConverter converter;

    @Test
    public void testMeetingMemoEntityAssemble(){
        MeetingMemoDto dto = getTestDto();
        MeetingMemo entity = converter.assemble(dto);
        assert (checkEquals(entity, dto));
    }

    @Test
    public void testMeetingMemoEntityDisassemble(){
        MeetingMemo entity = getTestEntity();
        MeetingMemoDto dto = converter.disassemble(entity);
        assert (checkEquals(entity, dto));
    }

    private MeetingMemo getTestEntity(){
        MeetingMemo entity = new MeetingMemo();
        entity.setId(123L);

        MeetingType meetingType = new MeetingType();
        meetingType.setCode("MEETING");
        entity.setMeetingType(meetingType);

        //MemoType memoType = new MemoType();
        //memoType.setCode("GENERAL");
        //entity.setMemoType(memoType);

        entity.setOtherPartyName("Other party name");
        entity.setPurpose("Purpose");
        entity.setMeetingDate(new Date());
        entity.setMeetingLocation("Location");
        entity.setMeetingTime("12:12 AM");
        MeetingArrangedBy arrangedBy = new MeetingArrangedBy();
        arrangedBy.setCode("BY_NIC");
        entity.setArrangedBy(arrangedBy);
        entity.setArrangedByDescription("Arranged by desc");

        Set<Employee> attendees = new HashSet<>();
        Employee employee = new Employee();
        employee.setId(1234L);
        attendees.add(employee);
        entity.setAttendeesNIC(attendees);

        entity.setAttendeesNICDescription("Attendees NIC Desc");
        entity.setAttendeesOtherDescription("Attendees other desc");
        entity.setAuthor("author");

        Set<Geography> geographies = new HashSet<>();
        Geography gegraphy = new Geography();
        gegraphy.setCode("GEO1");
        geographies.add(gegraphy);
        entity.setGeographies(geographies);

        Set<Strategy> strategies = new HashSet<>();
        Strategy strategy = new Strategy();
        strategy.setCode("STRAT1");
        strategies.add(strategy);
        entity.setStrategies(strategies);

        return entity;
    }

    private MeetingMemoDto getTestDto(){
        MeetingMemoDto dto = new MeetingMemoDto();
        dto.setId(123L);
        dto.setMeetingType("MEETING");
        //dto.setMemoType("GENERAL");
        dto.setOtherPartyName("Other party name");
        dto.setPurpose("Purpose");
        dto.setMeetingDate(new Date());
        dto.setMeetingLocation("Location");
        dto.setMeetingTime("12:12 AM");
        dto.setArrangedBy("BY_NIC");
        dto.setArrangedByDescription("Arranged by desc");

        Set<EmployeeDto> attendees = new HashSet<>();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1234L);
        attendees.add(employeeDto);
        dto.setAttendeesNIC(attendees);

        dto.setAttendeesNICDescription("Attendees NIC Desc");
        dto.setAttendeesOtherDescription("Attendees other desc");
        dto.setAuthor("author");

        Set<BaseDictionaryDto> geographies = new HashSet<>();
        BaseDictionaryDto gegraphy = new BaseDictionaryDto();
        gegraphy.setCode("GEO1");
        geographies.add(gegraphy);
        dto.setGeographies(geographies);

        Set<BaseDictionaryDto> strategies = new HashSet<>();
        BaseDictionaryDto strategy = new BaseDictionaryDto();
        strategy.setCode("STRAT1");
        strategies.add(strategy);
        dto.setStrategies(strategies);

        return dto;
    }

    private boolean checkEquals(MeetingMemo entity, MeetingMemoDto dto){

        return entity.getId().longValue() == dto.getId().longValue() &&
                entity.getMeetingType().getCode().equals(dto.getMeetingType()) &&
                //entity.getMemoType().getCode().equals(dto.getMemoType()) &&
                entity.getMeetingDate().getTime() == dto.getMeetingDate().getTime();
                //entity.getArrangedBy().getCode().equals(dto.getArrangedBy()) &&
                //entity.getAttendeesNIC().size() > 0 &&
                //entity.getAttendeesNIC().size() == dto.getAttendeesNIC().size() &&
                //entity.getGeographies().size() > 0 &&
                //entity.getGeographies().size() == dto.getGeographies().size() &&
                //entity.getStrategies().size() > 0 &&
                //entity.getStrategies().size() == dto.getStrategies().size();

    }
}
