package kz.nicnbk.repo.api.files;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import kz.nicnbk.repo.config.RepoBeanConfiguration;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.files.FilesType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
public class FilesRepositoryTest {

    @Autowired
    private FilesRepository filesRepository;

    @Test
    @DatabaseSetup("classpath:datasets/files/files_get.xml")
    public void testFilesRepoGet(){
        Files filesEntity = filesRepository.findOne(101L);
        assert(filesEntity.getId() == 101);
    }

    @Test
    @DatabaseSetup(value = "classpath:datasets/files/files_save.xml")
    public void testFilesRepoSave(){
        Files entity = new Files();
        entity.setFileName("dummyFileName");
        entity.setMimeType("dummyMime");
        entity.setSize(100L);

        FilesType filesType = new FilesType();
        filesType.setId(11);
        entity.setType(filesType);

        Long id = filesRepository.save(entity).getId();
        assert(id > 0);
        System.out.println("GOOD");
    }
}
