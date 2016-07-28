package kz.nicnbk.repo.api.m2s2;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import kz.nicnbk.repo.config.RepoBeanConfiguration;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.m2s2.MemoFiles;
import kz.nicnbk.repo.model.m2s2.PrivateEquityMeetingMemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;

/**
 * Created by magzumov on 05.07.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepoBeanConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, /*DirtiesContextTestExecutionListener.class,*/
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class MemoFilesRepositoryTest {

    @Autowired
    private MemoFilesRepository repository;

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/memo_files_save.xml")
    public void testMemoFilesRepoSave(){
        MemoFiles memoFiles = new MemoFiles();
        PrivateEquityMeetingMemo memo = new PrivateEquityMeetingMemo();
        memo.setId(112L);
        memoFiles.setMemo(memo);
        Files files = new Files();
        files.setId(131L);
        memoFiles.setFile(files);
        Long id = repository.save(memoFiles).getId();
        assert (id > 0);
    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/memo_files_get.xml")
    public void testMemoFilesRepoGetFilesByMemoId(){
        List<MemoFiles> memoFilesList = repository.getFilesByMemoId(112L);
        assert (memoFilesList.size() == 1);
    }

    @Test
    @DatabaseSetup("classpath:datasets/m2s2/memo_files_get.xml")
    public void testMemoFilesRepoGetFilesByFileId(){
        MemoFiles memoFile = repository.getFilesByFileId(131L);
        assert (memoFile != null);
    }
}
