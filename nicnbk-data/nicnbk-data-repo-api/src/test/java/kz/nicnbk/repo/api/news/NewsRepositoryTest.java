package kz.nicnbk.repo.api.news;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import kz.nicnbk.repo.config.RepoBeanConfiguration;
import kz.nicnbk.repo.model.lookup.NewsTypeLookup;
import kz.nicnbk.repo.model.news.News;
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

/**
 * Created by magzumov on 04.07.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepoBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, /*DirtiesContextTestExecutionListener.class,*/
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Test
    @DatabaseSetup("classpath:datasets/news/news_get.xml")
    public void testNewsRepoFindByTypeWithPaging(){
        Page<News> page = newsRepository.findByType(NewsTypeLookup.GENERAL.getCode(), new PageRequest(0, 5, new Sort(Sort.Direction.DESC, "id")));
        assert(page.getContent().size() == 5);

        page = newsRepository.findByType(NewsTypeLookup.GENERAL.getCode(), new PageRequest(2, 5, new Sort(Sort.Direction.DESC, "id")));
        assert(page.getContent().size() == 1);
    }
}
