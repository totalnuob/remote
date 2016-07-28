package kz.nicnbk.repo.api.m2s2;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import kz.nicnbk.repo.config.RepoBeanConfiguration;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by magzumov on 05.07.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepoBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, /*DirtiesContextTestExecutionListener.class,*/
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class MeetingMemoRepositorySearchTest {

    @Autowired
    private MeetingMemoRepository repository;

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/meeting_memo_get.xml")
    public void testMeetingMemoRepoFindAllByDateDesc(){
        int pageNumber = 0;
        int pageSize = 2;
        Page<MeetingMemo> page = repository.findAllByOrderByMeetingDateDesc(
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
        assert(page.getTotalElements() == 3);
        assert (page.getTotalPages() == 2);
        assert(page.getContent().size() == pageSize);
    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/meeting_memo_get.xml")
    public void testMeetingMemoRepoFindWithoutDates(){
        int pageNumber = 0;
        int pageSize = 2;
        Page<MeetingMemo> page = repository.findWithoutDates("CALL", 2, "Other party 1", null,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
        assert(page.getContent().size() == 1);

        pageSize = 100; // find all
        page = repository.findWithoutDates("CALL", 2, "Other party", null,
                new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
        assert(page.getContent().size() == 3);
    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/meeting_memo_get.xml")
    public void testMeetingMemoRepoFindBothDates(){
        int pageNumber = 0;
        int pageSize = 2;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date fromDate = simpleDateFormat.parse("01.02.2016");
            Date toDate = simpleDateFormat.parse("03.03.2016");
            Page<MeetingMemo> page = repository.findBothDates("CALL", 2, null, null, fromDate, toDate,
                    new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "meetingDate", "id")));
            assert(page.getContent().size() == 2);
        } catch (ParseException e) {
            e.printStackTrace();
            assert (false);
        }
    }
}
