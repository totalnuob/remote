package kz.nicnbk.service.impl.news;

import kz.nicnbk.service.CommonTest;
import kz.nicnbk.service.api.news.NewsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by magzumov on 18.07.2016.
 */

public class NewsServiceTest extends CommonTest{

    @Autowired
    private NewsService newsService;

    @Test
    public void testContext(){
        assert (newsService != null);
    }
}
