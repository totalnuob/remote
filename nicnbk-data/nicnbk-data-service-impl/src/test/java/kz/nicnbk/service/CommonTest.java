package kz.nicnbk.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import kz.nicnbk.service.config.ServiceBeanConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * Created by magzumov on 18.07.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, /*DirtiesContextTestExecutionListener.class,*/
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class CommonTest {

    @Test
    public void dummyTest(){
        assert (true);
    }
}
