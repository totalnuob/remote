package kz.nicnbk.repo.api.m2s2;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import kz.nicnbk.repo.config.RepoBeanConfiguration;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 05.07.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepoBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, /*DirtiesContextTestExecutionListener.class,*/
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class PEMeetingMemoRepositoryTest {

    @Autowired
    private PEMeetingMemoRepository repository;

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/pe_memo_save.xml")
    public void testSave(){
        PrivateEquityMeetingMemo entity = new PrivateEquityMeetingMemo();

//        MemoType memoType = new MemoType();
//        memoType.setId(2);
//        entity.setMemoType(memoType);

        MeetingType meetingType = new MeetingType();
        meetingType.setId(2);
        entity.setMeetingType(meetingType);

        entity.setMeetingDate(new Date());

        MeetingArrangedBy arrangedBy = new MeetingArrangedBy();
        arrangedBy.setId(3);
        entity.setArrangedBy(arrangedBy);

        Set<Employee> employees = new HashSet<>();
        Employee employee = new Employee();
        employee.setId(101L);
        employees.add(employee);
        entity.setAttendeesNIC(employees);

        Currency currency = new Currency();
        currency.setId(4);
        entity.setFundSizeCurrency(currency);

        Set<Geography> geographies = new HashSet<>();
        Geography geography = new Geography();
        geography.setId(5);
        geographies.add(geography);
        entity.setGeographies(geographies);

        Set<Strategy> strategies = new HashSet<>();
        Strategy strategy = new Strategy();
        strategy.setId(6);
        strategies.add(strategy);
        entity.setStrategies(strategies);

        entity.setGPAndTeamNotes("GP AND NOTES");

        Long id = repository.save(entity).getId();
        assert (id > 0);

    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/pe_memo_get.xml")
    public void testGetFullEager(){
        PrivateEquityMeetingMemo entity = repository.getFullEagerById(192L);
        entity.getArrangedBy().getCode();
        entity.getAttendeesNIC().size();
        entity.getFundSizeCurrency().getCode();
        entity.getGeographies().size();
        entity.getStrategies().size();
        entity.getMemoType();
        entity.getMeetingType().getCode();
        assert(entity != null && entity.getId() == 192);
    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/pe_memo_get.xml")
    public void testGetLazy(){
        PrivateEquityMeetingMemo entity = repository.findOne(192L);
        assert(entity != null && entity.getId() == 192);
    }
}
